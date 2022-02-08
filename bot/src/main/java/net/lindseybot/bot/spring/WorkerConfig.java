package net.lindseybot.bot.spring;

import net.lindseybot.shared.properties.BotProperties;
import net.lindseybot.shared.properties.PrometheusProperties;
import net.lindseybot.shared.worker.DefaultWorker;
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

    @Bean
    @ConfigurationProperties(prefix = "app.prometheus")
    public PrometheusProperties prometheus() {
        return new PrometheusProperties();
    }

}
