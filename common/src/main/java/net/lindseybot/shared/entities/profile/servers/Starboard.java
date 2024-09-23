package net.lindseybot.shared.entities.profile.servers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "server_settings_starboard")
public class Starboard {

    @Id
    private long guild;

    private boolean enabled = false;
    private int minStars = 3;
    private Long channel;

    public Starboard(long guild) {
        this.guild = guild;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Starboard sb) {
            return Objects.equals(sb.guild, this.guild);
        }
        return false;
    }

}
