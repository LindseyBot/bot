package net.lindseybot.shared.worker.impl;

import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

public interface IsFakeSlash {

    @NotNull
    Message getMessage();

}
