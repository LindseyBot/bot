package net.lindseybot.shared.worker.services;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.lindseybot.shared.entities.profile.MemberProfile;
import net.lindseybot.shared.entities.profile.ServerProfile;
import net.lindseybot.shared.entities.profile.UserProfile;
import org.jetbrains.annotations.NotNull;

public interface ProfileService {

    default @NotNull ServerProfile get(@NotNull Guild guild) {
        return this.getServer(guild.getIdLong());
    }

    default @NotNull UserProfile get(@NotNull User user) {
        return this.getUser(user.getIdLong());
    }

    default @NotNull MemberProfile get(@NotNull Member member) {
        return this.getMember(member.getIdLong(), member.getGuild().getIdLong());
    }

    @NotNull
    ServerProfile getServer(long id);

    @NotNull
    UserProfile getUser(long id);

    @NotNull
    MemberProfile getMember(long user, long guild);

    default boolean has(@NotNull Guild guild) {
        return this.hasServer(guild.getIdLong());
    }

    default boolean has(@NotNull User user) {
        return this.hasUser(user.getIdLong());
    }

    default boolean has(@NotNull Member member) {
        return this.hasMember(member.getIdLong(), member.getGuild().getIdLong());
    }

    boolean hasServer(long id);

    boolean hasUser(long id);

    boolean hasMember(long user, long guild);

    @NotNull
    ServerProfile save(@NotNull ServerProfile profile);

    @NotNull
    UserProfile save(@NotNull UserProfile profile);

    @NotNull
    MemberProfile save(@NotNull MemberProfile profile);

}
