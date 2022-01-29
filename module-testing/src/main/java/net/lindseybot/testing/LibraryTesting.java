package net.lindseybot.testing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ComponentScan("net.lindseybot.testing")
public class LibraryTesting {

    public LibraryTesting() {
        log.info("Initialized testing features.");
    }

}
