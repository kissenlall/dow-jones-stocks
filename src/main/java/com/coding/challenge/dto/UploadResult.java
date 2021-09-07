package com.coding.challenge.dto;

import lombok.Getter;
import lombok.ToString;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Getter
@ToString
public class UploadResult {

    public enum Status { OK, ERROR }

    private final File file;
    private final Status status;
    private final long totalRowsRead;
    private final long totalRowsIngested;
    private final long totalErrorRows;

    private final Throwable exception;
    private final List<ValidationError> validationErrors;

    public UploadResult(final File file) {
        this(file, Status.OK, 0, 0, 0, null, Collections.emptyList());
    }

    public UploadResult(final File file, final Status status, final long totalRowsRead, final long totalRowsIngested, final long totalErrorRows, final Throwable exception, final List<ValidationError> validationErrors) {
        this.file = file;
        this.status = status;
        this.totalRowsRead = totalRowsRead;
        this.totalRowsIngested = totalRowsIngested;
        this.totalErrorRows = totalErrorRows;
        this.exception = exception;
        this.validationErrors = validationErrors;
    }

    public UploadResult accumulate(final List<ValidationError> validationErrors, final boolean isIngested, final Throwable exception) {
        final boolean isError = (validationErrors != null && !validationErrors.isEmpty()) || exception != null;
        final Status status = this.status == Status.ERROR || isError
                ? Status.ERROR
                : Status.OK;
        final List<ValidationError> newValidationErrors = new LinkedList<>(this.validationErrors);
        if (validationErrors != null && !validationErrors.isEmpty()) {
            newValidationErrors.addAll(validationErrors);
        }
        return new UploadResult(
                this.file,
                status,
                this.totalRowsRead + 1,
                isIngested ? this.totalRowsIngested + 1 : this.totalRowsIngested,
                isError ? this.totalErrorRows + 1 : this.totalErrorRows,
                this.exception == null ? exception : this.exception,
                newValidationErrors
        );
    }
}
