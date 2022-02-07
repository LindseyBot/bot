package net.lindseybot.help.handlers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.lindseybot.help.models.ModuleHandler;
import net.lindseybot.help.services.HelpAntiAdService;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.discord.builders.ButtonBuilder;
import net.lindseybot.shared.entities.discord.builders.MessageBuilder;
import net.lindseybot.shared.entities.profile.servers.AntiAd;
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
        return Label.of("commands.modules.antiad");
    }

    @Override
    public Label description() {
        return Label.of("commands.modules.antiad.text");
    }

    @Override
    public FMessage enable(Member member, Guild guild) {
        AntiAd antiAd = this.service.get(guild);
        antiAd.setEnabled(true);
        this.service.save(antiAd);
        return this.onStatus(member, guild);
    }

    @Override
    public FMessage disable(Member member, Guild guild) {
        AntiAd antiAd = this.service.get(guild);
        antiAd.setEnabled(false);
        this.service.save(antiAd);
        return this.onStatus(member, guild);
    }

    @Override
    public FMessage onStatus(Member member, Guild guild) {
        AntiAd antiAd = this.service.get(guild);
        MessageBuilder builder = new MessageBuilder();
        if (antiAd.isEnabled()) {
            builder.content(Label.of("commands.modules.antiad.enabled"));
        } else {
            builder.content(Label.of("commands.modules.antiad.disabled"));
        }
        builder.addComponent(new ButtonBuilder()
                .success("module-enable", Label.of("labels.enable"))
                .withData(this.getSlug())
                .disabled(antiAd.isEnabled())
                .build());
        builder.addComponent(new ButtonBuilder()
                .danger("module-disable", Label.of("labels.disable"))
                .withData(this.getSlug())
                .disabled(!antiAd.isEnabled())
                .build());
        return builder.build();
    }

    @Override
    public FMessage onSetupStart(Member member, Guild guild) {
        AntiAd antiAd = this.service.get(guild);
        MessageBuilder builder = new MessageBuilder();
        builder.content(Label.of("commands.lindsey.modules.configure.none"));
        builder.addComponent(new ButtonBuilder()
                .success("module-enable", Label.of("labels.enable"))
                .withData(this.getSlug())
                .disabled(antiAd.isEnabled())
                .build());
        builder.addComponent(new ButtonBuilder()
                .danger("module-disable", Label.of("labels.disable"))
                .withData(this.getSlug())
                .disabled(!antiAd.isEnabled())
                .build());
        return builder.build();
    }

}
