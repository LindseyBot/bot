package net.lindseybot.economy.repositories.sql;

import net.lindseybot.shared.entities.profile.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
