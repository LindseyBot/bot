package net.lindseybot.shared.entities.profile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.lindseybot.shared.entities.profile.members.MemberId;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
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
