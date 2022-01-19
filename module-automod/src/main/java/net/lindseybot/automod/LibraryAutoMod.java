package net.lindseybot.automod;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@Configuration
@ComponentScan("net.lindseybot.automod")
@EnableJpaRepositories("net.lindseybot.automod.repositories.sql")
public class LibraryAutoMod {

    public LibraryAutoMod() {
        log.info("Initialized AutoMod features.");
    }

}
