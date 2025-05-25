package com.exchanger.common;

import com.exchanger.enums.http.HttpMethod;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HttpUtilTest {

    @Mock
    private HttpClient mockClient;

    @Mock
    private HttpResponse<String> mockResponse;

    private static MockedStatic<HttpClient> httpClientStaticMock;

    private static HttpClient originalClient;

    public static class DummyResponse {
        public String message;
    }

    @BeforeAll
    static void beforeAll() throws Exception {
        Field clientField = HttpUtil.class.getDeclaredField("client");
        clientField.setAccessible(true);
        originalClient = (HttpClient) clientField.get(null);

        httpClientStaticMock = mockStatic(HttpClient.class);
    }

    @AfterAll
    static void afterAll() throws Exception {
        if (httpClientStaticMock != null) {
            httpClientStaticMock.close();
        }

        Field clientField = HttpUtil.class.getDeclaredField("client");
        clientField.setAccessible(true);
        clientField.set(null, originalClient);
    }

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        httpClientStaticMock.when(HttpClient::newHttpClient).thenReturn(mockClient);

        Field clientField = HttpUtil.class.getDeclaredField("client");
        clientField.setAccessible(true);
        clientField.set(null, mockClient);
    }

    @AfterEach
    void tearDown() {
        reset(mockClient, mockResponse);
    }

    @Test
    void testSendRequest_successfulResponse_returnsParsedObject() throws Exception {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"message\":\"OK\"}");

        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        DummyResponse result = HttpUtil.sendRequest(
                "http://example.com",
                HttpMethod.GET,
                Map.of("Accept", "application/json"),
                null,
                DummyResponse.class
        );

        assertNotNull(result);
        assertEquals("OK", result.message);
    }

    @Test
    void testSendRequest_failedResponse_throwsException() throws Exception {
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockResponse.body()).thenReturn("Internal Server Error");

        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                HttpUtil.sendRequest(
                        "http://example.com",
                        HttpMethod.GET,
                        null,
                        null,
                        DummyResponse.class
                )
        );

        assertTrue(ex.getMessage().contains("HTTP error: 500"));
    }
}
