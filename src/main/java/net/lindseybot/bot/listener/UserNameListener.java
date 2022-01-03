package net.lindseybot.bot.listener;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import net.lindseybot.bot.services.ProfileServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class UserNameListener extends ListenerAdapter implements ExpirationListener<Long, String> {

    private final ProfileServiceImpl profiles;
    private final ExpiringMap<Long, String> users = ExpiringMap.builder()
            .expirationPolicy(ExpirationPolicy.ACCESSED)
            .expiration(5, TimeUnit.MINUTES)
            .asyncExpirationListener(this)
            .maxSize(15_000)
            .build();

    public UserNameListener(ShardManager api, ProfileServiceImpl profiles) {
        this.profiles = profiles;
        api.addEventListener(this);
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        users.put(event.getAuthor().getIdLong(), event.getAuthor().getAsTag());
    }

    @Override
    public void expired(Long userId, String name) {
        this.profiles.updateName(userId, name);
    }

}
