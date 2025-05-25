package com.exchanger.common;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class StandardResponseTest {

    @Test
    void testSuccess_withData() {
        String data = "Test Data";
        ResponseEntity<StandardResponse<String>> response = StandardResponse.success(data);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCode());
        assertEquals("Success", response.getBody().getMessage());
        assertEquals(data, response.getBody().getData());
    }

    @Test
    void testSuccess_withMessageAndData() {
        String data = "Test Data";
        String customMessage = "All good!";
        ResponseEntity<StandardResponse<String>> response = StandardResponse.success(customMessage, data);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCode());
        assertEquals(customMessage, response.getBody().getMessage());
        assertEquals(data, response.getBody().getData());
    }

    @Test
    void testError() {
        int errorCode = 500;
        String errorMessage = "Internal server error";
        ResponseEntity<StandardResponse<Object>> response = StandardResponse.error(errorCode, errorMessage);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorCode, response.getBody().getCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void testBadRequest() {
        String errorMessage = "Invalid input";
        ResponseEntity<StandardResponse<Object>> response = StandardResponse.badRequest(errorMessage);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }
}