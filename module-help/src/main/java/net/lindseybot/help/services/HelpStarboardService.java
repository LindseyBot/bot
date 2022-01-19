package net.lindseybot.help.services;

import net.dv8tion.jda.api.entities.Guild;
import net.lindseybot.shared.entities.profile.servers.Starboard;
import net.lindseybot.help.repositories.sql.HelpStarboardRepository;
import org.springframework.stereotype.Service;

@Service
public class HelpStarboardService {

    private final HelpStarboardRepository repository;

    public HelpStarboardService(HelpStarboardRepository repository) {
        this.repository = repository;
    }

    public Starboard get(Guild guild) {
        return this.repository.findById(guild.getIdLong())
                .orElse(new Starboard(guild.getIdLong()));
    }

    public void save(Starboard starboard) {
        this.repository.save(starboard);
    }

}
