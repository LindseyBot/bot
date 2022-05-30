package net.lindseybot.bot.spring;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("net.lindseybot.shared.entities")
@EnableJpaRepositories({
        "net.lindseybot.bot.repositories.sql",
        "net.lindseybot.shared.repositories"
})
public class MariaConfig {
}
