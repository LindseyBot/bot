package net.lindseybot.help.services;

import net.dv8tion.jda.api.entities.Guild;
import net.lindseybot.shared.entities.profile.servers.Registration;
import net.lindseybot.help.repositories.sql.HelpRegisterRepository;
import org.springframework.stereotype.Service;

@Service
public class HelpRegisterService {

    private final HelpRegisterRepository repository;

    public HelpRegisterService(HelpRegisterRepository repository) {
        this.repository = repository;
    }

    public Registration get(Guild guild) {
        return this.repository.findById(guild.getIdLong())
                .orElse(new Registration(guild.getIdLong()));
    }

    public void save(Registration registration) {
        this.repository.save(registration);
    }

}
