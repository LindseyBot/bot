package net.lindseybot.fun;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@Configuration
@ComponentScan("net.lindseybot.fun")
@EntityScan("net.lindseybot.fun.entities")
@EnableJpaRepositories("net.lindseybot.fun.repositories.sql")
public class LibraryFun {

    public LibraryFun() {
        log.info("Initialized fun features.");
    }

}
