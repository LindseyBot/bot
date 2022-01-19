package net.lindseybot.help.services;

import net.dv8tion.jda.api.entities.Guild;
import net.lindseybot.shared.entities.profile.servers.AntiAd;
import net.lindseybot.help.repositories.sql.HelpAntiAdRepository;
import org.springframework.stereotype.Service;

@Service
public class HelpAntiAdService {

    private final HelpAntiAdRepository repository;

    public HelpAntiAdService(HelpAntiAdRepository repository) {
        this.repository = repository;
    }

    public AntiAd get(Guild guild) {
        return this.repository.findById(guild.getIdLong())
                .orElse(new AntiAd(guild.getIdLong()));
    }

    public void save(AntiAd antiAd) {
        this.repository.save(antiAd);
    }

}
