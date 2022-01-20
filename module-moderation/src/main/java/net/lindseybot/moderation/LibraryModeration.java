package net.lindseybot.moderation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ComponentScan("net.lindseybot.moderation")
public class LibraryModeration {

    public LibraryModeration() {
        log.info("Initialized moderation features.");
    }

}
