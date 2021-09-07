package com.coding.challenge.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class Stock implements Serializable {

    private String stock;
    private String quarter;
    private String date;
    private String open;
    private String high;
    private String low;
    private String close;
    private String volume;
    private String percentChangePrice;
    private String percentChangeVolumeOverLastWeek;
    private String previousWeeksVolume;
    private String nextWeeksOpen;
    private String nextWeeksClose;
    private String percentChangeNextWeeksPrice;
    private String daysToNextDividend;
    private String percentReturnNextDividend;
}
