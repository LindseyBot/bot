package net.lindseybot.automod.services;

import net.dv8tion.jda.api.entities.Guild;
import net.lindseybot.automod.repositories.sql.AntiScamRepository;
import net.lindseybot.shared.entities.profile.servers.AntiScam;
import net.lindseybot.shared.services.CacheService;
import org.springframework.stereotype.Service;

@Service
public class AntiScamService {

    private final AntiScamRepository repository;
    private final CacheService cache;

    public AntiScamService(AntiScamRepository repository, CacheService cache) {
        this.repository = repository;
        this.cache = cache;
    }

    public AntiScam find(Guild guild) {
        if (this.cache.getAntiScam().containsKey(guild.getIdLong())) {
            return this.cache.getAntiScam().get(guild.getIdLong());
        }
        var data = repository.findById(guild.getIdLong())
                .orElse(new AntiScam(guild.getIdLong()));
        this.cache.getAntiScam().put(guild.getIdLong(), data);
        return data;
    }

}
