package net.lindseybot.points.services;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.jodah.expiringmap.ExpiringMap;
import net.lindseybot.points.entities.PointConfig;
import net.lindseybot.points.repositories.PointConfigRepository;
import net.lindseybot.points.repositories.PointRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class PointService {

    private final PointRepository repository;
    private final PointConfigRepository configs;

    private final ExpiringMap<Long, PointConfig> cache;

    public PointService(
            PointRepository repository,
            PointConfigRepository configs,
            ExpiringMap<Long, PointConfig> cache) {
        this.repository = repository;
        this.configs = configs;
        this.cache = cache;
    }

    /**
     * Fetches the point configuration for this guild.
     *
     * @param guild Server.
     * @return PointConfig
     */
    public @NotNull PointConfig getConfig(Guild guild) {
        long id = guild.getIdLong();
        if (this.cache.containsKey(id)) {
            return this.cache.get(id);
        }
        PointConfig config = this.configs.findById(id)
                .orElse(new PointConfig(id));
        this.cache.put(id, config);
        return config;
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
