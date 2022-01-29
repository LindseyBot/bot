package net.lindseybot.automod.listeners;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import net.lindseybot.automod.services.AntiAdService;
import net.lindseybot.shared.entities.profile.servers.AntiAd;
import net.lindseybot.shared.worker.services.Translator;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
@Component
public class AntiAdListener extends ListenerAdapter {

    private final Translator i18n;
    private final AntiAdService service;
    private final ExpiringMap<Long, AtomicInteger> triggers;
    private final Set<String> officialInvites = new HashSet<>();

    private final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    public AntiAdListener(AntiAdService service, IEventManager api, Translator i18n) {
        this.service = service;
        this.i18n = i18n;
        api.register(this);
        this.triggers = ExpiringMap.builder()
                .maxSize(10_000)
                .expiration(15, TimeUnit.MINUTES)
                .expirationPolicy(ExpirationPolicy.ACCESSED)
                .build();
        this.officialInvites.add("hypesquad");
        this.officialInvites.add("discord-api");
        this.officialInvites.add("discord-townhall");
        this.officialInvites.add("discord-developers");
        this.officialInvites.add("jake");
        this.executor.setThreadNamePrefix("antiad-");
        this.executor.initialize();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Member author = event.getMember();
        if (author == null
                || author.getUser().isBot()
                || author.hasPermission(Permission.MESSAGE_MANAGE)) {
            return;
        }
        Message message = event.getMessage();
        if (message.getInvites().isEmpty()) {
            return;
        }
        this.executor.submit(() -> {
            boolean found = false;
            for (String inviteCode : message.getInvites()) {
                Invite invite;
                try {
                    invite = Invite.resolve(event.getJDA(), inviteCode).complete();
                } catch (ErrorResponseException ex) {
                    continue;
                }
                if (!this.isOffense(invite, event.getGuild())) {
                    continue;
                }
                found = true;
                break;
            }
            if (!found) {
                return;
            }
            AntiAd settings = this.service.find(event.getGuild());
            if (!settings.isEnabled()) {
                return;
            }
            AtomicInteger integer = this.triggers.compute(event.getAuthor().getIdLong(), ((k, v) -> {
                if (v == null) {
                    return new AtomicInteger(0);
                }
                return v;
            }));
            try {
                if (integer.incrementAndGet() > settings.getStrikes()) {
                    event.getMember().ban(7, "Advertising")
                            .queue();
                    return;
                }
                message.delete()
                        .reason("Advertising")
                        .flatMap(aVoid -> message.getChannel()
                                .sendMessage(i18n.get(author, "automod.antiad.warn", author.getAsMention())))
                        .delay(10, TimeUnit.SECONDS)
                        .flatMap(Message::delete)
                        .queue(this.noop(), this.noop());
            } catch (InsufficientPermissionException ex) {
                // Ignored
            }
        });
    }

    private boolean isOffense(Invite invite, Guild guild) {
        return invite != null
                && invite.getType() == Invite.InviteType.GUILD
                && invite.getGuild() != null
                && !guild.getId().equals(invite.getGuild().getId())
                && !this.officialInvites.contains(invite.getCode());
    }

    private <T> Consumer<T> noop() {
        return t -> {
        };
    }

}
