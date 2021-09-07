package com.coding.challenge.utility;

import com.coding.challenge.dto.Stock;
import org.apache.commons.csv.CSVRecord;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * This class is responsible for transforming specified objects into new instances of {@link com.coding.challenge.model.Stock}
 */
public class StockMapper {

    public static final String QUARTER = "quarter";
    public static final String STOCK = "stock";
    public static final String DATE = "date";
    public static final String OPEN = "open";
    public static final String HIGH = "high";
    public static final String LOW = "low";
    public static final String CLOSE = "close";
    public static final String VOLUME = "volume";
    public static final String PERCENT_CHANGE_PRICE = "percent_change_price";
    public static final String PERCENT_CHANGE_VOLUME_OVER_LAST_WK = "percent_change_volume_over_last_wk";
    public static final String PREVIOUS_WEEKS_VOLUME = "previous_weeks_volume";
    public static final String NEXT_WEEKS_OPEN = "next_weeks_open";
    public static final String NEXT_WEEKS_CLOSE = "next_weeks_close";
    public static final String PERCENT_CHANGE_NEXT_WEEKS_PRICE = "percent_change_next_weeks_price";
    public static final String DAYS_TO_NEXT_DIVIDEND = "days_to_next_dividend";
    public static final String PERCENT_RETURN_NEXT_DIVIDEND = "percent_return_next_dividend";

    public static void toStock( com.coding.challenge.model.Stock fromStock, com.coding.challenge.model.Stock toStock ){
        toStock.setQuarter(fromStock.getQuarter());
        toStock.setOpen(fromStock.getOpen());
        toStock.setHigh(fromStock.getHigh());
        toStock.setLow(fromStock.getLow());
        toStock.setClose(fromStock.getClose());
        toStock.setVolume(fromStock.getVolume());
        toStock.setPercentChangePrice(fromStock.getPercentChangePrice());
        toStock.setPercentChangeVolumeOverLastWeek(fromStock.getPercentChangeVolumeOverLastWeek());
        toStock.setPreviousWeeksVolume(fromStock.getPreviousWeeksVolume());
        toStock.setNextWeeksOpen(fromStock.getNextWeeksOpen());
        toStock.setNextWeeksClose(fromStock.getNextWeeksClose());
        toStock.setPercentChangeNextWeeksPrice(fromStock.getPercentChangeNextWeeksPrice());
        toStock.setDaysToNextDividend(fromStock.getDaysToNextDividend());
        toStock.setPercentReturnNextDividend(fromStock.getPercentReturnNextDividend());
    }

    /**
     * Converts {@link Stock} to {@link com.coding.challenge.model.Stock}
     *
     * @param stock to be converted
     * @return converted {@link com.coding.challenge.model.Stock}
     */
    public static com.coding.challenge.model.Stock toStock(Stock stock) {
        return new com.coding.challenge.model.Stock(
                StringUtils.hasText(stock.getQuarter()) ? Integer.parseInt(stock.getQuarter()) : -1,
                stock.getStock(),
                StringUtils.hasText(stock.getDate()) ? LocalDate.parse(stock.getDate(), DateTimeFormatter.ofPattern("M/d/yyyy")) : null,
                StringUtils.hasText(stock.getOpen()) ? new BigDecimal(stock.getOpen().replace("$", "")) : null,
                StringUtils.hasText(stock.getHigh()) ? new BigDecimal(stock.getHigh().replace("$", "")) : null,
                StringUtils.hasText(stock.getLow()) ? new BigDecimal(stock.getLow().replace("$", "")) : null,
                StringUtils.hasText(stock.getClose()) ? new BigDecimal(stock.getClose().replace("$", "")) : null,
                StringUtils.hasText(stock.getVolume()) ? Long.parseLong(stock.getVolume()) : null,
                StringUtils.hasText(stock.getPercentChangePrice()) ? Double.parseDouble(stock.getPercentChangePrice()) : null,
                StringUtils.hasText(stock.getPercentChangeVolumeOverLastWeek()) ? Double.parseDouble(stock.getPercentChangeVolumeOverLastWeek()) : null,
                StringUtils.hasText(stock.getPreviousWeeksVolume()) ? Long.parseLong(stock.getPreviousWeeksVolume()) : null,
                StringUtils.hasText(stock.getNextWeeksOpen()) ? new BigDecimal(stock.getNextWeeksOpen().replace("$", "")) : null,
                StringUtils.hasText(stock.getNextWeeksClose()) ? new BigDecimal(stock.getNextWeeksClose().replace("$", "")) : null,
                StringUtils.hasText(stock.getPercentChangeNextWeeksPrice()) ? Double.parseDouble(stock.getPercentChangeNextWeeksPrice()) : null,
                StringUtils.hasText(stock.getDaysToNextDividend()) ? Integer.parseInt(stock.getDaysToNextDividend()) : 0,
                StringUtils.hasText(stock.getPercentReturnNextDividend()) ? Double.parseDouble(stock.getPercentReturnNextDividend()) : null

        );
    }

    /**
     * Converts {@link CSVRecord} to {@link com.coding.challenge.model.Stock}
     *
     * @param csvRecord to be converted
     * @return converted {@link com.coding.challenge.model.Stock}
     */
    public static com.coding.challenge.model.Stock toStock(CSVRecord csvRecord)
    {
        return new com.coding.challenge.model.Stock(
                StringUtils.hasText(csvRecord.get(QUARTER)) ? Integer.parseInt(csvRecord.get(QUARTER)) : -1,
                csvRecord.get(STOCK),
                StringUtils.hasText(csvRecord.get(DATE)) ? LocalDate.parse(csvRecord.get(DATE), DateTimeFormatter.ofPattern("M/d/yyyy")) : null,
                StringUtils.hasText(csvRecord.get(OPEN)) ? new BigDecimal(csvRecord.get(OPEN).replace("$", "")) : null,
                StringUtils.hasText(csvRecord.get(HIGH)) ? new BigDecimal(csvRecord.get(HIGH).replace("$", "")) : null,
                StringUtils.hasText(csvRecord.get(LOW)) ? new BigDecimal(csvRecord.get(LOW).replace("$", "")) : null,
                StringUtils.hasText(csvRecord.get(CLOSE)) ? new BigDecimal(csvRecord.get(CLOSE).replace("$", "")) : null,
                StringUtils.hasText(csvRecord.get(VOLUME)) ? Long.parseLong(csvRecord.get(VOLUME)) : null,
                StringUtils.hasText(csvRecord.get(PERCENT_CHANGE_PRICE)) ? Double.parseDouble(csvRecord.get(PERCENT_CHANGE_PRICE)) : null,
                StringUtils.hasText(csvRecord.get(PERCENT_CHANGE_VOLUME_OVER_LAST_WK)) ? Double.parseDouble(csvRecord.get(PERCENT_CHANGE_VOLUME_OVER_LAST_WK)) : null,
                StringUtils.hasText(csvRecord.get(PREVIOUS_WEEKS_VOLUME)) ? Long.parseLong(csvRecord.get(PREVIOUS_WEEKS_VOLUME)) : null,
                StringUtils.hasText(csvRecord.get(NEXT_WEEKS_OPEN)) ? new BigDecimal(csvRecord.get(NEXT_WEEKS_OPEN).replace("$", "")) : null,
                StringUtils.hasText(csvRecord.get(NEXT_WEEKS_CLOSE)) ? new BigDecimal(csvRecord.get(NEXT_WEEKS_CLOSE).replace("$", "")) : null,
                StringUtils.hasText(csvRecord.get(PERCENT_CHANGE_NEXT_WEEKS_PRICE)) ? Double.parseDouble(csvRecord.get(PERCENT_CHANGE_NEXT_WEEKS_PRICE)) : null,
                StringUtils.hasText(csvRecord.get(DAYS_TO_NEXT_DIVIDEND)) ? Integer.parseInt(csvRecord.get(DAYS_TO_NEXT_DIVIDEND)) : null,
                StringUtils.hasText(csvRecord.get(PERCENT_RETURN_NEXT_DIVIDEND)) ? Double.parseDouble(csvRecord.get(PERCENT_RETURN_NEXT_DIVIDEND)) : null
        );
    }
}
