package com.exchanger.service.impl;

import com.exchanger.common.ExchangerException;
import com.exchanger.entity.ConversionDetails;
import com.exchanger.mapper.ConversionMapper;
import com.exchanger.repository.ConversionDetailsRepository;
import com.exchanger.rest.request.UploadRequest;
import com.exchanger.rest.response.ConversionHistoryResponse;
import com.exchanger.rest.response.ConversionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExchangeServiceImplTest {

    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private ConversionMapper conversionMapper;
    @Mock
    private ConversionDetailsRepository repository;

    @InjectMocks
    private ExchangeServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetExchangeRate_success() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("USD")).thenReturn("{\"USDINR\": 82.0}");

        double rate = service.getExchangeRate("USD", "INR");

        assertEquals(82.0, rate);
    }

    @Test
    void testGetExchangeRate_failure() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("USD")).thenReturn(null);

        assertThrows(ExchangerException.class, () -> service.getExchangeRate("USD", "INR"));
    }

    @Test
    void testGetConvertedAmount_success() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("USD")).thenReturn("{\"USDINR\": 82.0}");

        ConversionDetails entity = new ConversionDetails();
        entity.setConvertedAmount(820.0);
        entity.setId(1L);

        when(conversionMapper.toEntity(10.0, "USD", "INR")).thenReturn(entity);
        when(repository.save(any())).thenReturn(entity);
        when(conversionMapper.toResponse(820.0, 1L)).thenReturn(new ConversionResponse());

        ConversionResponse response = service.getConvertedAmount(10.0, "USD", "INR");
        assertNotNull(response);
    }

    @Test
    void testGetConversionHistory_byId_success() {
        ConversionDetails detail = new ConversionDetails();
        detail.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(detail));
        when(conversionMapper.toHistoryResponsePage(any())).thenReturn(Page.empty());

        Page<ConversionHistoryResponse> result = service.getConversionHistory(1L, null, PageRequest.of(0, 1));
        assertNotNull(result);
    }

    @Test
    void testGetConversionHistory_byDate_success() {
        LocalDateTime now = LocalDateTime.now();
        Page<ConversionDetails> page = new PageImpl<>(List.of(new ConversionDetails()));

        when(repository.findByDate(eq(now), any())).thenReturn(page);
        when(conversionMapper.toHistoryResponsePage(any())).thenReturn(Page.empty());

        Page<ConversionHistoryResponse> result = service.getConversionHistory(null, now, PageRequest.of(0, 1));
        assertNotNull(result);
    }

    @Test
    void testGetConversionHistory_failure_invalidParams() {
        assertThrows(ExchangerException.class, () -> service.getConversionHistory(null, null, PageRequest.of(0, 1)));
    }

    @Test
    void testGetConversionHistory_failure_noData() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ExchangerException.class, () -> service.getConversionHistory(1L, null, PageRequest.of(0, 1)));
    }

    @Test
    void testGetBulkConvertedAmount_success() {
        String content = "100,USD,INR\n";
        MultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", content.getBytes());

        UploadRequest req = new UploadRequest();
        req.setAmount(100.0);
        req.setFromCurrency("USD");
        req.setToCurrency("INR");

        when(conversionMapper.toUploadRequest(100.0, "USD", "INR")).thenReturn(req);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("USD")).thenReturn("{\"USDINR\": 82.0}");

        ConversionDetails cd = new ConversionDetails();
        cd.setConvertedAmount(8200.0);
        cd.setId(1L);

        when(conversionMapper.toEntity(100.0, "USD", "INR")).thenReturn(cd);
        when(repository.save(any())).thenReturn(cd);
        when(conversionMapper.toResponse(8200.0, 1L)).thenReturn(new ConversionResponse());

        List<ConversionResponse> responses = service.getBulkConvertedAmount(new MultipartFile[]{file});
        assertEquals(1, responses.size());
    }


    @Test
    void testParseCsv_invalidContentType() {
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "100,USD,INR".getBytes());
        assertThrows(ExchangerException.class, () -> service.getBulkConvertedAmount(new MultipartFile[]{file}));
    }

    @Test
    void testParseCsv_emptyFile() {
        MultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", new byte[0]);
        List<ConversionResponse> results = service.getBulkConvertedAmount(new MultipartFile[]{file});
        assertTrue(results.isEmpty());
    }
}