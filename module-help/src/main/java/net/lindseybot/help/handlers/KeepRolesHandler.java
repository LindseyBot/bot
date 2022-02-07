package net.lindseybot.help.handlers;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.lindseybot.help.models.ModuleHandler;
import net.lindseybot.help.services.HelpKeepRolesService;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.discord.builders.ButtonBuilder;
import net.lindseybot.shared.entities.discord.builders.MessageBuilder;
import net.lindseybot.shared.entities.profile.servers.KeepRoles;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

@Component
public class KeepRolesHandler extends InteractionHandler implements ModuleHandler {

    private final HelpKeepRolesService service;

    public KeepRolesHandler(Messenger msg, HelpKeepRolesService service) {
        super(msg);
        this.service = service;
    }

    @Override
    public String getSlug() {
        return "keeproles";
    }

    @Override
    public Label getName() {
        return Label.of("commands.modules.keeproles");
    }

    @Override
    public Label description() {
        return Label.of("commands.modules.keeproles.text");
    }

    @Override
    public FMessage enable(Member member, Guild guild) {
        if (!guild.getSelfMember()
                .hasPermission(Permission.MANAGE_ROLES)) {
            return FMessage.of(Label.of("permissions.bot", Permission.MANAGE_ROLES.getName()), true);
        }
        KeepRoles keepRoles = this.service.get(guild);
        keepRoles.setEnabled(true);
        this.service.save(keepRoles);
        return this.onStatus(member, guild);
    }

    @Override
    public FMessage disable(Member member, Guild guild) {
        KeepRoles keepRoles = this.service.get(guild);
        keepRoles.setEnabled(false);
        this.service.save(keepRoles);
        return this.onStatus(member, guild);
    }

    @Override
    public FMessage onStatus(Member member, Guild guild) {
        KeepRoles keepRoles = this.service.get(guild);
        MessageBuilder builder = new MessageBuilder();
        if (keepRoles.isEnabled()) {
            builder.content(Label.of("commands.modules.keeproles.enabled"));
        } else {
            builder.content(Label.of("commands.modules.keeproles.disabled"));
        }
        builder.addComponent(new ButtonBuilder()
                .success("module-enable", Label.of("labels.enable"))
                .withData(this.getSlug())
                .disabled(keepRoles.isEnabled())
                .build());
        builder.addComponent(new ButtonBuilder()
                .danger("module-disable", Label.of("labels.disable"))
                .withData(this.getSlug())
                .disabled(!keepRoles.isEnabled())
                .build());
        return builder.build();
    }

    @Override
    public FMessage onSetupStart(Member member, Guild guild) {
        KeepRoles keepRoles = this.service.get(guild);
        MessageBuilder builder = new MessageBuilder();
        builder.content(Label.of("commands.lindsey.modules.configure.none"));
        builder.addComponent(new ButtonBuilder()
                .success("module-enable", Label.of("labels.enable"))
                .withData(this.getSlug())
                .disabled(keepRoles.isEnabled())
                .build());
        builder.addComponent(new ButtonBuilder()
                .danger("module-disable", Label.of("labels.disable"))
                .withData(this.getSlug())
                .disabled(!keepRoles.isEnabled())
                .build());
        return builder.build();
    }

}
