package net.lindseybot.shared.worker;

import lombok.Getter;
import net.lindseybot.shared.entities.discord.Label;

public class UserError extends RuntimeException {

    @Getter
    private final Label label;

    public UserError(String message) {
        this(Label.raw(message));
    }

    public UserError(Label label) {
        this.label = label;
    }

}
