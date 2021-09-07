package com.coding.challenge.controller;

import com.coding.challenge.dto.Stock;
import com.coding.challenge.event.UploadProgressEventProcessor;
import com.coding.challenge.service.StockService;
import com.coding.challenge.utility.StockMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * This class acts as the endpoint of the api calls that will enable the upload
 * of bulk data, query data using ticker and also enable addition of new records.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;
    private final UploadProgressEventProcessor uploadProgressEventProcessor;

    /**
     * This method handles the bulk upload of stocks within a csv file.
     *
     * @param filePartMono represents the uploaded file
     * @return tickers that were in the uploaded file.
     */
    @Operation(summary = "Bulk upload a csv file with stock data.")
    @ApiResponses(value = {@ApiResponse(responseCode = "202", description = "Uploaded the file for processing.",content = {@Content(mediaType = "application/json",schema = @Schema(implementation = Mono.class))})})
    @PostMapping(value = "/stocks", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<String> upload(@Parameter(description = "file to be uploaded") @RequestPart("file") Mono<FilePart> filePartMono) {
        return filePartMono.flatMap(stockService::save);
    }

    /**
     * This method performs stock search based on the specified ticker.
     *
     * @param ticker to use for the search
     * @return stocks matching specified ticker
     */
    @Operation(summary = "Search for a given stock using ticker value.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Search ran successfully.",content = {@Content(mediaType = "application/json",schema = @Schema(implementation = Flux.class))})})
    @GetMapping(value = "/stock/{ticker}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Flux<com.coding.challenge.model.Stock> findByTicker(@Parameter(description = "ticker to use for search") @PathVariable String ticker) {
        return stockService.findByTicker(ticker);
    }

    /**
     * This method saves the specified stock information.
     *
     * @return stock once saved
     */
    @Operation(summary = "Create a new stock record.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Record inserted successfully.",content = {@Content(mediaType = "application/json",schema = @Schema(implementation = Mono.class))})})
    @PostMapping(value = "/stock", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Mono<com.coding.challenge.model.Stock> save(@Parameter(description = "details of stock to be inserted") @RequestBody Mono<Stock> stockDTO) {
        return stockDTO.flatMap(s -> stockService.saveOrUpdate(StockMapper.toStock(s)));
    }

    /**
     * This method publishes status information regarding the upload request id specified.
     *
     * @param requestId to get status about
     * @return status of upload
     */
    @Operation(summary = "Report on the status of the file upload.", hidden = true)
    @GetMapping(path = "/status/{requestId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getUploadStatus(@PathVariable String requestId) {
        return uploadProgressEventProcessor.start(requestId);
    }
}
