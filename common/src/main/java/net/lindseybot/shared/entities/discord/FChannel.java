package net.lindseybot.shared.entities.discord;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class FChannel {

    private long id;
    private String name;
    private FChannelType type;
    private Integer position;
    private Boolean nsfw;

    private String permissions;

    @JsonIgnore
    public String getAsMention() {
        return "<#" + this.id + ">";
    }

}
