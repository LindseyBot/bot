package net.lindseybot.help.handlers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.discord.builders.ButtonBuilder;
import net.lindseybot.shared.entities.discord.builders.EmbedBuilder;
import net.lindseybot.shared.entities.discord.builders.MessageBuilder;
import net.lindseybot.shared.entities.profile.servers.AntiScam;
import net.lindseybot.help.models.ModuleHandler;
import net.lindseybot.help.services.HelpAntiScamService;
import net.lindseybot.shared.utils.GFXUtils;
import net.lindseybot.shared.utils.StandardEmotes;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

@Component
public class AntiScamHandler extends InteractionHandler implements ModuleHandler {

    private final HelpAntiScamService service;

    public AntiScamHandler(Messenger msg, HelpAntiScamService service) {
        super(msg);
        this.service = service;
    }

    @Override
    public String getSlug() {
        return "antiscam";
    }

    @Override
    public Label getName() {
        return Label.raw("Anti-Scam");
    }

    @Override
    public Label description() {
        return Label.raw("Automatically deletes messages that contain scam links.");
    }

    @Override
    public FMessage enable(Member member, Guild guild) {
        AntiScam antiScam = this.service.get(guild);
        antiScam.setEnabled(true);
        this.service.save(antiScam);
        return this.onStatus(member, guild, false);
    }

    @Override
    public FMessage disable(Member member, Guild guild) {
        AntiScam antiScam = this.service.get(guild);
        antiScam.setEnabled(false);
        this.service.save(antiScam);
        return this.onStatus(member, guild, false);
    }

    @Override
    public FMessage onStatus(Member member, Guild guild, boolean setup) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.title(this.getName());
        if (setup) {
            embed.description(Label.raw(StandardEmotes.CHECK.asMention() + " Setup Finished!\n\nAnti-Scam will " +
                    "automatically delete messages that contain scam links from the server."));
        } else {
            embed.description(Label.raw("Anti-Scam will automatically delete messages that contain scam " +
                    "links from the server."));
        }
        embed.color(GFXUtils.YELLOW);
        embed.image("https://cdn.lindseybot.net/showcases/antiscam.gif");

        AntiScam antiScam = this.service.get(guild);

        MessageBuilder builder = new MessageBuilder();
        builder.embed(embed.build());
        builder.addComponent(new ButtonBuilder()
                .danger("module-disable", Label.raw("Disable"))
                .withData(this.getSlug())
                .disabled(!antiScam.isEnabled())
                .build());
        builder.addComponent(new ButtonBuilder()
                .success("module-enable", Label.raw("Enable"))
                .withData(this.getSlug())
                .disabled(antiScam.isEnabled())
                .build());
        return builder.build();
    }

    @Override
    public FMessage onSetupStart(Member member, Guild guild) {
        return FMessage.of(Label.raw("No configuration exists for this module."), true);
    }

}
