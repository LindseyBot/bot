package net.lindseybot.shared.repositories;

import net.lindseybot.shared.entities.profile.servers.PointConfig;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointConfigRepository extends JpaRepository<PointConfig, Long> {

    @Override
    @Cacheable("point-config")
    @NotNull Optional<PointConfig> findById(@NotNull Long aLong);

    @Override
    @CacheEvict(value = "point-config", key = "#p0.guild")
    <S extends PointConfig> @NotNull S save(@NotNull S entity);

}
