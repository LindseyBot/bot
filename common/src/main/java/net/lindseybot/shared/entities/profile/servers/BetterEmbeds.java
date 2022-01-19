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
@Table(name = "server_settings_embeds")
public class BetterEmbeds {

    @Id
    private long guild;

    private boolean apoiase = true;
    private boolean catarse = true;
    private boolean kitsu = true;
    private boolean myAnimeList = true;
    private boolean picarto = true;

    public BetterEmbeds(long guild) {
        this.guild = guild;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BetterEmbeds be) {
            return Objects.equals(be.guild, this.guild);
        }
        return false;
    }

}
