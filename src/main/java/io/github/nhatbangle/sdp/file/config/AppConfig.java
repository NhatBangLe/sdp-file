package io.github.nhatbangle.sdp.file.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@EnableWebMvc
@Configuration
@EnableConfigurationProperties(AppConfigProps.class)
public class AppConfig implements WebMvcConfigurer {

    @Bean
    public Path storageRoot(@Value("${app.storage-dir}") String storageDir) throws IOException {
        var path = Paths.get(storageDir);
        if (!Files.isDirectory(path)) Files.createDirectory(path);
        return path;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .maxAge(3600);
    }

}
