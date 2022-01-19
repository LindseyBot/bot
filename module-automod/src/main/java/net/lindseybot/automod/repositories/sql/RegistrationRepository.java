package net.lindseybot.automod.repositories.sql;

import net.lindseybot.shared.entities.profile.servers.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
}
