package io.github.nhatbangle.sdp.file.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@EnableConfigurationProperties(AppConfigProps.class)
public class AppConfig {

    @Bean
    public Path storageRoot(@Value("${app.storage-dir}") String storageDir) throws IOException {
        var path = Paths.get(storageDir);
        if (!Files.isDirectory(path)) Files.createDirectory(path);
        return path;
    }

}
