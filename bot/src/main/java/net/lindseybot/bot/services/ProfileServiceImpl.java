package net.lindseybot.bot.services;

import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import net.lindseybot.bot.listener.UserNameListener;
import net.lindseybot.bot.repositories.sql.MemberRepository;
import net.lindseybot.bot.repositories.sql.ServerRepository;
import net.lindseybot.bot.repositories.sql.UserRepository;
import net.lindseybot.shared.entities.profile.MemberProfile;
import net.lindseybot.shared.entities.profile.ServerProfile;
import net.lindseybot.shared.entities.profile.UserProfile;
import net.lindseybot.shared.entities.profile.members.MemberId;
import net.lindseybot.shared.worker.services.ProfileService;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository users;
    private final MemberRepository members;
    private final ServerRepository servers;
    private final JdbcTemplate template;

    private final ExpiringMap<Long, UserProfile> userCache = ExpiringMap.builder()
            .expirationPolicy(ExpirationPolicy.ACCESSED)
            .expiration(15, TimeUnit.MINUTES)
            .maxSize(10_000)
            .build();

    public ProfileServiceImpl(
            UserRepository users,
            MemberRepository members,
            ServerRepository servers,
            JdbcTemplate template) {
        this.users = users;
        this.members = members;
        this.servers = servers;
        this.template = template;
    }

    @Override
    public @NotNull ServerProfile getServer(long id) {
        return this.servers.findById(id)
                .orElse(new ServerProfile(id));
    }

    @Override
    public @NotNull UserProfile getUser(long id) {
        if (this.userCache.containsKey(id)) {
            return this.userCache.get(id);
        }
        UserProfile profile = this.users.findById(id)
                .orElse(new UserProfile(id));
        this.userCache.put(id, profile);
        return profile;
    }

    @Override
    public @NotNull MemberProfile getMember(long user, long guild) {
        MemberId id = new MemberId();
        id.setUserId(user);
        id.setGuildId(guild);
        return this.members.findById(id)
                .orElse(new MemberProfile(user, guild));
    }

    @Override
    public boolean hasServer(long id) {
        return this.servers.countByGuild(id) > 0;
    }

    @Override
    public boolean hasUser(long id) {
        return this.users.countByUser(id) > 0;
    }

    @Override
    public boolean hasMember(long user, long guild) {
        return this.members.countByGuildIdAndUserId(guild, user) > 0;
    }

    @Override
    public @NotNull ServerProfile save(@NotNull ServerProfile profile) {
        return this.servers.save(profile);
    }

    @Override
    public @NotNull UserProfile save(@NotNull UserProfile profile) {
        this.userCache.put(profile.getUser(), profile);
        return this.users.save(profile);
    }

    @Override
    public @NotNull MemberProfile save(@NotNull MemberProfile profile) {
        return this.members.save(profile);
    }

    @Transactional
    @Scheduled(cron = "0 0 * * * *")
    public void onDaily() {
        long time = Instant.ofEpochMilli(System.currentTimeMillis())
                .truncatedTo(ChronoUnit.DAYS)
                .minus(3, ChronoUnit.DAYS)
                .toEpochMilli();
        int deleted = this.users.deleteOutdatedStreaks(time);
        log.info("Reset {} outdated cookie streaks.", deleted);
    }

    @Transactional
    public void updateSeen(Set<Long> guilds) {
        this.servers.updateLastSeen(System.currentTimeMillis(), guilds);
    }

    @Transactional
    public void updateNames(List<UserNameListener.UserUpdate> updates) {
        this.template.batchUpdate("update user_settings set name = ?, last_seen = ? where user = ?",
                new BatchPreparedStatementSetter() {
                    public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                        var update = updates.get(i);
                        ps.setString(1, update.name());
                        ps.setLong(2, System.currentTimeMillis());
                        ps.setLong(3, update.id());
                    }

                    public int getBatchSize() {
                        return updates.size();
                    }
                });
    }

}
