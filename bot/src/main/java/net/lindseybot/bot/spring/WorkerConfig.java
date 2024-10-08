package net.lindseybot.bot.spring;

import net.lindseybot.shared.properties.BotProperties;
import net.lindseybot.shared.services.CacheService;
import net.lindseybot.shared.worker.impl.MessengerImpl;
import net.lindseybot.shared.worker.services.DiscordAdapter;
import net.lindseybot.shared.worker.services.Messenger;
import net.lindseybot.shared.worker.services.ProfileService;
import net.lindseybot.shared.worker.services.Translator;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkerConfig {

    @Bean
    @ConfigurationProperties(prefix = "app.bot")
    public BotProperties bot() {
        return new BotProperties();
    }

    @Bean
    public Messenger messenger(DiscordAdapter adapter) {
        return new MessengerImpl(adapter);
    }

    @Bean
    public DiscordAdapter discordAdapter(Translator i18n) {
        return new DiscordAdapter(i18n);
    }

    @Bean
    public Translator translator(ProfileService profiles) {
        return new Translator(profiles);
    }

    @Bean
    public CacheService cacheService() {
        return new CacheService();
    }

}
