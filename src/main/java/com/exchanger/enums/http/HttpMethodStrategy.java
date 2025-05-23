package com.exchanger.enums.http;

import java.net.http.HttpRequest;

@FunctionalInterface
public interface HttpMethodStrategy {
    HttpRequest.BodyPublisher getBodyPublisher(String body);
}