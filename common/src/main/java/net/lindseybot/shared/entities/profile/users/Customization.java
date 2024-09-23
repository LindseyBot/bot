package net.lindseybot.shared.entities.profile.users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.lindseybot.shared.converters.LongListJsonConverter;
import net.lindseybot.shared.entities.items.Background;

import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "user_settings_customization")
public class Customization {

    @Id
    private long user;

    @ManyToOne
    private Background background;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = LongListJsonConverter.class)
    private List<Long> badges;

    public Customization(long user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Customization uc) {
            return Objects.equals(uc.user, this.user);
        }
        return false;
    }

}
