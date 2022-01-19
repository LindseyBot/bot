package net.lindseybot.bot.repositories.sql;

import net.lindseybot.shared.entities.profile.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserProfile, Long> {

    int countByUser(long id);

    @Modifying
    @Query("update UserProfile profile set profile.name = ?1, profile.lastSeen = ?2 where profile.user = ?3")
    void updateName(String name, long seen, long user);

    @Modifying
    @Query("update UserProfile pr set pr.cookieStreak = 0 where pr.cookieStreak > 0 and pr.lastDailyCookies < ?1")
    int deleteOutdatedStreaks(long timestamp);

}
