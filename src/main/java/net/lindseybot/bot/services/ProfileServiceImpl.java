package net.lindseybot.bot.services;

import lombok.extern.slf4j.Slf4j;
import net.lindseybot.bot.repositories.sql.MemberRepository;
import net.lindseybot.bot.repositories.sql.ServerRepository;
import net.lindseybot.bot.repositories.sql.UserRepository;
import net.lindseybot.shared.entities.profile.MemberProfile;
import net.lindseybot.shared.entities.profile.ServerProfile;
import net.lindseybot.shared.entities.profile.UserProfile;
import net.lindseybot.shared.entities.profile.members.MemberId;
import net.lindseybot.shared.worker.services.ProfileService;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Slf4j
@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository users;
    private final MemberRepository members;
    private final ServerRepository servers;

    public ProfileServiceImpl(UserRepository users,
                              MemberRepository members,
                              ServerRepository servers) {
        this.users = users;
        this.members = members;
        this.servers = servers;
    }

    @Override
    public @NotNull ServerProfile getServer(long id) {
        return this.servers.findById(id)
                .orElse(new ServerProfile(id));
    }

    @Override
    public @NotNull UserProfile getUser(long id) {
        return this.users.findById(id)
                .orElse(new UserProfile(id));
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
        return this.users.save(profile);
    }

    @Override
    public @NotNull MemberProfile save(@NotNull MemberProfile profile) {
        return this.members.save(profile);
    }

    @Scheduled(cron = "0 0 * * *")
    public void onDaily() {
        long time = Instant.ofEpochMilli(System.currentTimeMillis())
                .truncatedTo(ChronoUnit.DAYS)
                .minus(3, ChronoUnit.DAYS)
                .toEpochMilli();
        long deleted = this.users.deleteOutdatedStreaks(time);
        log.info("Reset {} outdated cookie streaks.", deleted);
    }

    @Transactional
    public void updateSeen(Set<Long> guilds) {
        this.servers.updateLastSeen(System.currentTimeMillis(), guilds);
    }

    @Transactional
    public void updateName(long user, String name) {
        this.users.updateName(name, System.currentTimeMillis(), user);
    }

}
