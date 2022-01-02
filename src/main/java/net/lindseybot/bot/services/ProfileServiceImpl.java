package net.lindseybot.bot.services;

import net.lindseybot.bot.repositories.sql.MemberRepository;
import net.lindseybot.bot.repositories.sql.ServerRepository;
import net.lindseybot.bot.repositories.sql.UserRepository;
import net.lindseybot.entities.profile.MemberProfile;
import net.lindseybot.entities.profile.ServerProfile;
import net.lindseybot.entities.profile.UserProfile;
import net.lindseybot.entities.profile.members.MemberId;
import net.lindseybot.worker.services.ProfileService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

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

}
