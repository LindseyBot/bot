package net.lindseybot.wiki;

import lombok.extern.slf4j.Slf4j;
import net.lindseybot.wiki.properties.ApiProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ComponentScan("net.lindseybot.wiki")
public class LibraryWiki {

    public LibraryWiki() {
        log.info("Initialized wiki features.");
    }

    @Bean
    @ConfigurationProperties(prefix = "app.api")
    public ApiProperties apiWiki() {
        return new ApiProperties();
    }

}
