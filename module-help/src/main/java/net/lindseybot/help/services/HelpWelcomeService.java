package net.lindseybot.help.services;

import net.dv8tion.jda.api.entities.Guild;
import net.lindseybot.shared.entities.profile.servers.Welcome;
import net.lindseybot.help.repositories.sql.HelpWelcomeRepository;
import org.springframework.stereotype.Service;

@Service
public class HelpWelcomeService {

    private final HelpWelcomeRepository repository;

    public HelpWelcomeService(HelpWelcomeRepository repository) {
        this.repository = repository;
    }

    public Welcome get(Guild guild) {
        return this.repository.findById(guild.getIdLong())
                .orElse(new Welcome(guild.getIdLong()));
    }

    public void save(Welcome welcome) {
        this.repository.save(welcome);
    }

}
