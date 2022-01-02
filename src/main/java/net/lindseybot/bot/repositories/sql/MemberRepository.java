package net.lindseybot.bot.repositories.sql;

import net.lindseybot.shared.entities.profile.MemberProfile;
import net.lindseybot.shared.entities.profile.members.MemberId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberProfile, MemberId> {

    int countByGuildIdAndUserId(long guild, long user);

}
