package net.lindseybot.economy.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.lindseybot.economy.services.EconomyService;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.profile.UserProfile;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import net.lindseybot.shared.worker.services.ProfileService;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class CookiesDaily extends InteractionHandler {

    private final ProfileService profiles;
    private final EconomyService economy;

    public CookiesDaily(Messenger msg, ProfileService profiles, EconomyService economy) {
        super(msg);
        this.profiles = profiles;
        this.economy = economy;
    }

    @SlashCommand("cookies.daily")
    public void onDaily(SlashCommandInteractionEvent event) {
        UserProfile profile = this.profiles.get(event.getUser());
        if (this.isSameDay(profile.getLastDailyCookies(), System.currentTimeMillis())) {
            long next = Instant.ofEpochMilli(System.currentTimeMillis())
                    .truncatedTo(ChronoUnit.DAYS)
                    .plus(1, ChronoUnit.DAYS)
                    .getEpochSecond();
            this.msg.error(event, Label.of("commands.cookies.daily.fail", "<t:" + next + ":R>"));
            return;
        }
        long streak;
        if (isStreak(profile.getLastDailyCookies())) {
            streak = profile.getCookieStreak() + 1;
        } else {
            streak = 1L;
        }
        profile.setCookieStreak(streak);
        profile.setLastDailyCookies(System.currentTimeMillis());
        profile.setLastSeen(System.currentTimeMillis());
        profile.setName(event.getUser().getAsTag());
        profiles.save(profile);
        economy.pay(event.getUser(), streak * 15);
        this.msg.reply(event, Label.of("commands.cookies.daily.received", streak * 15, streak));
    }

    private boolean isSameDay(long one, long two) {
        return Instant.ofEpochMilli(one).truncatedTo(ChronoUnit.DAYS)
                .equals(Instant.ofEpochMilli(two).truncatedTo(ChronoUnit.DAYS));
    }

    private boolean isStreak(long last) {
        return Instant.ofEpochMilli(last).truncatedTo(ChronoUnit.DAYS)
                .equals(Instant.now().minus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS));
    }

}
