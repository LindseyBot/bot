package net.lindseybot.points.repositories;

import net.lindseybot.shared.entities.profile.members.MemberId;
import net.lindseybot.shared.entities.profile.members.PointProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PointRepository extends JpaRepository<PointProfile, MemberId> {

    @Query(value = """
            insert into member_points (user_id, guild_id, points)
            values (?1, ?2, ?3)
            on duplicate key
            update points = points + ?3
            """, nativeQuery = true)
    void addPoints(long user, long guild, long points);

}
