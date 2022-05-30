package net.lindseybot.bot.spring;

import com.github.benmanes.caffeine.cache.Caffeine;
import net.lindseybot.shared.properties.BotProperties;
import net.lindseybot.shared.properties.PrometheusProperties;
import net.lindseybot.shared.worker.DefaultWorker;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

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

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(15_000)
                .expireAfterWrite(60, TimeUnit.MINUTES));
        return manager;
    }

}
