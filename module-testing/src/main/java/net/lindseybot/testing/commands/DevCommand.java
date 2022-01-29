package net.lindseybot.testing.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.lindseybot.testing.services.CommandMappingService;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DevCommand extends InteractionHandler {

    private final CommandMappingService service;

    public DevCommand(Messenger messenger, CommandMappingService service) {
        super(messenger);
        this.service = service;
    }

    private boolean canNotExecute(SlashCommandInteractionEvent event) {
        return event.getUser().getIdLong() != 87166524837613568L
                && event.getUser().getIdLong() != 119566649731842049L;
    }

    @SlashCommand("dev.commands.list")
    public void onList(SlashCommandInteractionEvent event) {
        if (canNotExecute(event) || event.getGuild() == null) {
            this.msg.error(event, Label.raw(":XCHECK: No."));
            return;
        }
        String filter = this.getOption("filter", event, String.class);
        if ("known".equals(filter)) {
            String list = this.service.getCommands().stream()
                    .map(CommandData::getName)
                    .sorted()
                    .collect(Collectors.joining(", "));
            this.msg.reply(event, Label.raw("Known Commands: " + list));
        } else if ("guild".equals(filter)) {
            String list = event.getGuild().retrieveCommands()
                    .complete().stream()
                    .map(Command::getName)
                    .sorted()
                    .collect(Collectors.joining(", "));
            this.msg.reply(event, Label.raw("Guild Commands: " + list));
        } else {
            String list = event.getJDA().retrieveCommands()
                    .complete().stream()
                    .map(Command::getName)
                    .sorted()
                    .collect(Collectors.joining(", "));
            this.msg.reply(event, Label.raw("All Commands: " + list));
        }
    }

    @SlashCommand("dev.commands.remove")
    public void onRemove(SlashCommandInteractionEvent event) {
        if (canNotExecute(event) || event.getGuild() == null) {
            this.msg.error(event, Label.raw(":XCHECK: No."));
            return;
        }
        String name = this.getOption("name", event, String.class);

        boolean isGlobal = false;
        Boolean global = this.getOption("global", event, Boolean.class);
        if (global != null && global) {
            isGlobal = true;
        }
        if ("all".equals(name)) {
            if (isGlobal) {
                for (Command command : event.getJDA().retrieveCommands()
                        .complete()) {
                    command.delete().queue();
                }
                this.msg.reply(event, Label.raw("Removed all commands globally."));
            } else {
                for (Command command : event.getGuild().retrieveCommands()
                        .complete()) {
                    if ("dev".equals(command.getName())) {
                        continue;
                    }
                    command.delete().queue();
                }
                this.msg.reply(event, Label.raw("Removed all commands locally."));
            }
        } else {
            if (isGlobal) {
                Command target = event.getJDA().retrieveCommands()
                        .complete().stream()
                        .filter((cmd) -> cmd.getName().equalsIgnoreCase(name))
                        .findFirst().orElse(null);
                if (target == null) {
                    this.msg.error(event, Label.raw("Invalid command"));
                    return;
                }
                target.delete().queue();
                this.msg.reply(event, Label.raw("Removed " + name + " globally."));
            } else if (event.getGuild() != null) {
                Command target = event.getGuild().retrieveCommands()
                        .complete().stream()
                        .filter((cmd) -> cmd.getName().equalsIgnoreCase(name))
                        .findFirst().orElse(null);
                if (target == null) {
                    this.msg.error(event, Label.raw("Invalid command"));
                    return;
                }
                target.delete().queue();
                this.msg.reply(event, Label.raw("Removed " + name + " locally."));
            }
        }
    }

    @SlashCommand("dev.commands.publish")
    public void onPublish(SlashCommandInteractionEvent event) {
        if (canNotExecute(event)) {
            this.msg.error(event, Label.raw(":XCHECK: No."));
            return;
        }
        String name = this.getOption("name", event, String.class);
        if ("all".equals(name)) {
            boolean isGlobal = false;
            Boolean global = this.getOption("global", event, Boolean.class);
            if (global != null && global) {
                isGlobal = true;
            }
            for (CommandData data : this.service.getCommands()) {
                if ("dev".equals(data.getName())) {
                    continue;
                }
                if (isGlobal) {
                    event.getJDA().upsertCommand(data)
                            .queue();

                } else if (event.getGuild() != null) {
                    event.getGuild().upsertCommand(data)
                            .queue();
                }
            }
            if (isGlobal) {
                this.msg.reply(event, Label.raw("Published all commands globally."));
            } else {
                this.msg.reply(event, Label.raw("Published all commands locally."));
            }
        } else {
            CommandData data = this.service.getByName(name);
            if (data == null) {
                this.msg.error(event, Label.raw(":XCHECK: Command not found."));
                return;
            }
            boolean isGlobal = false;
            Boolean global = this.getOption("global", event, Boolean.class);
            if (global != null && global) {
                isGlobal = true;
            }
            if (isGlobal) {
                event.getJDA().upsertCommand(data)
                        .queue();
                this.msg.reply(event, Label.raw("Published " + data.getName() + " globally."));
            } else if (event.getGuild() != null) {
                event.getGuild().upsertCommand(data)
                        .queue();
                this.msg.reply(event, Label.raw("Published " + data.getName() + " locally."));
            }
        }
    }

}
