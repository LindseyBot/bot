package net.lindseybot.automod.services;

import net.dv8tion.jda.api.entities.Guild;
import net.lindseybot.automod.repositories.sql.AntiScamRepository;
import net.lindseybot.shared.entities.profile.servers.AntiScam;
import org.springframework.stereotype.Service;

@Service
public class AntiScamService {

    private final AntiScamRepository repository;

    public AntiScamService(AntiScamRepository repository) {
        this.repository = repository;
    }

    public AntiScam find(Guild guild) {
        return repository.findById(guild.getIdLong())
                .orElse(new AntiScam(guild.getIdLong()));
    }

}
