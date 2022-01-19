package net.lindseybot.shared.entities.discord;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class FRole {

    private long id;
    private String name;
    private int color;
    private boolean hoisted;
    private int position;
    private String permissions;
    private boolean managed;
    private boolean mentionable;

    @JsonIgnore
    public String getAsMention() {
        return "<@&" + this.id + ">";
    }

}
