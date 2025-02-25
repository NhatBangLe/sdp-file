package io.github.nhatbangle.sdp.file.config;

import io.github.nhatbangle.sdp.file.mapper.FileMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public FileMapper fileMapper() {
        return new FileMapper();
    }

}
