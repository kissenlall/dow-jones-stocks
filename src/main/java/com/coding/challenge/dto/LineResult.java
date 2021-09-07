package com.coding.challenge.dto;

import com.coding.challenge.model.Stock;
import lombok.ToString;

import java.util.List;

@ToString
public class LineResult {

    private final Long lineNumber;
    private final Stock stock;
    private final List<ValidationError> validationErrors;
    private final Boolean insertSucceed;
    private final Throwable exception;

    public LineResult(final Long lineNumber, final Stock stock) {
        this(lineNumber, stock, null, null, null);
    }

    public LineResult(final Long lineNumber, final Stock stock, final List<ValidationError> validationErrors) {
        this(lineNumber, stock, validationErrors, null, null);
    }

    public LineResult(final Throwable exception) {
        this(0L, null, null, false, exception);
    }

    public LineResult(final Long lineNumber, final Stock stock, final List<ValidationError> validationErrors, final Boolean insertSucceed, final Throwable exception) {
        this.lineNumber = lineNumber;
        this.stock = stock;
        this.validationErrors = validationErrors;
        this.insertSucceed = insertSucceed;
        this.exception = exception;
    }

    public Long getLineNumber() {
        return lineNumber;
    }

    public Stock getStock() {
        return stock;
    }

    public Boolean isInsertSucceed() {
        return insertSucceed;
    }

    public Throwable getException() {
        return exception;
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public boolean hasValidationError() {
        return this.validationErrors != null && !this.validationErrors.isEmpty();
    }
}
