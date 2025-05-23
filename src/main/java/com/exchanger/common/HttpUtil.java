package com.exchanger.common;

import com.exchanger.enums.http.HttpMethod;
import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class HttpUtil {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    public static <T> T sendRequest(String url, HttpMethod method, Map<String, String> headers, String body,
                                    Class<T> responseType) throws Exception {

        HttpRequest.BodyPublisher publisher = method.getBodyPublisher(body);

        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url)).method(method.name(), publisher);

        if (headers != null) {
            headers.forEach(builder::header);
        }

        HttpRequest request = builder.build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return gson.fromJson(response.body(), responseType);
        } else {
            throw new RuntimeException("HTTP error: " + response.statusCode() + " - " + response.body());
        }
    }
}
