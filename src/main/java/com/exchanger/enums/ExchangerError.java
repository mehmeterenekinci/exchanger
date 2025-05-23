package com.exchanger.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExchangerError {
    NOT_VALID_QUERY_PARAMETERS("C-001", "Not a valid query parameters", HttpStatus.BAD_REQUEST),
    DATA_NOT_FOUND("C-002", "Requested data not found.", HttpStatus.NOT_FOUND),
    CANNOT_ACCESS_RATE_API("C-003", "Can not access Rate API.", HttpStatus.FORBIDDEN),
    AN_ISSUE_OCCURED_WHILE_READING_FILES("C-004", "An issue occurred while reading files.", HttpStatus.INTERNAL_SERVER_ERROR),
    UNSUPPORTED_FILE_TYPE("C-005", "Unsupported file type.", HttpStatus.INTERNAL_SERVER_ERROR),;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ExchangerError(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
