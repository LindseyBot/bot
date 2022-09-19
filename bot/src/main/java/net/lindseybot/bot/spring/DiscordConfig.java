package net.lindseybot.bot.spring;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.internal.utils.concurrent.CountingThreadFactory;
import net.lindseybot.shared.properties.BotProperties;
import net.lindseybot.shared.properties.ShardProperties;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.InteractionService;
import net.lindseybot.shared.worker.Metrics;
import net.lindseybot.shared.worker.impl.*;
import net.lindseybot.shared.worker.services.Messenger;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class DiscordConfig {

    @Bean
    public ShardManager jda(BotProperties config, IEventManager manager) throws LoginException {
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder
                .createDefault(config.getToken())
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .setMemberCachePolicy(MemberCachePolicy.VOICE.or(MemberCachePolicy.PENDING))
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
        if (config.getShards() != null) {
            ShardProperties shards = config.getShards();
            builder.setShardsTotal(shards.getTotal());
            if (shards.getShards() != null) {
                builder.setShards(shards.getShards());
                log.info("Starting JDA with shard set.");
            } else if (shards.getMin() != null && shards.getMax() != null) {
                builder.setShards(shards.getMin(), shards.getMax());
                log.info("Starting JDA with shard range.");
            }
        }
        return builder.build();
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
    public Metrics metrics() throws IOException {
        return new Metrics();
    }

    @Bean
    public DefaultInteractionListener interactionListener(
            InteractionService service, IEventManager api, Messenger msg, Metrics metrics) {
        return new DefaultInteractionListener(service, api, msg, metrics);
    }

}
