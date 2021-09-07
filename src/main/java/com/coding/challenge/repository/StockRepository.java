package com.coding.challenge.repository;

import com.coding.challenge.model.Stock;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface StockRepository extends ReactiveCrudRepository<Stock, Long> {
    Mono<Stock> findStockByTickerAndStockDate(String ticker, LocalDate stockDate);
    Flux<Stock> findStockByTicker(String ticker);
}
