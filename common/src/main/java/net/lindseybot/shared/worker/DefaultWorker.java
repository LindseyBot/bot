package net.lindseybot.shared.worker;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.internal.utils.concurrent.CountingThreadFactory;
import net.lindseybot.shared.properties.BotProperties;
import net.lindseybot.shared.properties.ShardProperties;
import net.lindseybot.shared.worker.impl.*;
import net.lindseybot.shared.worker.services.DiscordAdapter;
import net.lindseybot.shared.worker.services.Messenger;
import net.lindseybot.shared.worker.services.ProfileService;
import net.lindseybot.shared.worker.services.Translator;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;

import javax.security.auth.login.LoginException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Initializes necessary services, requires two beans:
 * - List of interaction handlers
 * - BotProperties with credentials
 */
@Slf4j
public class DefaultWorker {

    @Bean
    public ShardManager jda(BotProperties config, IEventManager manager) {
        try {
            DefaultShardManagerBuilder builder = DefaultShardManagerBuilder
                    .createLight(config.getToken())
                    .setEventManagerProvider((d) -> manager);
            if (config.getGateway() != null && !config.getGateway().isBlank()) {
                builder.setSessionController(new GatewayController(config));
                builder.setCompression(Compression.NONE);
                log.info("Starting JDA with Gateway Proxy");
            }
            if (config.getRest() != null && !config.getRest().isBlank()) {
                try {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(new DiscordInterceptor(new URL(config.getRest())))
                            .readTimeout(2, TimeUnit.MINUTES)
                            .writeTimeout(5, TimeUnit.SECONDS)
                            .callTimeout(2, TimeUnit.MINUTES)
                            .connectTimeout(5, TimeUnit.SECONDS)
                            .build();
                    builder.setHttpClient(client);

                    ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(256);
                    pool.setThreadFactory(new CountingThreadFactory(() -> "JDA", "RateLimit", false));
                    pool.setKeepAliveTime(1, TimeUnit.MINUTES);
                    pool.allowCoreThreadTimeOut(true);
                    builder.setRateLimitPool(pool);

                    log.info("Starting JDA with rest proxy.");
                } catch (MalformedURLException ex) {
                    log.error("Failed to format rest URL", ex);
                }
            }
            if (config.getIntents() != null) {
                builder.setEnabledIntents(GatewayIntent.getIntents(config.getIntents()));
            }
            if (config.getShards() != null) {
                ShardProperties shards = config.getShards();
                if (shards.getShards() != null) {
                    builder.setShards(shards.getShards());
                    log.info("Starting JDA with shard set.");
                } else if (shards.getMin() != null && shards.getMax() != null) {
                    builder.setShards(shards.getMin(), shards.getMax());
                    log.info("Starting JDA with shard range.");
                }
                builder.setShardsTotal(shards.getTotal());
            }
            return builder.build();
        } catch (LoginException ex) {
            throw new IllegalStateException("Failed to start JDA", ex);
        }
    }

    @Bean
    public IEventManager eventManager() {
        return new PooledEventManager();
    }

    @Bean
    public InteractionService interactionService(List<InteractionHandler> handlers) {
        return new DefaultInteractionService(handlers);
    }

    @Bean
    public DefaultInteractionListener interactionListener(DefaultInteractionService service, ShardManager api) {
        return new DefaultInteractionListener(service, api);
    }

    @Bean
    public Messenger messenger(DiscordAdapter adapter) {
        return new DefaultMessenger(adapter);
    }

    @Bean
    public DiscordAdapter discordAdapter(Translator i18n) {
        return new DiscordAdapter(i18n);
    }

    @Bean
    public Translator translator(ProfileService profiles) {
        return new Translator(profiles);
    }

}
