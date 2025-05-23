package com.exchanger.service;

import com.exchanger.rest.response.ConversionHistoryResponse;
import com.exchanger.rest.response.ConversionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface ExchangeService {
    double getExchangeRate(String from, String to);
    ConversionResponse getConvertedAmount(Double amount, String from, String to);
    Page<ConversionHistoryResponse> getConversionHistory(Long id, LocalDateTime date, Pageable pageable);
    List<ConversionResponse> getBulkConvertedAmount(MultipartFile[] files);
}