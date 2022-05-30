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
@Table(name = "server_settings_points")
public class PointConfig {

    @Id
    private long guild;

    private boolean enabled = false;

    private int weightAttachment = 1;
    private int weightMessage = 1;

    public PointConfig(long guild) {
        this.guild = guild;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PointConfig cfg) {
            return Objects.equals(cfg.guild, this.guild);
        }
        return false;
    }

}
