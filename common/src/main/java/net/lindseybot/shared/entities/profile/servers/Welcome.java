package net.lindseybot.shared.entities.profile.servers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "server_settings_welcome")
public class Welcome {

    @Id
    private long guild;

    private boolean enabled = false;
    private Long channelId;

    @Column(columnDefinition = "TEXT")
    private String message;

    public Welcome(long guild) {
        this.guild = guild;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Welcome wc) {
            return Objects.equals(wc.guild, this.guild);
        }
        return false;
    }

}
