package net.lindseybot.help.handlers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.discord.builders.ButtonBuilder;
import net.lindseybot.shared.entities.discord.builders.EmbedBuilder;
import net.lindseybot.shared.entities.discord.builders.MessageBuilder;
import net.lindseybot.shared.entities.profile.servers.AntiAd;
import net.lindseybot.help.models.ModuleHandler;
import net.lindseybot.help.services.HelpAntiAdService;
import net.lindseybot.shared.utils.GFXUtils;
import net.lindseybot.shared.utils.StandardEmotes;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

@Component
public class AntiAdHandler extends InteractionHandler implements ModuleHandler {

    private final HelpAntiAdService service;

    public AntiAdHandler(Messenger msg, HelpAntiAdService service) {
        super(msg);
        this.service = service;
    }

    @Override
    public String getSlug() {
        return "antiad";
    }

    @Override
    public Label getName() {
        return Label.raw("Anti-Advertising");
    }

    @Override
    public Label description() {
        return Label.raw("Automatically deletes messages that contain invite links.");
    }

    @Override
    public FMessage enable(Member member, Guild guild) {
        AntiAd antiAd = this.service.get(guild);
        antiAd.setEnabled(true);
        this.service.save(antiAd);
        return this.onStatus(member, guild, false);
    }

    @Override
    public FMessage disable(Member member, Guild guild) {
        AntiAd antiAd = this.service.get(guild);
        antiAd.setEnabled(false);
        this.service.save(antiAd);
        return this.onStatus(member, guild, false);
    }

    @Override
    public FMessage onStatus(Member member, Guild guild, boolean setup) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.title(this.getName());
        if (setup) {
            embed.description(Label.raw(StandardEmotes.CHECK.asMention() + " Setup Finished!\n\nAnti-Advertising will " +
                    "automatically delete messages that contain invite links to other servers."));
        } else {
            embed.description(Label.raw("Anti-Advertising will automatically delete messages that " +
                    "contain invite links to other servers."));
        }
        embed.color(GFXUtils.YELLOW);
        embed.image("https://cdn.lindseybot.net/showcases/antiad.gif");

        AntiAd antiAd = this.service.get(guild);

        MessageBuilder builder = new MessageBuilder();
        builder.embed(embed.build());
        builder.addComponent(new ButtonBuilder()
                .danger("module-disable", Label.raw("Disable"))
                .withData(this.getSlug())
                .disabled(!antiAd.isEnabled())
                .build());
        builder.addComponent(new ButtonBuilder()
                .success("module-enable", Label.raw("Enable"))
                .withData(this.getSlug())
                .disabled(antiAd.isEnabled())
                .build());
        return builder.build();
    }

    @Override
    public FMessage onSetupStart(Member member, Guild guild) {
        return FMessage.of(Label.raw("No configuration exists for this module."), true);
    }

}
