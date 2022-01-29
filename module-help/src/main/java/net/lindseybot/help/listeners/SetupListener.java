package net.lindseybot.help.listeners;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.TimeFormat;
import net.lindseybot.help.models.ModuleHandler;
import net.lindseybot.shared.entities.Notification;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.worker.Button;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import net.lindseybot.shared.worker.services.NotificationService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SetupListener extends InteractionHandler {

    private final NotificationService notifications;
    private final Map<String, ModuleHandler> handlers = new HashMap<>();

    public SetupListener(Messenger msg, List<ModuleHandler> handlers, NotificationService notifications) {
        super(msg);
        this.notifications = notifications;
        handlers.forEach(h -> this.handlers.put(h.getSlug(), h));
    }

    public boolean hasPermission(Member member) {
        return member.isOwner() || member.hasPermission(Permission.MANAGE_SERVER);
    }

    @SlashCommand("lindsey.modules.list")
    public void onList(SlashCommandInteractionEvent event) {
        this.msg.reply(event, FMessage.of(
                Label.raw("Modules: antiad, antiscam, keeproles, registration, starboard, welcome"), true
        ));
    }

    @SlashCommand(value = "lindsey.modules.status", guildOnly = true)
    public void onStatus(SlashCommandInteractionEvent event) {
        String name = this.getOption("name", event, String.class);
        if (name == null || name.isBlank()) {
            return;
        } else if (event.getGuild() == null || event.getMember() == null) {
            this.msg.error(event, Label.of("error.guild"));
            return;
        } else if (!hasPermission(event.getMember())) {
            this.msg.error(event, Label.of("permissions.user"));
            return;
        }
        ModuleHandler handler = this.handlers.get(name);
        if (handler == null) {
            this.msg.error(event, Label.raw("Unknown module!"));
            return;
        }
        this.msg.reply(event, handler.onStatus(event.getMember(), event.getGuild(), false));
    }

    @SlashCommand(value = "lindsey.modules.configure", guildOnly = true)
    public void onSlashConfigure(SlashCommandInteractionEvent event) {
        String name = this.getOption("name", event, String.class);
        if (name == null || name.isBlank()) {
            return;
        } else if (event.getGuild() == null || event.getMember() == null) {
            this.msg.error(event, Label.of("error.guild"));
            return;
        } else if (!hasPermission(event.getMember())) {
            this.msg.error(event, Label.of("permissions.user"));
            return;
        }
        ModuleHandler handler = this.handlers.get(name);
        if (handler == null) {
            this.msg.error(event, Label.raw("Unknown module!"));
            return;
        }
        this.msg.reply(event, handler.onSetupStart(event.getMember(), event.getGuild()));
    }

    @SlashCommand(value = "lindsey.modules.enable", guildOnly = true)
    public void onSlashEnable(SlashCommandInteractionEvent event) {
        String name = this.getOption("name", event, String.class);
        if (name == null || name.isBlank()) {
            return;
        } else if (event.getGuild() == null || event.getMember() == null) {
            this.msg.error(event, Label.of("error.guild"));
            return;
        } else if (!hasPermission(event.getMember())) {
            this.msg.error(event, Label.of("permissions.user"));
            return;
        }
        ModuleHandler handler = this.handlers.get(name);
        if (handler == null) {
            this.msg.error(event, Label.raw("Unknown module!"));
            return;
        }
        this.msg.reply(event, handler.enable(event.getMember(), event.getGuild()));
    }

    @SlashCommand(value = "lindsey.modules.disable", guildOnly = true)
    public void onSlashDisable(SlashCommandInteractionEvent event) {
        String name = this.getOption("name", event, String.class);
        if (name == null || name.isBlank()) {
            return;
        } else if (event.getGuild() == null || event.getMember() == null) {
            this.msg.error(event, Label.of("error.guild"));
            return;
        } else if (!hasPermission(event.getMember())) {
            this.msg.error(event, Label.of("permissions.user"));
            return;
        }
        ModuleHandler handler = this.handlers.get(name);
        if (handler == null) {
            this.msg.error(event, Label.raw("Unknown module!"));
            return;
        }
        this.msg.reply(event, handler.disable(event.getMember(), event.getGuild()));
    }

    @SlashCommand(value = "lindsey.modules.logs", guildOnly = true)
    public void onSlashLogs(SlashCommandInteractionEvent event) {
        if (event.getGuild() == null || event.getMember() == null) {
            this.msg.error(event, Label.of("error.guild"));
            return;
        } else if (!hasPermission(event.getMember())) {
            this.msg.error(event, Label.of("permissions.user"));
            return;
        }
        List<Notification> notifications = this.notifications.findRecent(event.getGuild());
        if (notifications.isEmpty()) {
            this.msg.error(event, Label.of("commands.lindsey.modules.logs.empty"));
            return;
        }
        StringBuilder message = new StringBuilder();
        TimeFormat format = TimeFormat.RELATIVE;
        for (Notification notification : notifications) {
            message.append(format.format(notification.getTimestamp()))
                    .append(" : ")
                    .append(notification.getMessage())
                    .append("\n");
        }
        this.msg.reply(event, FMessage.of(Label.raw(message.toString()), true));
    }

    @Button("module-disable")
    public void onButtonDisable(ButtonInteractionEvent event) {
        String name = this.getData(event);
        if (name == null || name.isBlank()) {
            return;
        } else if (event.getGuild() == null || event.getMember() == null) {
            this.msg.error(event, Label.of("error.guild"));
            return;
        } else if (!hasPermission(event.getMember())) {
            this.msg.error(event, Label.of("permissions.user"));
            return;
        }
        ModuleHandler handler = this.handlers.get(name);
        if (handler == null) {
            this.msg.error(event, Label.raw("Unknown module!"));
            return;
        }
        this.msg.edit(event, handler.disable(event.getMember(), event.getGuild()));
    }

    @Button("module-enable")
    public void onButtonEnable(ButtonInteractionEvent event) {
        String name = this.getData(event);
        if (name == null || name.isBlank()) {
            return;
        } else if (event.getGuild() == null || event.getMember() == null) {
            this.msg.error(event, Label.of("error.guild"));
            return;
        } else if (!hasPermission(event.getMember())) {
            this.msg.error(event, Label.of("permissions.user"));
            return;
        }
        ModuleHandler handler = this.handlers.get(name);
        if (handler == null) {
            this.msg.error(event, Label.raw("Unknown module!"));
            return;
        }
        this.msg.edit(event, handler.enable(event.getMember(), event.getGuild()));
    }

    @Button("module-configure")
    public void onButtonConfigure(ButtonInteractionEvent event) {
        String name = this.getData(event);
        if (name == null || name.isBlank()) {
            return;
        } else if (event.getGuild() == null || event.getMember() == null) {
            this.msg.error(event, Label.of("error.guild"));
            return;
        } else if (!hasPermission(event.getMember())) {
            this.msg.error(event, Label.of("permissions.user"));
            return;
        }
        ModuleHandler handler = this.handlers.get(name);
        if (handler == null) {
            this.msg.error(event, Label.raw("Unknown module!"));
            return;
        }
        this.msg.edit(event, handler.onSetupStart(event.getMember(), event.getGuild()));
    }

}
