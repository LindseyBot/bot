package net.lindseybot.help.services;

import net.dv8tion.jda.api.entities.Guild;
import net.lindseybot.shared.entities.profile.servers.KeepRoles;
import net.lindseybot.help.repositories.sql.HelpKeepRolesRepository;
import org.springframework.stereotype.Service;

@Service
public class HelpKeepRolesService {

    private final HelpKeepRolesRepository repository;

    public HelpKeepRolesService(HelpKeepRolesRepository repository) {
        this.repository = repository;
    }

    public KeepRoles get(Guild guild) {
        return this.repository.findById(guild.getIdLong())
                .orElse(new KeepRoles(guild.getIdLong()));
    }

    public void save(KeepRoles keepRoles) {
        this.repository.save(keepRoles);
    }

}
