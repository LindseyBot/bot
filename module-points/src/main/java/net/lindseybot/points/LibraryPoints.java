package net.lindseybot.points;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@Configuration
@ComponentScan("net.lindseybot.points")
@EnableJpaRepositories("net.lindseybot.points.repositories")
public class LibraryPoints {

    public LibraryPoints() {
        log.info("Initialized points features.");
    }

}
