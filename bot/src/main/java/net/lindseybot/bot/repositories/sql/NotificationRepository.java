package net.lindseybot.bot.repositories.sql;

import net.lindseybot.shared.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("select nt from Notification nt where nt.guildId = ?1 order by nt.timestamp asc")
    List<Notification> findByGuild(long guildId);

    @Query("select nt from Notification nt where nt.userId = ?1 order by nt.timestamp asc")
    List<Notification> findByUser(long userId);

    @Modifying
    @Query("delete from Notification nt where nt.timestamp < ?1")
    int deleteOutdated(long timestamp);

}
