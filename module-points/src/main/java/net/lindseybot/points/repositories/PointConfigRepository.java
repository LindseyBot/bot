package net.lindseybot.points.repositories;

import net.lindseybot.points.entities.PointConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointConfigRepository extends JpaRepository<PointConfig, Long> {
}
