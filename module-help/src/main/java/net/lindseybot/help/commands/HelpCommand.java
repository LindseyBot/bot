package net.lindseybot.help.commands;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.discord.*;
import net.lindseybot.shared.entities.discord.builders.*;
import net.lindseybot.shared.utils.GFXUtils;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SelectMenu;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

import java.awt.*;

@Slf4j
@Component
public class HelpCommand extends InteractionHandler {

    private final Color color = GFXUtils.getColor("#c7bfb9");

    protected HelpCommand(Messenger msg) {
        super(msg);
    }

    @SlashCommand(value = "help", ephemeral = true)
    public void onCommand(SlashCommandInteractionEvent event) {
        String topic = this.getOption("topic", event, String.class);
        if (topic == null) {
            // send default
            this.msg.reply(event, this.getDefault());
        } else if ("economy".equalsIgnoreCase(topic)) {
            this.msg.reply(event, this.getEconomy());
        } else if ("moderation".equalsIgnoreCase(topic)) {
            this.msg.reply(event, this.getModeration());
        } else if ("automod".equalsIgnoreCase(topic)) {
            this.msg.reply(event, this.getAutomod());
        }
    }

    @SelectMenu(value = "help", ephemeral = true)
    public void onSelectMenu(SelectMenuInteractionEvent event) {
        String data = this.getSelected(event);
        if ("automod".equalsIgnoreCase(data)) {
            this.msg.edit(event, this.getAutomod());
        } else if ("economy".equalsIgnoreCase(data)) {
            this.msg.edit(event, this.getEconomy());
        } else if ("moderation".equalsIgnoreCase(data)) {
            this.msg.edit(event, this.getModeration());
        }
    }

    private FSelectMenu getSelectMenu() {
        return new SelectMenuBuilder("help")
                .addOption(new SelectOptionBuilder("automod", Label.raw("Automod"))
                        .withDescription(Label.of("commands.help.automod"))
                        .withEmote(FEmote.ofUnicode("\uD83E\uDD16"))
                        .build())
                .addOption(new SelectOptionBuilder("economy", Label.raw("Economy"))
                        .withDescription(Label.of("commands.help.economy"))
                        .withEmote(FEmote.ofUnicode("\uD83D\uDCB5"))
                        .build())
                .addOption(new SelectOptionBuilder("moderation", Label.raw("Moderation"))
                        .withDescription(Label.of("commands.help.moderation"))
                        .withEmote(FEmote.ofUnicode("\uD83D\uDD28"))
                        .build())
                .build();
    }

    private FMessage getDefault() {
        FEmbed embed = new EmbedBuilder()
                .title(Label.raw("Lindsey Help"))
                .color(this.color)
                .field(Label.raw("**Automod**"), Label.raw("`/help automod`"), false)
                .field(Label.raw("**Economy**"), Label.raw("`/help economy`"), false)
                .field(Label.raw("**Moderation**"), Label.raw("`/help moderation`"), false)
                .build();
        return new MessageBuilder()
                .ephemeral()
                .embed(embed)
                .addComponent(this.getSelectMenu())
                .build();
    }

    private FMessage getAutomod() {
        FEmbed embed = new EmbedBuilder()
                .title(Label.raw("Automod Help"))
                .color(this.color)
                .description(Label.of("commands.help.automod.text"))
                .field(Label.raw("**Anti-Advertising** - /lindsey configure antiad"),
                        Label.of("commands.help.automod.antiad"), false)
                .field(Label.raw("**Anti-Scam** - /lindsey configure antiscam"),
                        Label.of("commands.help.automod.antiscam"), false)
                .field(Label.raw("**Keep Roles** - /lindsey configure keeproles"),
                        Label.of("commands.help.automod.keeproles"), false)
                .build();
        return new MessageBuilder()
                .ephemeral()
                .embed(embed)
                .addComponent(this.getSelectMenu())
                .addComponent(new ButtonBuilder()
                        .secondary("module-configure", Label.raw("Anti-Advertising"))
                        .withEmote(FEmote.ofUnicode("\uD83D\uDCE2"))
                        .withData("antiad")
                        .build())
                .addComponent(new ButtonBuilder()
                        .secondary("module-configure", Label.raw("Anti-Scam"))
                        .withEmote(FEmote.ofUnicode("\uD83D\uDD17"))
                        .withData("antiscam")
                        .build())
                .addComponent(new ButtonBuilder()
                        .secondary("module-configure", Label.raw("KeepRoles"))
                        .withEmote(FEmote.ofUnicode("\uD83E\uDDF2"))
                        .withData("keeproles")
                        .build())
                .build();
    }

    private FMessage getEconomy() {
        FEmbed embed = new EmbedBuilder()
                .title(Label.raw("Economy Help"))
                .color(this.color)
                .description(Label.of("commands.help.economy.text"))
                .field(Label.raw("/cookies daily"), Label.of("commands.help.economy.daily"), false)
                .field(Label.raw("/leaderboard [name]"), Label.of("commands.help.economy.leaderboard"), false)
                .field(Label.raw("/scramble"), Label.of("commands.help.economy.scramble"), false)
                .field(Label.raw("/bet"), Label.of("commands.help.economy.bet"), false)
                .field(Label.raw("/blackjack"), Label.of("commands.help.economy.blackjack"), false)
                .build();
        return new MessageBuilder()
                .ephemeral()
                .embed(embed)
                .addComponent(this.getSelectMenu())
                .build();
    }

    private FMessage getModeration() {
        FEmbed embed = new EmbedBuilder()
                .title(Label.raw("Moderation Help"))
                .color(this.color)
                .field(Label.raw("/hackban"), Label.of("commands.help.moderation.hackban"), false)
                .field(Label.raw("/prune"), Label.of("commands.help.moderation.prune"), false)
                .field(Label.raw("/softban"), Label.of("commands.help.moderation.softban"), false)
                .field(Label.raw("/voice move [channel] [channel]"),
                        Label.of("commands.help.moderation.move"), false)
                .field(Label.raw("/voice split [channel] [channel]"),
                        Label.of("commands.help.moderation.split"), false)
                .build();
        return new MessageBuilder()
                .ephemeral()
                .embed(embed)
                .addComponent(this.getSelectMenu())
                .build();
    }

}
