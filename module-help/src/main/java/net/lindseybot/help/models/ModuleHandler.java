package net.lindseybot.help.models;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.GenericComponentInteractionCreateEvent;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.FSelectOption;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.discord.builders.SelectOptionBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface ModuleHandler {

    String getSlug();

    Label getName();

    Label description();

    FMessage enable(Member member, Guild guild);

    FMessage disable(Member member, Guild guild);

    FMessage onStatus(Member member, Guild guild, boolean setup);

    FMessage onSetupStart(Member member, Guild guild);

    default FSelectOption[] getTextChannels(Guild guild) {
        List<FSelectOption> options = new ArrayList<>();
        List<TextChannel> channels = guild.getTextChannels();
        for (int i = 0; i < Math.min(25, channels.size()); i++) {
            TextChannel channel = channels.get(i);
            options.add(new SelectOptionBuilder(channel.getId(), Label.raw(channel.getName())).build());
        }
        return options.toArray(new FSelectOption[0]);
    }

    default FSelectOption[] getRoles(Guild guild) {
        List<FSelectOption> options = new ArrayList<>();
        List<Role> roles = guild.getRoles();
        for (int i = 0; i < Math.min(25, roles.size()); i++) {
            Role role = roles.get(i);
            options.add(new SelectOptionBuilder(role.getId(), Label.raw(role.getName())).build());
        }
        return options.toArray(new FSelectOption[0]);
    }

    default boolean isNotSafe(GenericComponentInteractionCreateEvent event) {
        return event.getGuild() == null
                || event.getMember() == null
                || (!event.getMember().isOwner() && !event.getMember().hasPermission(Permission.MANAGE_SERVER));
    }

    default <T> Consumer<T> noop() {
        return t -> {
        };
    }

}
