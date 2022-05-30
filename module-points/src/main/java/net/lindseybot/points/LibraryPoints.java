package net.lindseybot.points;

import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import net.lindseybot.points.entities.PointConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@ComponentScan("net.lindseybot.points")
public class LibraryPoints {

    public LibraryPoints() {
        log.info("Initialized points features.");
    }

    @Bean
    public ExpiringMap<Long, PointConfig> configCache() {
        return ExpiringMap.builder()
                .expirationPolicy(ExpirationPolicy.ACCESSED)
                .expiration(5, TimeUnit.MINUTES)
                .maxSize(15_000)
                .build();
    }

}
