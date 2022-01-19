package net.lindseybot.shared.entities.discord;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import net.lindseybot.shared.utils.DiscordCDN;

@Data
public class FUser {

    private long id;
    private String name;
    private String discriminator;
    private String avatarHash;
    private boolean bot;
    private int flags;

    @JsonIgnore
    public String getAsMention() {
        return "<@" + this.id + ">";
    }

    @JsonIgnore
    public String getAvatarUrl() {
        if (this.avatarHash != null) {
            return DiscordCDN.avatarUrl(this.avatarHash, this.id);
        } else {
            return DiscordCDN.defaultAvatar(this.discriminator);
        }
    }

}
