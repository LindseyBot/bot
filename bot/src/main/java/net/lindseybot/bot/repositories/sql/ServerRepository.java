package net.lindseybot.bot.repositories.sql;

import net.lindseybot.shared.entities.profile.ServerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface ServerRepository extends JpaRepository<ServerProfile, Long> {

    int countByGuild(long id);

    @Modifying
    @Query("update ServerProfile profile set profile.lastSeen = ?1 where profile.guild in (?2)")
    void updateLastSeen(long timestamp, Set<Long> servers);

    @Query("select profile.guild from ServerProfile profile where profile.lastSeen < ?1")
    List<Long> findGuildByLastSeenLessThan(long timestamp);

}
