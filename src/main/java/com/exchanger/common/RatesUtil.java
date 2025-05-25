package com.exchanger.common;

import com.exchanger.enums.CurrencyEnum;
import com.exchanger.enums.ExchangerError;
import com.exchanger.enums.http.HttpMethod;
import com.exchanger.rest.response.CurrencyResponse;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RatesUtil {

    public static final String ACCESS_KEY = "e5a236f1e98b3d2a6e836f7ea9092dad";
    public static final String BASE_URL = "http://api.currencylayer.com/";
    public static final String ENDPOINT = "live";

    private final StringRedisTemplate redisTemplate;

    @Scheduled(cron = "0 0 * * * *")
    public void getCurrencyRates() {
        try {
            for (CurrencyEnum currency : CurrencyEnum.values()) {
                CurrencyResponse response = HttpUtil.sendRequest(
                        BASE_URL + ENDPOINT + "?access_key=" + ACCESS_KEY + "&currencies=" + CurrencyEnum.valuesExcept(currency) + "&source=" + currency.name(),
                        HttpMethod.GET,
                        null,
                        null,
                        CurrencyResponse.class
                );
                redisTemplate.opsForValue().set(currency.name(), new Gson().toJson(response.getQuotes()));
            }
        } catch (Exception e) {
            throw new ExchangerException(ExchangerError.CANNOT_ACCESS_RATE_API);
        }
    }
}
