package net.lindseybot.economy.repositories.sql;

import net.lindseybot.shared.entities.profile.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    @Query(value = "SELECT FIND_IN_SET(cookies, (SELECT GROUP_CONCAT(cookies ORDER BY cookies DESC) FROM `user_settings`)) " +
            "AS rank FROM `user_settings` WHERE user = ?1", nativeQuery = true)
    long findCookieRank(long user);

    @Query(value = "SELECT FIND_IN_SET(slot_wins, (SELECT GROUP_CONCAT(slot_wins ORDER BY slot_wins DESC) FROM `user_settings`)) " +
            "AS rank FROM `user_settings` WHERE user = ?1", nativeQuery = true)
    long findSlotRank(long user);

    @Query(value = "SELECT FIND_IN_SET(cookie_streak, (SELECT GROUP_CONCAT(cookie_streak ORDER BY cookie_streak DESC) FROM `user_settings`)) " +
            "AS rank FROM `user_settings` WHERE user = ?1", nativeQuery = true)
    long findStreakRank(long user);

}
