package com.exchanger.enums;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum CurrencyEnum {
    USD, EUR, GBP;

    public static String valuesExcept(CurrencyEnum excluded) {
        return Arrays.stream(values()).filter(method -> method != excluded).map(Enum::name).collect(Collectors.joining(","));
    }
}