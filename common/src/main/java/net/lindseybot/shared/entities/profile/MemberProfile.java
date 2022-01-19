package net.lindseybot.shared.entities.profile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.lindseybot.shared.entities.profile.members.MemberId;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor
@IdClass(MemberId.class)
@Table(name = "member_settings")
public class MemberProfile {

    @Id
    private long userId;

    @Id
    private long guildId;

    private long lastSeen;

    public MemberProfile(long userId, long guildId) {
        this.userId = userId;
        this.guildId = guildId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MemberProfile profile) {
            return Objects.equals(profile.userId, this.userId)
                    && Objects.equals(profile.guildId, this.guildId);
        }
        return false;
    }

}
