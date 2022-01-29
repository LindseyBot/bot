package net.lindseybot.fun.commands;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class Flip extends InteractionHandler {

    private final Random random = new Random();

    protected Flip(Messenger msg) {
        super(msg);
    }

    @SlashCommand("flip")
    public void onCommand(SlashCommandInteractionEvent event) {
        if (random.nextBoolean()) {
            this.msg.reply(event, Label.of("commands.flip.heads", getAsMention(event.getMember())));
        } else {
            this.msg.reply(event, Label.of("commands.flip.tails", getAsMention(event.getMember())));
        }
    }

}
