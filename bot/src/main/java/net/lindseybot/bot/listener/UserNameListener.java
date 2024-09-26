package net.lindseybot.bot.listener;

import jakarta.annotation.PreDestroy;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import net.lindseybot.bot.services.ProfileServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

@Component
public class UserNameListener extends ListenerAdapter implements ExpirationListener<Long, String> {

    private final ProfileServiceImpl profiles;
    private final ExpiringMap<Long, String> users = ExpiringMap.builder()
            .expirationPolicy(ExpirationPolicy.CREATED)
            .expiration(1, TimeUnit.MINUTES)
            .expirationListener(this)
            .maxSize(15_000)
            .build();
    private final Queue<UserUpdate> queue = new ArrayDeque<>();

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
        this.queue.add(new UserUpdate(userId, name));
        if (queue.size() > 250) {
            List<UserUpdate> updates = queue.stream()
                    .limit(250).toList();
            this.profiles.updateNames(updates);
        }
    }

    @PreDestroy
    public void onDestroy() {
        List<UserUpdate> updates = queue.stream()
                .toList();
        this.profiles.updateNames(updates);
    }

    public record UserUpdate(long id, String name) {
    }

}
