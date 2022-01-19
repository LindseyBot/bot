package net.lindseybot.economy.repositories.sql;

import net.lindseybot.shared.entities.items.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
}
