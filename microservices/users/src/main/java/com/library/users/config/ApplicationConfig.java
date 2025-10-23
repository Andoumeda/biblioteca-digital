package com.library.users.config;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Configuration
public class ApplicationConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setAmbiguityIgnored(true);

        //LocalDateTime -> OffsetDateTime
        Converter<LocalDateTime, OffsetDateTime> localDateTimeToOffsetDateTime = new AbstractConverter<LocalDateTime, OffsetDateTime>() {
            @Override
            protected OffsetDateTime convert(LocalDateTime source) {
                return source != null ? source.atOffset(ZoneOffset.UTC) : null;
            }
        };

        modelMapper.addConverter(localDateTimeToOffsetDateTime);

        return modelMapper;
    }
}