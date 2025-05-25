package com.exchanger.controller;

import com.exchanger.common.ExchangerException;
import com.exchanger.common.StandardResponse;
import com.exchanger.enums.ExchangerError;
import com.exchanger.rest.response.ConversionHistoryResponse;
import com.exchanger.rest.response.ConversionResponse;
import com.exchanger.service.ExchangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/exchange")
@RequiredArgsConstructor
public class ExchangeController {

    private final ExchangeService exchangeService;

    @GetMapping("/{currency1:USD|EUR|GBP}/{currency2:USD|EUR|GBP}")
    @Operation(
            summary = "Get exchange rate for a currency pair",
            description = "Returns the current exchange rate from currency1 to currency2. " +
                    "Both path variables should be ISO currency codes. Supported currencies: USD , EUR , GBP."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exchange rate successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid currency code supplied"),
            @ApiResponse(responseCode = "404", description = "Exchange rate not found for given pair"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StandardResponse<Double>> getExchangeRate(
            @Parameter(description = "Source currency code (ISO 4217 format)", example = "USD")
            @PathVariable String currency1,
            @Parameter(description = "Target currency code (ISO 4217 format)", example = "EUR")
            @PathVariable String currency2) {
        return StandardResponse.success("Success", exchangeService.getExchangeRate(currency1,currency2));
    }

    @GetMapping("/{amount}/{currency1:USD|EUR|GBP}/{currency2:USD|EUR|GBP}")
    @Operation(
            summary = "Convert currency amount",
            description = "Converts the given amount from one currency to another. Supported currencies: USD, EUR, GBP."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully converted",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StandardResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid currency or amount",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<StandardResponse<ConversionResponse>> getConvertedAmount(
            @Parameter(description = "Amount to be converted", example = "100.0")
            @PathVariable Double amount,
            @Parameter(description = "Source currency (USD, EUR, GBP)", example = "USD")
            @PathVariable String currency1,
            @Parameter(description = "Target currency (USD, EUR, GBP)", example = "EUR")
            @PathVariable String currency2) {
        return StandardResponse.success("Success", exchangeService.getConvertedAmount(amount, currency1, currency2));
    }

    @GetMapping
    @Operation(
            summary = "Get conversion details with optional filters",
            description = "Returns a paginated list of conversion records filtered by ID and/or date.",
            parameters = {
                    @Parameter(name = "id", description = "Optional filter by ID", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
                    @Parameter(name = "date", description = "Optional filter by date (yyyy-MM-dd)", in = ParameterIn.QUERY, schema = @Schema(type = "string", format = "date"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of conversion records"),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
            }
    )
    public ResponseEntity<StandardResponse<Page<ConversionHistoryResponse>>> getConversions(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String date,
            @ParameterObject Pageable pageable) {
        LocalDateTime ldt = null;
        if (date != null && !date.isBlank()) {
            try {
                ldt = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
            } catch (DateTimeParseException e) {
                throw new ExchangerException(ExchangerError.INVALID_DATE_FORMAT);
            }
        }
        for (Sort.Order order : pageable.getSort()) {
            String direction = order.getDirection().name().toLowerCase();
            if (!direction.equals("asc") && !direction.equals("desc")) {
                throw new ExchangerException(ExchangerError.INVALID_SORTING_ORDER);
            }
        }
        return StandardResponse.success("Success", exchangeService.getConversionHistory(id, ldt, pageable));
    }

    @Operation(
            summary = "Upload multiple CSV files",
            description = "Accepts multiple CSV files, each containing rows of `amount`, `fromCurrency`, and `toCurrency`. Parses them into UploadRequest objects and returns the conversion results."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CSV files parsed and processed successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ConversionResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Bad request or file parse error",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StandardResponse<List<ConversionResponse>>> uploadCsvFiles(
            @Parameter(description = "Multiple CSV files", required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
            @RequestPart("files") MultipartFile[] files) {
        return StandardResponse.success("Success", exchangeService.getBulkConvertedAmount(files));
    }
}
