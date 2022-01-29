package net.lindseybot.fun.commands;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class Roll extends InteractionHandler {

    private final Random random = new Random();

    protected Roll(Messenger msg) {
        super(msg);
    }

    @SlashCommand("roll")
    public void onCommand(SlashCommandInteractionEvent event) {
        int sides;
        OptionMapping opt = event.getOption("sides");
        if (opt != null) {
            sides = Long.valueOf(opt.getAsLong()).intValue();
        } else {
            sides = 6;
        }
        this.msg.reply(event, Label.of("commands.roll.roll", getAsMention(event.getMember()),
                sides, random.nextInt(sides) + 1));
    }

}
