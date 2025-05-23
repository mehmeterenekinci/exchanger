package com.exchanger.rest.response;

import lombok.Data;

import java.util.Map;

@Data
public class CurrencyResponse {
    String success;
    String source;
    Map<String, Double> quotes;
}