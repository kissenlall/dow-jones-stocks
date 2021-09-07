package com.coding.challenge.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a given stock record.
 */
@Data
@Table("stock")
public class Stock implements Persistable<Long> {

    @Id
    private Long id;

    private int quarter;
    private String ticker;
    private LocalDate stockDate;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private Long volume;
    private Double percentChangePrice;
    private Double percentChangeVolumeOverLastWeek;
    private Long previousWeeksVolume;
    private BigDecimal nextWeeksOpen;
    private BigDecimal nextWeeksClose;
    private Double percentChangeNextWeeksPrice;
    private int daysToNextDividend;
    private Double percentReturnNextDividend;

    public Stock(int quarter,
                 String ticker,
                 LocalDate stockDate,
                 BigDecimal open,
                 BigDecimal high,
                 BigDecimal low,
                 BigDecimal close,
                 Long volume,
                 Double percentChangePrice,
                 Double percentChangeVolumeOverLastWeek,
                 Long previousWeeksVolume,
                 BigDecimal nextWeeksOpen,
                 BigDecimal nextWeeksClose,
                 Double percentChangeNextWeeksPrice,
                 int daysToNextDividend,
                 Double percentReturnNextDividend) {

        this.quarter = quarter;
        this.ticker = ticker;
        this.stockDate = stockDate;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.percentChangePrice = percentChangePrice;
        this.percentChangeVolumeOverLastWeek = percentChangeVolumeOverLastWeek;
        this.previousWeeksVolume = previousWeeksVolume;
        this.nextWeeksOpen = nextWeeksOpen;
        this.nextWeeksClose = nextWeeksClose;
        this.percentChangeNextWeeksPrice = percentChangeNextWeeksPrice;
        this.daysToNextDividend = daysToNextDividend;
        this.percentReturnNextDividend = percentReturnNextDividend;
    }

    @Transient
    private boolean newStock;

    @Override
    @Transient
    public boolean isNew() {
        return this.newStock || id == null;
    }

    public Stock setAsNew(){
        this.newStock = true;
        return this;
    }
}
