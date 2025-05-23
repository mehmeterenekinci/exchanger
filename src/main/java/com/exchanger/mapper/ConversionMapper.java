package com.exchanger.mapper;

import com.exchanger.entity.ConversionDetails;
import com.exchanger.rest.request.UploadRequest;
import com.exchanger.rest.response.ConversionHistoryResponse;
import com.exchanger.rest.response.ConversionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper
public interface ConversionMapper {
    ConversionMapper INSTANCE = Mappers.getMapper(ConversionMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", expression = "java(java.time.LocalDateTime.now().toLocalDate().atStartOfDay())")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "fromCurrency", source = "from")
    @Mapping(target = "toCurrency", source = "to")
    ConversionDetails toEntity(Double amount, String from, String to);

    @Mapping(target = "convertedValue", source = "amount")
    @Mapping(target = "conversionId", source = "id")
    ConversionResponse toResponse(Double amount, Long id);

    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "convertedAmount", source = "convertedAmount")
    @Mapping(target = "fromCurrency", source = "fromCurrency")
    @Mapping(target = "toCurrency", source = "toCurrency")
    @Mapping(target = "date", source = "date")
    ConversionHistoryResponse toHistoryResponse(ConversionDetails entity);

    List<ConversionHistoryResponse> toHistoryResponseList(List<ConversionDetails> entities);

    default Page<ConversionHistoryResponse> toHistoryResponsePage(Page<ConversionDetails> page) {
        return page.map(this::toHistoryResponse);
    }

    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "fromCurrency", source = "from")
    @Mapping(target = "toCurrency", source = "to")
    UploadRequest toUploadRequest(Double amount, String from, String to);
}
