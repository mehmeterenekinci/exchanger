package com.exchanger.rest.request;

import lombok.Data;

@Data
public class UploadRequest {
    private Double amount;
    private String fromCurrency;
    private String toCurrency;
}