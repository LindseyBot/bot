package net.lindseybot.shared.entities.profile.servers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
