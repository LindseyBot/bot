package net.lindseybot.help.services;

import net.dv8tion.jda.api.entities.Guild;
import net.lindseybot.shared.entities.profile.servers.AntiScam;
import net.lindseybot.help.repositories.sql.HelpAntiScamRepository;
import org.springframework.stereotype.Service;

@Service
public class HelpAntiScamService {

    private final HelpAntiScamRepository repository;

    public HelpAntiScamService(HelpAntiScamRepository repository) {
        this.repository = repository;
    }

    public AntiScam get(Guild guild) {
        return this.repository.findById(guild.getIdLong())
                .orElse(new AntiScam(guild.getIdLong()));
    }

    public void save(AntiScam antiScam) {
        this.repository.save(antiScam);
    }

}
