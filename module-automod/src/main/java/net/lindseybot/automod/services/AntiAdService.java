package net.lindseybot.automod.services;

import net.dv8tion.jda.api.entities.Guild;
import net.lindseybot.automod.repositories.sql.AntiAdRepository;
import net.lindseybot.shared.entities.profile.servers.AntiAd;
import org.springframework.stereotype.Service;

@Service
public class AntiAdService {

    private final AntiAdRepository repository;

    public AntiAdService(AntiAdRepository repository) {
        this.repository = repository;
    }

    public AntiAd find(Guild guild) {
        return repository.findById(guild.getIdLong())
                .orElse(new AntiAd(guild.getIdLong()));
    }

}
