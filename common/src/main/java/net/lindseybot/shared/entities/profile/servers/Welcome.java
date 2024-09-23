package net.lindseybot.shared.entities.profile.servers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
