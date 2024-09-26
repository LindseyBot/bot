package net.lindseybot.automod.services;

import net.dv8tion.jda.api.entities.Guild;
import net.lindseybot.automod.repositories.sql.RegistrationRepository;
import net.lindseybot.shared.entities.profile.servers.Registration;
import net.lindseybot.shared.services.CacheService;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final RegistrationRepository repository;
    private final CacheService cache;

    public RegistrationService(RegistrationRepository repository, CacheService cache) {
        this.repository = repository;
        this.cache = cache;
    }

    public Registration find(Guild guild) {
        if (this.cache.getRegistration().containsKey(guild.getIdLong())) {
            return this.cache.getRegistration().get(guild.getIdLong());
        }
        var data = repository.findById(guild.getIdLong())
                .orElse(new Registration(guild.getIdLong()));
        this.cache.getRegistration().put(guild.getIdLong(), data);
        return data;
    }

    public void disable(Registration registration) {
        registration.setEnabled(false);
        this.repository.save(registration);
        this.cache.getRegistration().remove(registration.getGuild());
    }

}
