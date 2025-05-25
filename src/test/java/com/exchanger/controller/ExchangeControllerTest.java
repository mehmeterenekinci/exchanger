package com.exchanger.controller;

import com.exchanger.rest.response.ConversionHistoryResponse;
import com.exchanger.rest.response.ConversionResponse;
import com.exchanger.service.ExchangeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExchangeController.class)
class ExchangeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeService exchangeService;

    @Test
    void testGetExchangeRate() throws Exception {
        Mockito.when(exchangeService.getExchangeRate("USD", "EUR")).thenReturn(0.85);

        mockMvc.perform(get("/exchange/USD/EUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data").value(0.85));
    }

    @Test
    void testGetConvertedAmount() throws Exception {
        ConversionResponse mockResponse = new ConversionResponse(100.0, 1L);
        Mockito.when(exchangeService.getConvertedAmount(100.0, "USD", "EUR")).thenReturn(mockResponse);

        mockMvc.perform(get("/exchange/100.0/USD/EUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.convertedValue").value(100.0))
                .andExpect(jsonPath("$.data.conversionId").value(1L));
    }

    @Test
    void testGetConversions() throws Exception {
        ConversionHistoryResponse mockHistory = new ConversionHistoryResponse();
        PageImpl<ConversionHistoryResponse> mockPage = new PageImpl<>(List.of(mockHistory));
        Mockito.when(exchangeService.getConversionHistory(any(), any(), any(Pageable.class)))
                .thenReturn(mockPage);

        mockMvc.perform(get("/exchange")
                        .param("id", "1")
                        .param("date", "2024-05-01")
                        .param("size", "1")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.content", hasSize(1)));
    }

    @Test
    void testUploadCsvFiles() throws Exception {
        MockMultipartFile file = new MockMultipartFile("files", "file.csv", "text/csv",
                "100,USD,EUR".getBytes());

        ConversionResponse mockResponse = new ConversionResponse(100.0, 1L);
        Mockito.when(exchangeService.getBulkConvertedAmount(any())).thenReturn(List.of(mockResponse));

        mockMvc.perform(multipart("/exchange/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].convertedValue").value(100.0))
                .andExpect(jsonPath("$.data[0].conversionId").value(1L));
    }
}
