package net.lindseybot.shared.entities.profile.servers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.lindseybot.shared.converters.LongSetStringConverter;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "server_settings_giveme")
public class GiveMe {

    @Id
    private long guild;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = LongSetStringConverter.class)
    private Set<Long> roles = new HashSet<>();

    public GiveMe(long guild) {
        this.guild = guild;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GiveMe gm) {
            return Objects.equals(gm.guild, this.guild);
        }
        return false;
    }

}
