package net.lindseybot.economy;

import lombok.extern.slf4j.Slf4j;
import net.lindseybot.economy.properties.ImageGenProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@Configuration
@ComponentScan("net.lindseybot.economy")
@EnableJpaRepositories("net.lindseybot.economy.repositories.sql")
public class LibraryEconomy {

    public LibraryEconomy() {
        log.info("Initialized Economy features.");
    }

    @Bean
    @ConfigurationProperties(prefix = "app.images")
    public ImageGenProperties imageGen() {
        return new ImageGenProperties();
    }

}
