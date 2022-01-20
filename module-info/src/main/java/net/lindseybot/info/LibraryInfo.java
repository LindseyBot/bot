package net.lindseybot.info;

import lombok.extern.slf4j.Slf4j;
import net.lindseybot.info.properties.ApiProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@Configuration
@ComponentScan("net.lindseybot.info")
@EnableJpaRepositories("net.lindseybot.info.repositories.sql")
public class LibraryInfo {

    public LibraryInfo() {
        log.info("Initialized info features.");
    }

    @Bean
    @ConfigurationProperties(prefix = "app.api")
    public ApiProperties infoAPI() {
        return new ApiProperties();
    }

}
