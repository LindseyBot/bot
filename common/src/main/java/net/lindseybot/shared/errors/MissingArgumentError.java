package net.lindseybot.shared.errors;

import lombok.Getter;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.worker.UserError;

@Getter
public class MissingArgumentError extends UserError {

    private final String name;

    public MissingArgumentError(String name) {
        super(Label.of("error.argument", name));
        this.name = name;
    }

}
