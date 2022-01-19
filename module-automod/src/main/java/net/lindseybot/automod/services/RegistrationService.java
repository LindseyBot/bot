package net.lindseybot.automod.services;

import net.dv8tion.jda.api.entities.Guild;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import net.lindseybot.automod.repositories.sql.RegistrationRepository;
import net.lindseybot.shared.entities.profile.servers.Registration;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RegistrationService {

    private final RegistrationRepository repository;
    private final ExpiringMap<Long, Registration> cache = ExpiringMap.builder()
            .expirationPolicy(ExpirationPolicy.ACCESSED)
            .expiration(1, TimeUnit.MINUTES)
            .maxSize(10_000)
            .build();

    public RegistrationService(RegistrationRepository repository) {
        this.repository = repository;
    }

    public Registration find(Guild guild) {
        Registration cached = this.cache.get(guild.getIdLong());
        if (cached != null) {
            return cached;
        }
        Registration registration = repository.findById(guild.getIdLong())
                .orElse(new Registration(guild.getIdLong()));
        this.cache.put(guild.getIdLong(), registration);
        return registration;
    }

    public void disable(Registration registration) {
        registration.setEnabled(false);
        this.repository.save(registration);
        this.cache.remove(registration.getGuild());
    }

}
