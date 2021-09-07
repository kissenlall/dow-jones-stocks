package com.coding.challenge.exception;

import lombok.Getter;

/**
 * This class is used to report on any <code>Exception</code> that occurs
 * while parsing the uploaded file. The aim here is to provide maximum
 * information regarding the nature of the problem such as the line number
 * and exception message.
 */
@Getter
public class UnparseableFileException  extends RuntimeException {

    private final Long lineNumber;

    public UnparseableFileException( Long lineNumber, Throwable throwable ) {
        super(throwable.getMessage(), throwable);
        this.lineNumber = lineNumber;
    }
}
