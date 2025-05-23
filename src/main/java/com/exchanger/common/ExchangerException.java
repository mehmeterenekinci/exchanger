package com.exchanger.common;

import com.exchanger.enums.ExchangerError;
import org.springframework.http.HttpStatus;

public class ExchangerException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    public ExchangerException(ExchangerError errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getCode();
        this.httpStatus = errorCode.getHttpStatus();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
