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

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class UserNameListener extends ListenerAdapter implements ExpirationListener<Long, String> {

    private final ProfileServiceImpl profiles;
    private final ExpiringMap<Long, String> users = ExpiringMap.builder()
            .expirationPolicy(ExpirationPolicy.CREATED)
            .expiration(5, TimeUnit.MINUTES)
            .asyncExpirationListener(this)
            .maxSize(50_000)
            .build();
    private final AtomicInteger tracker = new AtomicInteger(0);
    private final Queue<UserUpdate> queue = new ConcurrentLinkedQueue<>();

    public UserNameListener(IEventManager api, ProfileServiceImpl profiles) {
        this.profiles = profiles;
        api.register(this);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        var author = event.getAuthor();
        users.put(author.getIdLong(), author.getAsTag());
    }

    @Override
    public void expired(Long userId, String name) {
        this.queue.add(new UserUpdate(userId, name));
        int size = tracker.incrementAndGet();
        if (size > 250) {
            List<UserUpdate> updates = queue.stream()
                    .limit(250).toList();
            this.profiles.updateNames(updates);
            tracker.set(0);
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
