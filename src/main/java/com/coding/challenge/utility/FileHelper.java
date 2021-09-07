package com.coding.challenge.utility;

import com.coding.challenge.dto.LineResult;
import com.coding.challenge.dto.UploadResult;
import com.coding.challenge.dto.ValidationError;
import com.coding.challenge.event.UploadProgressEvent;
import com.coding.challenge.exception.UnparseableFileException;
import com.coding.challenge.model.Stock;
import com.coding.challenge.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.BaseStream;

/**
 * This class contains helper methods to deal with the uploaded
 * file in a non-blocking reactive way.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileHelper {

    private final StockRepository stockRepository;
    private final ApplicationEventPublisher publisher;

    /**
     * This method is used to create a new temp file using specified prefix and suffix: As the
     * <code>createTempFile</code> is blocking, it is wrapped in a <code>Mono.defer</code>
     *
     * @param prefix to create temp file
     * @param suffix to create temp file
     * @return path to temp file
     */
    public Mono<Path> createTempFile(final String prefix, final String suffix) {
        return Mono.defer(() -> {
            try {
                return Mono.just(Files.createTempFile(prefix, suffix));
            } catch (IOException ex) {
                return Mono.error(ex);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * This method is used to process the uploaded file line by line.
     *
     * @param requestId unique identifier of the upload
     * @param file to be processed
     */
    public void processFile(final String requestId, final File file) {
        ingestFile(requestId, file).subscribe(r -> log.info("[RequestId={}] Final Result => [{}]", requestId, r));
    }

    private Mono<UploadResult> ingestFile(final String requestId, final File file) {

        publisher.publishEvent(new UploadProgressEvent(requestId, Boolean.FALSE, "started"));

        return indexedLines(file)
                .skip(1)    // Ignore the header line.
                .map(this::mapStock)
                .map(this::validateStock)
                .transform(this::insertStock)
                .transform(this::handleException)
                .reduce(new UploadResult(file), (fileResult, lineResult) -> {

                    publisher.publishEvent(new UploadProgressEvent(requestId, Boolean.FALSE, fileResult.toString()));

                            return fileResult.accumulate(
                                    lineResult.getValidationErrors(),
                                    lineResult.isInsertSucceed() != null && lineResult.isInsertSucceed(),
                                    lineResult.getException()
                            );

                        }
                ).doOnTerminate(() -> publisher.publishEvent(new UploadProgressEvent(requestId, Boolean.TRUE, "completed")));
    }

    private Flux<Tuple2<Long, String>> indexedLines(final File file) {
        return Flux.using(
                () -> Files.lines(file.toPath()),
                lines -> Flux.fromStream(lines).index(),
                BaseStream::close
        );
    }

    private LineResult mapStock(Tuple2<Long, String> indexedLine) {
        try {
            CSVParser csvParser = CSVParser.parse(
                    indexedLine.getT2(),
                    CSVFormat.DEFAULT.withHeader(
                            StockMapper.QUARTER,
                            StockMapper.STOCK,
                            StockMapper.DATE,
                            StockMapper.OPEN,
                            StockMapper.HIGH,
                            StockMapper.LOW,
                            StockMapper.CLOSE,
                            StockMapper.VOLUME,
                            StockMapper.PERCENT_CHANGE_PRICE,
                            StockMapper.PERCENT_CHANGE_VOLUME_OVER_LAST_WK,
                            StockMapper.PREVIOUS_WEEKS_VOLUME,
                            StockMapper.NEXT_WEEKS_OPEN,
                            StockMapper.NEXT_WEEKS_CLOSE,
                            StockMapper.PERCENT_CHANGE_NEXT_WEEKS_PRICE,
                            StockMapper.DAYS_TO_NEXT_DIVIDEND,
                            StockMapper.PERCENT_RETURN_NEXT_DIVIDEND
                    ).withTrim()
            );
            return csvParser
                    .getRecords()
                    .stream()
                    .findFirst()
                    .map(r -> new LineResult(indexedLine.getT1(), StockMapper.toStock(r)))
                    .orElse(null);
        } catch (Exception e) {
            throw new UnparseableFileException(indexedLine.getT1(), e);
        }
    }

    private Flux<LineResult> insertStock(final Flux<LineResult> stream) {
        return stream.map(this::insertStock).flatMap(lineResultMono -> lineResultMono);
    }

    private Mono<LineResult> insertStock(LineResult lineResult) {
        if (lineResult.hasValidationError()) {
            return Mono.just(lineResult);
        } else {
            return stockRepository
                    .save(lineResult.getStock())
                    .flatMap(savedStock -> Mono.just(new LineResult(
                            lineResult.getLineNumber(),
                            savedStock,
                            null,
                            savedStock.getId() != null && savedStock.getId() > 0L,
                            null
                    )));
        }
    }

    public LineResult validateStock(final LineResult lineResult) {

        final List<ValidationError> errors = new ArrayList<>();
        final Long lineNumber = lineResult.getLineNumber();
        final Stock stock = lineResult.getStock();

        if (stock.getQuarter() <= 0) {
            errors.add(new ValidationError("Quarter", "Quarter must be a positive, non-zero value."));
        }

        if (!StringUtils.hasText(stock.getTicker())) {
            errors.add(new ValidationError("Ticker", "Ticker must be specified."));
        }

        if (stock.getStockDate() == null) {
            errors.add(new ValidationError("Date", "Date must be specified."));
        }

        return new LineResult(lineNumber, stock, errors);
    }

    private Flux<LineResult> handleException(final Flux<LineResult> stream) {
        return stream.onErrorResume(ex -> Flux.just(new LineResult(ex)));
    }
}
