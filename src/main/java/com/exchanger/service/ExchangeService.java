package com.exchanger.service;

import com.exchanger.rest.response.ConversionHistoryResponse;
import com.exchanger.rest.response.ConversionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ExchangeService {
    double getExchangeRate(String from, String to);
    ConversionResponse getConvertedAmount(Double amount, String from, String to);
    Page<ConversionHistoryResponse> getConversionHistory(Long id, LocalDateTime date, Pageable pageable);
}