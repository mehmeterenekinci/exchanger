package com.exchanger.rest.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConversionHistoryResponse {
    Double amount;
    Double convertedAmount;
    String fromCurrency;
    String toCurrency;
    LocalDateTime date;
}
