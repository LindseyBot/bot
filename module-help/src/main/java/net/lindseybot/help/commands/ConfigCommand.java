package net.lindseybot.help.commands;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.profile.servers.PointConfig;
import net.lindseybot.shared.repositories.PointConfigRepository;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.ModalListener;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.UserError;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConfigCommand extends InteractionHandler {

    private final PointConfigRepository repository;

    public ConfigCommand(Messenger msg, PointConfigRepository repository) {
        super(msg);
        this.repository = repository;
    }

    @SlashCommand("lindsey.modules.config")
    public void onCommand(SlashCommandInteractionEvent event) {
        if (event.getMember() == null || event.getGuild() == null) {
            return;
        } else if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            return;
        }
        String module = this.getOption("name", event, String.class);
        if (module == null || module.isBlank()) {
            this.msg.error(event, Label.raw("Invalid module"));
            return;
        }
        Modal modal = this.points(event.getGuild().getIdLong());
        event.replyModal(modal)
                .queue();
    }

    @ModalListener("config")
    public void onModal(ModalInteractionEvent event) {
        if (event.getGuild() == null) {
            return;
        }
        PointConfig config = repository.findById(event.getGuild().getIdLong())
                .orElse(new PointConfig(event.getGuild().getIdLong()));

        boolean enabled = this.getOption("enabled", event, Boolean.class)
                .orElseThrow(() -> new UserError(Label.raw("Invalid option (enabled).")));
        config.setEnabled(enabled);

        int message = this.getOption("message-points", event, Integer.class)
                .orElse(1);
        config.setWeightMessage(message);

        int attachment = this.getOption("attachment-points", event, Integer.class)
                .orElse(1);
        config.setWeightAttachment(attachment);

        this.repository.save(config);
        if (enabled) {
            this.msg.reply(event, Label.raw("Enabled point system."));
        } else {
            this.msg.reply(event, Label.of("Disabled point system."));
        }
    }

    private Modal points(long id) {
        PointConfig config = repository.findById(id).orElse(new PointConfig());
        return Modal.create("config", "Update Points").addActionRows(ActionRow.partitionOf(
                TextInput.create("enabled", "Enabled", TextInputStyle.SHORT)
                        .build(),
                TextInput.create("message-points", "Points per Message", TextInputStyle.SHORT)
                        .setRequiredRange(1, 1)
                        .setValue(config.getWeightMessage() + "")
                        .build(),
                TextInput.create("attachment-points", "Points per Attachment", TextInputStyle.SHORT)
                        .setRequiredRange(1, 1)
                        .setValue(config.getWeightAttachment() + "")
                        .build()
        )).build();
    }

}
