package net.lindseybot.bot.spring;

import net.lindseybot.properties.BotProperties;
import net.lindseybot.worker.DefaultWorker;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkerConfig extends DefaultWorker {

    @Bean
    @ConfigurationProperties(prefix = "app.bot")
    public BotProperties bot() {
        return new BotProperties();
    }

}
