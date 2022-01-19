package net.lindseybot.help.handlers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.lindseybot.help.models.ModuleHandler;
import net.lindseybot.help.services.HelpKeepRolesService;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.discord.builders.ButtonBuilder;
import net.lindseybot.shared.entities.discord.builders.EmbedBuilder;
import net.lindseybot.shared.entities.discord.builders.MessageBuilder;
import net.lindseybot.shared.entities.profile.servers.KeepRoles;
import net.lindseybot.shared.utils.GFXUtils;
import net.lindseybot.shared.utils.StandardEmotes;
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
        return Label.raw("KeepRoles");
    }

    @Override
    public Label description() {
        return Label.raw("Adds roles to users back after they rejoin the server.");
    }

    @Override
    public FMessage enable(Member member, Guild guild) {
        KeepRoles keepRoles = this.service.get(guild);
        keepRoles.setEnabled(true);
        this.service.save(keepRoles);
        return this.onStatus(member, guild, false);
    }

    @Override
    public FMessage disable(Member member, Guild guild) {
        KeepRoles keepRoles = this.service.get(guild);
        keepRoles.setEnabled(false);
        this.service.save(keepRoles);
        return this.onStatus(member, guild, false);
    }

    @Override
    public FMessage onStatus(Member member, Guild guild, boolean setup) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.title(this.getName());
        if (setup) {
            embed.description(Label.raw(StandardEmotes.CHECK.asMention() + " Setup Finished!\n\nKeepRoles will re-add roles to users if they leave/join the server."));
        } else {
            embed.description(Label.raw("KeepRoles will re-add roles to users if they leave/join the server."));
        }
        embed.color(GFXUtils.RED);
        //embed.image("https://cdn.lindseybot.net/showcases/keeproles.gif");

        KeepRoles keepRoles = this.service.get(guild);

        MessageBuilder builder = new MessageBuilder();
        builder.embed(embed.build());
        builder.addComponent(new ButtonBuilder()
                .danger("module-disable", Label.raw("Disable"))
                .withData(this.getSlug())
                .disabled(!keepRoles.isEnabled())
                .build());
        builder.addComponent(new ButtonBuilder()
                .success("module-enable", Label.raw("Enable"))
                .withData(this.getSlug())
                .disabled(keepRoles.isEnabled())
                .build());
        return builder.build();
    }

    @Override
    public FMessage onSetupStart(Member member, Guild guild) {
        return FMessage.of(Label.raw("No configuration exists for this module."), true);
    }

}
