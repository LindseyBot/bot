package net.lindseybot.bot.repositories.sql;

import net.lindseybot.shared.entities.profile.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserProfile, Long> {

    int countByUser(long id);

    @Modifying
    @Query("update UserProfile profile set profile.name = ?1, profile.lastSeen = ?2 where profile.user = ?3")
    void updateName(long user, long seen, String name);

}
