package net.lindseybot.shared.entities.discord;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FEmote {

    private long id;
    private String name;
    private boolean animated;

    public FEmote(long id, String name, boolean animated) {
        this.id = id;
        this.name = name;
        this.animated = animated;
    }

    /**
     * @return If this emote is unicode.
     */
    @JsonIgnore
    public boolean isUnicode() {
        return this.id == 0;
    }

    /**
     * Creates an emote from an unicode emoji codepoint.
     *
     * @param code Unicode.
     * @return Emote.
     */
    public static FEmote ofUnicode(String code) {
        return new FEmote(0, code, false);
    }

    /**
     * Returns this emote's mention representation, used in messages.
     *
     * @return Emote's mention.
     */
    @JsonIgnore
    public String asMention() {
        if (isUnicode()) {
            return this.name;
        } else {
            return "<" + (isAnimated() ? "a" : "") + ":" + this.getName() + ":" + this.getId() + ">";
        }
    }

    /**
     * Returns this emote's reaction representation, used in reactions.
     *
     * @return Emote's reaction name.
     */
    @JsonIgnore
    public String asReaction() {
        if (isUnicode()) {
            return this.name;
        } else {
            return this.getName() + ":" + this.getId();
        }
    }

}
