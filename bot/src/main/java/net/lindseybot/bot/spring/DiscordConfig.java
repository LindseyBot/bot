package net.lindseybot.bot.spring;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.lindseybot.shared.properties.BotProperties;
import net.lindseybot.shared.properties.ShardProperties;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.InteractionService;
import net.lindseybot.shared.worker.impl.DefaultInteractionListener;
import net.lindseybot.shared.worker.impl.DefaultInteractionService;
import net.lindseybot.shared.worker.impl.PooledEventManager;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;
import java.util.List;

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
    public DefaultInteractionListener interactionListener(
            InteractionService service, IEventManager api, Messenger msg) {
        return new DefaultInteractionListener(service, api, msg);
    }

}
