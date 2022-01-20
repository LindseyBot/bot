package net.lindseybot.moderation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@Configuration
@ComponentScan("net.lindseybot.moderation")
@EnableJpaRepositories("net.lindseybot.moderation.repositories.sql")
public class LibraryModeration {

    public LibraryModeration() {
        log.info("Initialized moderation features.");
    }

}
