package com.coding.challenge.service;

import com.coding.challenge.model.Stock;
import com.coding.challenge.repository.StockRepository;
import com.coding.challenge.utility.FileHelper;
import com.coding.challenge.utility.StockMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final FileHelper fileHelper;
    private final StockRepository stockRepository;

    public Mono<String> save(FilePart filePart) {

        final String requestId = UUID.randomUUID().toString();
        final String extension = FilenameUtils.getExtension(filePart.filename());
        final String baseName = FilenameUtils.getBaseName(filePart.filename());
        final String format = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);

        log.info("[RequestId={}] File received : [{}]", requestId, filePart.filename());

        return fileHelper.createTempFile(String.format("%s-%s", baseName, format), extension)
                .flatMap(t -> filePart.transferTo(t).doOnSuccess(v -> fileHelper.processFile(requestId, t.toFile())))
                .map(v -> requestId);
    }

    public Flux<Stock> findByTicker(String ticker) {
        return stockRepository.findStockByTicker(ticker);
    }

    public Mono<Stock> saveOrUpdate(Stock stock) {

        Mono<Stock> matchingStock = stockRepository.findStockByTickerAndStockDate(stock.getTicker(), stock.getStockDate());

        Mono<Stock> savedStock = matchingStock.flatMap(ms -> {
            StockMapper.toStock(stock, ms);
            return stockRepository.save(ms)
                    .doOnSuccess(s -> log.info("Updated [Ticker={}] for [Date={}]", s.getTicker(), s.getStockDate()))
                    .doOnError(e -> log.error(String.format("Could not save [Ticker=%s]", ms.getTicker()), e));
        }).switchIfEmpty(stockRepository
                .save(stock.setAsNew())
                .doOnSuccess(s -> log.info("Saved [Ticker={}] for [Date={}]", s.getTicker(), s.getStockDate()))
                .doOnError(e -> log.error(String.format("Could not save [Ticker=%s]", stock.getTicker()), e))
        );

        return savedStock;
    }
}
