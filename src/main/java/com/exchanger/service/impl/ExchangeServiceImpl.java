package com.exchanger.service.impl;

import com.exchanger.common.ExchangerException;
import com.exchanger.entity.ConversionDetails;
import com.exchanger.enums.ExchangerError;
import com.exchanger.mapper.ConversionMapper;
import com.exchanger.repository.ConversionDetailsRepository;
import com.exchanger.rest.response.ConversionHistoryResponse;
import com.exchanger.rest.response.ConversionResponse;
import com.exchanger.service.ExchangeService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

    private final StringRedisTemplate redisTemplate;
    private final ConversionMapper conversionMapper;
    private final ConversionDetailsRepository conversionDetailsRepository;

    @Override
    public double getExchangeRate(String from, String to) {
        return getRate(from, to);
    }

    @Override
    public ConversionResponse getConvertedAmount(Double amount, String from, String to) {
        ConversionDetails conversionDetails = conversionMapper.toEntity(amount, from, to);
        Double rate = getRate(from, to);
        double convertedAmount = rate * amount;
        conversionDetails.setConvertedAmount(convertedAmount);
        conversionDetails = conversionDetailsRepository.save(conversionDetails);
        return conversionMapper.toResponse(convertedAmount, conversionDetails.getId());
    }

    @Override
    public Page<ConversionHistoryResponse> getConversionHistory(Long id, LocalDateTime date, Pageable pageable) {
        if (Objects.isNull(id) && Objects.isNull(date)) {
            throw new ExchangerException(ExchangerError.NOT_VALID_QUERY_PARAMETERS);
        }
        Page<ConversionDetails> historyList;
        if (!Objects.isNull(id)) {
            historyList = new PageImpl<>(Collections.singletonList(conversionDetailsRepository.findById(id)
                            .orElseThrow( () -> new ExchangerException(ExchangerError.DATA_NOT_FOUND))),
                    PageRequest.of(0, 1), 1);
        } else {
            historyList = conversionDetailsRepository.findByDate(date, pageable);
        }
        if (historyList.get().toList().isEmpty()) {
            throw new ExchangerException(ExchangerError.DATA_NOT_FOUND);
        }
        return conversionMapper.toHistoryResponsePage(historyList);
    }

    private double getRate(String from, String to) {
        try {
            return (double) Objects.requireNonNull(new Gson().fromJson(redisTemplate.opsForValue().get(from), Map.class)).get(from + to);
        } catch (Exception e) {
            throw new ExchangerException(ExchangerError.DATA_NOT_FOUND);
        }
    }
}
