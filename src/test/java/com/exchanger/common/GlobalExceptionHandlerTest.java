package com.exchanger.common;

import com.exchanger.enums.ExchangerError;
import com.exchanger.rest.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    @Test
    void handleExchangerException_returnsErrorResponse() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/test/path");

        ExchangerException ex = new ExchangerException(ExchangerError.AN_ISSUE_OCCURED_PLEASE_TRY_LATER);

        ResponseEntity<ErrorResponse> responseEntity = handler.handleExchangerException(ex, request);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        ErrorResponse body = responseEntity.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(body.getErrorCode()).isEqualTo("C-006");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timestamp = body.getTimestamp();

        Duration duration = Duration.between(timestamp, now).abs();
        assertThat(duration.getSeconds()).isLessThanOrEqualTo(1);
    }
}