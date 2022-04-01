package net.lindseybot.legacy;

import lombok.extern.slf4j.Slf4j;
import net.lindseybot.legacy.properties.LegacyProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ComponentScan("net.lindseybot.legacy")
public class LibraryLegacy {

    public LibraryLegacy() {
        log.info("Initialized legacy features.");
    }

    @Bean
    @ConfigurationProperties(prefix = "app.legacy")
    public LegacyProperties api() {
        return new LegacyProperties();
    }

}
