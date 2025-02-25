package io.github.nhatbangle.sdp.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SdpFileApplication {

    public static void main(String[] args) {
        SpringApplication.run(SdpFileApplication.class, args);
    }

}
