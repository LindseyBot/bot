package net.lindseybot.bot.spring;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "net.lindseybot.entities")
@EnableJpaRepositories(basePackages = "net.lindseybot.bot.repositories.sql")
public class MariaConfig {
}
