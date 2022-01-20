package net.lindseybot.automod.listeners;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.lindseybot.automod.services.RegistrationService;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.profile.servers.Registration;
import net.lindseybot.shared.utils.PlaceHolderUtils;
import net.lindseybot.shared.worker.services.Messenger;
import net.lindseybot.shared.worker.services.NotificationService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
public class RegistrationListener extends ListenerAdapter {

    private final RegistrationService service;
    private final Messenger msg;
    private final NotificationService notifications;

    public RegistrationListener(RegistrationService service,
                                Messenger msg, IEventManager api,
                                NotificationService notifications) {
        this.service = service;
        this.msg = msg;
        this.notifications = notifications;
        api.register(this);
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        Member author = event.getMember();
        if (author == null
                || author.getUser().isBot()
                || author.isPending()) {
            return;
        }
        Registration registration = this.service.find(event.getGuild());
        if (!registration.isEnabled()) {
            return;
        } else if (registration.getChannelId() > 0
                && event.getChannel().getIdLong() != registration.getChannelId()) {
            return;
        } else if (registration.getPhrase() == null) {
            this.service.disable(registration);
            this.notifications.notify(event.getGuild(), Label.of("logs.module.config", "Registration"));
            return;
        }
        String phrase = PlaceHolderUtils.replace(registration.getPhrase(), author);
        Role role = event.getGuild().getRoleById(registration.getRoleId());
        if (role == null || role.isPublicRole() || role.isManaged()) {
            this.service.disable(registration);
            this.notifications.notify(event.getGuild(), Label.of("logs.module.role", "Registration"));
            return;
        } else if (!event.getMessage().getContentDisplay()
                .equalsIgnoreCase(phrase)) {
            return;
        }
        try {
            event.getGuild().addRoleToMember(author, role)
                    .reason("Auto-Register")
                    .queue(noop(), noop());
        } catch (InsufficientPermissionException ex) {
            this.msg.reply(event.getMessage(), Label.of("permissions.bot", ex.getPermission().getName()));
        } catch (HierarchyException ex) {
            this.msg.reply(event.getMessage(), Label.of("permissions.hierarchy"));
        }
    }

    private <T> Consumer<T> noop() {
        return t -> {
        };
    }

}
