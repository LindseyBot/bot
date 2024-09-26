package net.lindseybot.automod.services;

import net.dv8tion.jda.api.entities.Guild;
import net.lindseybot.automod.repositories.sql.AntiAdRepository;
import net.lindseybot.shared.entities.profile.servers.AntiAd;
import net.lindseybot.shared.services.CacheService;
import org.springframework.stereotype.Service;

@Service
public class AntiAdService {

    private final AntiAdRepository repository;
    private final CacheService cache;

    public AntiAdService(AntiAdRepository repository, CacheService cache) {
        this.repository = repository;
        this.cache = cache;
    }

    public AntiAd find(Guild guild) {
        if (this.cache.getAntiAd().containsKey(guild.getIdLong())) {
            return this.cache.getAntiAd().get(guild.getIdLong());
        }
        var data = repository.findById(guild.getIdLong())
                .orElse(new AntiAd(guild.getIdLong()));
        this.cache.getAntiAd().put(guild.getIdLong(), data);
        return data;
    }

}
