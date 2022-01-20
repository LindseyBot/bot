package net.lindseybot.nsfw;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ComponentScan("net.lindseybot.nsfw")
public class LibraryNSFW {

    public LibraryNSFW() {
        log.info("Initialized NSFW features.");
    }

}
