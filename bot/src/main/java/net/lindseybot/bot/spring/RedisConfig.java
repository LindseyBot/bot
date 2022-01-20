package net.lindseybot.bot.spring;

import net.dv8tion.jda.api.sharding.ShardManager;
import net.lindseybot.shared.worker.legacy.LegacyListener;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisConfig {

    @Bean
    @Primary
    public RedisProperties redisProperties() {
        return new RedisProperties();
    }

    @Bean
    @Primary
    public RedisConnectionFactory redisFactory(RedisProperties properties) {
        RedisStandaloneConfiguration config =
                new RedisStandaloneConfiguration(properties.getHost(), properties.getPort());
        if (properties.getPassword() != null && !properties.getPassword().isBlank()) {
            config.setPassword(properties.getPassword());
        }
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisConnection redisConnection(RedisConnectionFactory factory) {
        return factory.getConnection();
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<?, ?> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        return template;
    }

    @Bean
    public RedisMessageListenerContainer listeners(RedisConnectionFactory factory, ShardManager api) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(new LegacyListener(api), new PatternTopic("legacy"));
        return container;
    }

}
