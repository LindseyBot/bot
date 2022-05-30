package net.lindseybot.points.services;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.lindseybot.shared.repositories.PointConfigRepository;
import net.lindseybot.points.repositories.PointRepository;
import net.lindseybot.shared.entities.profile.servers.PointConfig;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class PointService {

    private final PointRepository repository;
    private final PointConfigRepository configs;

    public PointService(
            PointRepository repository,
            PointConfigRepository configs) {
        this.repository = repository;
        this.configs = configs;
    }

    /**
     * Fetches the point configuration for this guild.
     *
     * @param guild Server.
     * @return PointConfig
     */
    public @NotNull PointConfig getConfig(Guild guild) {
        long id = guild.getIdLong();
        return this.configs.findById(id)
                .orElse(new PointConfig(id));
    }

    /**
     * Adds points to a member.
     *
     * @param member Member.
     * @param points Points.
     */
    public void add(Member member, long points) {
        this.repository.addPoints(member.getIdLong(), member.getGuild().getIdLong(), points);
    }

}
