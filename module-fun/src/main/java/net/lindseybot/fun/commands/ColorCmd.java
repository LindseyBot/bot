package net.lindseybot.fun.commands;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.lindseybot.shared.entities.discord.FEmbed;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.discord.builders.EmbedBuilder;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

import java.awt.*;

@Slf4j
@Component
public class ColorCmd extends InteractionHandler {

    protected ColorCmd(Messenger msg) {
        super(msg);
    }

    @SlashCommand("color")
    public void onCommand(SlashCommandInteractionEvent event) {
        String color = this.getOption("hex", event, String.class);
        if (color == null) {
            return;
        } else if (!color.startsWith("#")) {
            this.msg.error(event, Label.of("commands.color.invalid"));
            return;
        }
        String c = color.substring(1);
        FEmbed embed = new EmbedBuilder()
                .image("https://via.placeholder.com/128/" + c + "/" + c + ".png")
                .color(this.toColor(color))
                .build();
        this.msg.reply(event, FMessage.of(embed));
    }

    /**
     * @param hex - Hex string. eg: #FFFFFF or #FFF
     * @return Color object.
     */
    private Color toColor(String hex) {
        try {
            if (hex.length() == 7) {
                return new Color(
                        Integer.valueOf(hex.substring(1, 3), 16),
                        Integer.valueOf(hex.substring(3, 5), 16),
                        Integer.valueOf(hex.substring(5, 7), 16));
            } else if (hex.length() == 4) {
                return new Color(
                        Integer.valueOf(hex.substring(1, 2), 16),
                        Integer.valueOf(hex.substring(2, 3), 16),
                        Integer.valueOf(hex.substring(3, 4), 16));
            } else {
                return new Color(0, 0, 0, 0);
            }
        } catch (Exception ex) {
            return new Color(0, 0, 0, 0);
        }
    }

}
