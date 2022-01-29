package net.lindseybot.automod.commands;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.lindseybot.automod.services.GiveMeService;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.profile.servers.GiveMe;
import net.lindseybot.shared.worker.AutoComplete;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class GiveMeCommand extends InteractionHandler {

    private final GiveMeService service;

    public GiveMeCommand(Messenger msg, GiveMeService service) {
        super(msg);
        this.service = service;
    }

    @SlashCommand(value = "giveme.list", guildOnly = true)
    public void onList(SlashCommandInteractionEvent event) {
        if (event.getMember() == null
                || event.getGuild() == null) {
            return;
        }
        GiveMe giveMe = this.service.find(event.getGuild());
        if (giveMe.getRoles().isEmpty()) {
            this.msg.error(event, Label.of("commands.giveme.none"));
            return;
        }
        String roles = giveMe.getRoles().stream()
                .map(role -> event.getGuild().getRoleById(role))
                .filter(Objects::nonNull)
                .map(Role::getName)
                .collect(Collectors.joining(", "));
        this.msg.reply(event, FMessage.of(Label.of("commands.giveme.all", roles), true));
    }

    @SlashCommand(value = "giveme.get", guildOnly = true)
    public void onGet(SlashCommandInteractionEvent event) {
        if (event.getMember() == null
                || event.getGuild() == null) {
            return;
        }
        String roleId = this.getOption("name", event, String.class);
        if (roleId == null || !roleId.matches("[0-9]+")) {
            this.msg.error(event, Label.of("search.role"));
            return;
        }
        Role role = event.getGuild().getRoleById(roleId);
        if (role == null) {
            this.msg.error(event, Label.of("search.role"));
            return;
        }
        GiveMe giveMe = this.service.find(event.getGuild());
        if (!giveMe.getRoles().contains(role.getIdLong())) {
            this.msg.error(event, Label.of("commands.giveme.invalid"));
            return;
        }
        try {
            event.getGuild().addRoleToMember(event.getMember(), role).queue((s) -> {
                this.msg.reply(event, FMessage.of(Label.of("commands.giveme.success", role.getName()), true));
            }, (e) -> {
                this.msg.error(event, Label.of("error.discord", e.getMessage()));
            });
        } catch (InsufficientPermissionException ex) {
            this.msg.error(event, Label.of("permissions.bot", ex.getPermission().getName()));
        } catch (HierarchyException ex) {
            this.msg.error(event, Label.of("permissions.hierarchy"));
        }
    }

    @SlashCommand(value = "giveme.add", guildOnly = true)
    public void onAdd(SlashCommandInteractionEvent event) {
        if (event.getMember() == null
                || event.getGuild() == null) {
            return;
        } else if (!this.hasPermission(event.getMember())) {
            this.msg.error(event, Label.of("permissions.user"));
            return;
        }
        Role role = this.getOption("name", event, Role.class);
        if (role == null || role.isPublicRole()) {
            this.msg.error(event, Label.of("search.role"));
            return;
        } else if (this.isDangerous(role)) {
            this.msg.error(event, Label.of("commands.giveme.dangerous"));
            return;
        }
        GiveMe giveMe = this.service.find(event.getGuild());
        giveMe.getRoles().add(role.getIdLong());
        this.service.save(giveMe);
        this.msg.reply(event, Label.of("commands.giveme.added", role.getName()));
    }

    @SlashCommand(value = "giveme.remove", guildOnly = true)
    public void onRemove(SlashCommandInteractionEvent event) {
        if (event.getMember() == null
                || event.getGuild() == null) {
            return;
        } else if (!this.hasPermission(event.getMember())) {
            this.msg.error(event, Label.of("permissions.user"));
            return;
        }
        Role role = this.getOption("name", event, Role.class);
        if (role == null || role.isPublicRole()) {
            this.msg.error(event, Label.of("search.role"));
            return;
        }
        GiveMe giveMe = this.service.find(event.getGuild());
        giveMe.getRoles().remove(role.getIdLong());
        this.service.save(giveMe);
        this.msg.reply(event, Label.of("commands.giveme.removed", role.getName()));
    }

    @AutoComplete("giveme.get.name")
    public void onGetAutoComplete(CommandAutoCompleteInteractionEvent event) {
        Guild guild = event.getGuild();
        if (!event.isFromGuild() || guild == null) {
            event.replyChoices()
                    .queue();
            return;
        }
        AutoCompleteQuery query = event.getFocusedOption();

        List<Command.Choice> choices = new ArrayList<>();
        GiveMe giveMe = this.service.find(guild);
        for (Long roleId : giveMe.getRoles()) {
            Role role = guild.getRoleById(roleId);
            if (role != null) {
                choices.add(new Command.Choice(role.getName(), role.getId()));
            }
        }

        if (choices.isEmpty() || query.getValue().isBlank()) {
            if (choices.size() > 25) {
                event.replyChoices(choices.subList(0, 24))
                        .queue();
            } else {
                event.replyChoices(choices)
                        .queue();
            }
        } else {
            event.replyChoices(FuzzySearch.extractSorted(query.getValue(), choices, Command.Choice::getName, 45)
                    .stream()
                    .map(BoundExtractedResult::getReferent)
                    .limit(25)
                    .toList()
            ).queue();
        }
    }

    private boolean hasPermission(Member member) {
        return member.isOwner()
                || member.hasPermission(Permission.MANAGE_SERVER)
                || member.hasPermission(Permission.MANAGE_ROLES);
    }

    private boolean isDangerous(Role role) {
        return role.hasPermission(Permission.MANAGE_SERVER)
                || role.hasPermission(Permission.MANAGE_ROLES)
                || role.hasPermission(Permission.MANAGE_PERMISSIONS)
                || role.hasPermission(Permission.MANAGE_CHANNEL)
                || role.hasPermission(Permission.ADMINISTRATOR)
                || role.hasPermission(Permission.BAN_MEMBERS)
                || role.hasPermission(Permission.KICK_MEMBERS);
    }

}
