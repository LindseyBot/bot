package net.lindseybot.help;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@Configuration
@ComponentScan("net.lindseybot.help")
@EnableJpaRepositories("net.lindseybot.help.repositories.sql")
public class LibraryHelp {

    public LibraryHelp() {
        log.info("Initialized help features.");
    }

}
