package net.lindseybot.shared.entities.profile.members;

import lombok.Getter;
import lombok.Setter;
import net.lindseybot.shared.converters.LongSetStringConverter;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@IdClass(MemberId.class)
@Table(name = "member_roles")
public class RoleHistory {

    @Id
    private long userId;

    @Id
    private long guildId;

    private long lastUpdated;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = LongSetStringConverter.class)
    private Set<Long> roles = new HashSet<>();

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RoleHistory history) {
            return Objects.equals(history.userId, this.userId)
                    && Objects.equals(history.guildId, this.guildId);
        }
        return false;
    }

}
