package net.lindseybot.shared.entities.profile.members;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@Entity
@IdClass(MemberId.class)
@Table(name = "member_points")
public class PointProfile {

    @Id
    private long userId;

    @Id
    private long guildId;

    private long points;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PointProfile history) {
            return Objects.equals(history.userId, this.userId)
                    && Objects.equals(history.guildId, this.guildId);
        }
        return false;
    }

}
