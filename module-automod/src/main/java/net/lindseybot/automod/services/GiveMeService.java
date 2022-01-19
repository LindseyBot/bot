package net.lindseybot.automod.services;

import net.dv8tion.jda.api.entities.Guild;
import net.lindseybot.automod.repositories.sql.GiveMeRepository;
import net.lindseybot.shared.entities.profile.servers.GiveMe;
import org.springframework.stereotype.Service;

@Service
public class GiveMeService {

    private final GiveMeRepository repository;

    public GiveMeService(GiveMeRepository repository) {
        this.repository = repository;
    }

    public GiveMe find(Guild guild) {
        return repository.findById(guild.getIdLong())
                .orElse(new GiveMe(guild.getIdLong()));
    }

    public void save(GiveMe giveMe) {
        this.repository.save(giveMe);
    }

}
