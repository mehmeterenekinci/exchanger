package com.exchanger.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExchangerError {
    NOT_VALID_QUERY_PARAMETERS("C-001", "Not a valid query parameters", HttpStatus.BAD_REQUEST),
    DATA_NOT_FOUND("C-002", "Requested data not found.", HttpStatus.NOT_FOUND),
    CANNOT_ACCESS_RATE_API("C-003", "Can not access Rate API.", HttpStatus.FORBIDDEN),;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ExchangerError(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
