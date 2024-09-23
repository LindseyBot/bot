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
@Table(name = "server_settings_antiscam")
public class AntiScam {

    @Id
    private long guild;

    private boolean enabled = true;
    private int strikes = 3;

    public AntiScam(long guild) {
        this.guild = guild;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AntiScam sc) {
            return Objects.equals(sc.guild, this.guild);
        }
        return false;
    }

}
