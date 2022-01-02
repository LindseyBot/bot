package net.lindseybot.bot.repositories.sql;

import net.lindseybot.entities.profile.MemberProfile;
import net.lindseybot.entities.profile.members.MemberId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberProfile, MemberId> {

    int countByGuildIdAndUserId(long guild, long user);

}
