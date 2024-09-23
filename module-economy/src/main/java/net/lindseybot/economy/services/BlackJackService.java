package net.lindseybot.economy.services;

import net.jodah.expiringmap.ExpiringMap;
import net.lindseybot.economy.models.BlackjackModel;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class BlackJackService {

    private final ExpiringMap<Long, BlackjackModel> cache = ExpiringMap.builder()
            .expiration(5, TimeUnit.MINUTES)
            .build();

    public Optional<BlackjackModel> findById(Long userId) {
        return Optional.ofNullable(cache.get(userId));
    }

    public void delete(BlackjackModel model) {
        this.cache.remove(model.getId());
    }

    public void save(BlackjackModel model) {
        this.cache.put(model.getId(), model);
    }

}
