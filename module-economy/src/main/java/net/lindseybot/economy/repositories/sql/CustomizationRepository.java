package net.lindseybot.economy.repositories.sql;

import net.lindseybot.shared.entities.profile.users.Customization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomizationRepository extends JpaRepository<Customization, Long> {
}
