package net.lindseybot.help.handlers;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.lindseybot.help.models.ModuleHandler;
import net.lindseybot.help.services.HelpAntiScamService;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.discord.builders.ButtonBuilder;
import net.lindseybot.shared.entities.discord.builders.MessageBuilder;
import net.lindseybot.shared.entities.profile.servers.AntiScam;
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
        return Label.of("commands.modules.antiscam");
    }

    @Override
    public Label description() {
        return Label.of("commands.modules.antiscam.text");
    }

    @Override
    public FMessage enable(Member member, Guild guild) {
        if (!guild.getSelfMember()
                .hasPermission(Permission.MESSAGE_MANAGE)) {
            return FMessage.of(Label.of("permissions.bot", Permission.MESSAGE_MANAGE.getName()), true);
        }
        AntiScam antiScam = this.service.get(guild);
        antiScam.setEnabled(true);
        this.service.save(antiScam);
        return this.onStatus(member, guild);
    }

    @Override
    public FMessage disable(Member member, Guild guild) {
        AntiScam antiScam = this.service.get(guild);
        antiScam.setEnabled(false);
        this.service.save(antiScam);
        return this.onStatus(member, guild);
    }

    @Override
    public FMessage onStatus(Member member, Guild guild) {
        AntiScam antiScam = this.service.get(guild);
        MessageBuilder builder = new MessageBuilder();
        if (antiScam.isEnabled()) {
            builder.content(Label.of("commands.modules.antiscam.enabled"));
        } else {
            builder.content(Label.of("commands.modules.antiscam.disabled"));
        }
        builder.addComponent(new ButtonBuilder()
                .success("module-enable", Label.of("labels.enable"))
                .withData(this.getSlug())
                .disabled(antiScam.isEnabled())
                .build());
        builder.addComponent(new ButtonBuilder()
                .danger("module-disable", Label.of("labels.disable"))
                .withData(this.getSlug())
                .disabled(!antiScam.isEnabled())
                .build());
        return builder.build();
    }

    @Override
    public FMessage onSetupStart(Member member, Guild guild) {
        AntiScam antiScam = this.service.get(guild);
        MessageBuilder builder = new MessageBuilder();
        builder.content(Label.of("commands.lindsey.modules.configure.none"));
        builder.addComponent(new ButtonBuilder()
                .success("module-enable", Label.of("labels.enable"))
                .withData(this.getSlug())
                .disabled(antiScam.isEnabled())
                .build());
        builder.addComponent(new ButtonBuilder()
                .danger("module-disable", Label.of("labels.disable"))
                .withData(this.getSlug())
                .disabled(!antiScam.isEnabled())
                .build());
        return builder.build();
    }

}
