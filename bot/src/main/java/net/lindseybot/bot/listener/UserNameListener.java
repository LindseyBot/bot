package net.lindseybot.bot.listener;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
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
            .expirationPolicy(ExpirationPolicy.CREATED)
            .expiration(1, TimeUnit.MINUTES)
            .asyncExpirationListener(this)
            .maxSize(15_000)
            .build();

    public UserNameListener(IEventManager api, ProfileServiceImpl profiles) {
        this.profiles = profiles;
        api.register(this);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        long userId = event.getAuthor().getIdLong();
        if (users.containsKey(userId)) {
            return;
        }
        users.put(userId, event.getAuthor().getAsTag());
    }

    @Override
    public void expired(Long userId, String name) {
        this.profiles.updateName(userId, name);
    }

}
