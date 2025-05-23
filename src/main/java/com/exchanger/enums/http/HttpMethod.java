package com.exchanger.enums.http;

import java.net.http.HttpRequest;

public enum HttpMethod {
    GET(body -> HttpRequest.BodyPublishers.noBody()),
    POST(body -> HttpRequest.BodyPublishers.ofString(body != null ? body : "")),
    PUT(body -> HttpRequest.BodyPublishers.ofString(body != null ? body : "")),
    DELETE(body -> HttpRequest.BodyPublishers.noBody());

    private final HttpMethodStrategy strategy;

    HttpMethod(HttpMethodStrategy strategy) {
        this.strategy = strategy;
    }

    public HttpRequest.BodyPublisher getBodyPublisher(String body) {
        return strategy.getBodyPublisher(body);
    }
}
