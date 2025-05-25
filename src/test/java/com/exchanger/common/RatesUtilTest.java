package com.exchanger.common;

import com.exchanger.enums.CurrencyEnum;
import com.exchanger.enums.ExchangerError;
import com.exchanger.enums.http.HttpMethod;
import com.exchanger.rest.response.CurrencyResponse;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RatesUtilTest {

    private StringRedisTemplate redisTemplate;
    private RatesUtil ratesUtil;

    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        Mockito.framework().clearInlineMocks();
        redisTemplate = mock(StringRedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        ratesUtil = new RatesUtil(redisTemplate);
    }

    @Test
    void testGetCurrencyRates_success() {
        try (MockedStatic<HttpUtil> mockedHttpUtil = mockStatic(HttpUtil.class)) {
            for (CurrencyEnum currency : CurrencyEnum.values()) {
                CurrencyResponse mockResponse = new CurrencyResponse();
                mockResponse.setQuotes(Map.of("USDGBP", 0.82));
                mockedHttpUtil.when(() ->
                        HttpUtil.sendRequest(
                                contains("source=" + currency.name()),
                                eq(HttpMethod.GET),
                                isNull(),
                                isNull(),
                                eq(CurrencyResponse.class)
                        )
                ).thenReturn(mockResponse);
            }

            ratesUtil.getCurrencyRates();

            verify(redisTemplate, times(CurrencyEnum.values().length)).opsForValue();
            verify(valueOperations, atLeastOnce()).set(anyString(), anyString());
        }
    }

    @Test
    void testGetCurrencyRates_apiFails_throwsException() {
        try (MockedStatic<HttpUtil> mockedHttpUtil = mockStatic(HttpUtil.class)) {
            CurrencyEnum testCurrency = CurrencyEnum.USD;

            mockedHttpUtil.when(() ->
                    HttpUtil.sendRequest(
                            contains("source=" + testCurrency.name()),
                            eq(HttpMethod.GET),
                            isNull(),
                            isNull(),
                            eq(CurrencyResponse.class)
                    )
            ).thenThrow(new RuntimeException("API failed"));

            RatesUtil ratesUtilPartial = new RatesUtil(redisTemplate) {
                @Override
                public void getCurrencyRates() {
                    try {
                        CurrencyResponse response = HttpUtil.sendRequest(
                                BASE_URL + ENDPOINT + "?access_key=" + ACCESS_KEY +
                                        "&currencies=" + CurrencyEnum.valuesExcept(testCurrency) +
                                        "&source=" + testCurrency.name(),
                                HttpMethod.GET,
                                null,
                                null,
                                CurrencyResponse.class
                        );
                        redisTemplate.opsForValue().set(testCurrency.name(), new Gson().toJson(response.getQuotes()));
                    } catch (Exception e) {
                        throw new ExchangerException(ExchangerError.CANNOT_ACCESS_RATE_API);
                    }
                }
            };

            ExchangerException exception = assertThrows(ExchangerException.class, ratesUtilPartial::getCurrencyRates);
            assert(Objects.equals(exception.getErrorCode(), ExchangerError.CANNOT_ACCESS_RATE_API.getCode()));
        }
    }

}
