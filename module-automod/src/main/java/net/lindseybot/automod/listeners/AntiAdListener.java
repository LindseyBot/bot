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
    private final ExpiringMap<String, Invite> cache;
    private final Set<String> officialInvites = new HashSet<>();

    public AntiAdListener(AntiAdService service, IEventManager api, Translator i18n) {
        this.service = service;
        this.i18n = i18n;
        api.register(this);
        this.triggers = ExpiringMap.builder()
                .maxSize(10_000)
                .expiration(15, TimeUnit.MINUTES)
                .expirationPolicy(ExpirationPolicy.ACCESSED)
                .build();
        this.cache = ExpiringMap.builder()
                .maxSize(250_000)
                .expirationPolicy(ExpirationPolicy.CREATED)
                .expiration(30, TimeUnit.MINUTES)
                .build();
        this.officialInvites.add("hypesquad");
        this.officialInvites.add("discord-api");
        this.officialInvites.add("discord-townhall");
        this.officialInvites.add("discord-developers");
        this.officialInvites.add("jake");
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
        AntiAd settings = this.service.find(event.getGuild());
        if (!settings.isEnabled()) {
            return;
        }
        boolean found = false;
        for (String code : message.getInvites()) {
            if (this.officialInvites.contains(code)) {
                continue;
            }
            Invite invite = this.retrieve(event.getGuild(), code);
            if (!this.isOffense(invite, event.getGuild())) {
                continue;
            }
            found = true;
            break;
        }
        if (!found) {
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
    }

    public Invite retrieve(Guild guild, String code) {
        if (this.cache.containsKey(code)) {
            return this.cache.get(code);
        }
        try {
            Invite local = null;
            for (Invite invite : guild.retrieveInvites().complete()) {
                this.cache.put(invite.getCode(), invite);
                if (code.equals(invite.getCode())) {
                    local = invite;
                }
            }
            if (local != null) {
                this.cache.put(code, local);
                return local;
            }
        } catch (ErrorResponseException | InsufficientPermissionException ex) {
            // Ignored
        }
        Invite invite;
        try {
            invite = Invite.resolve(guild.getJDA(), code).complete();
        } catch (ErrorResponseException ex) {
            return null;
        }
        this.cache.put(code, invite);
        return invite;
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
