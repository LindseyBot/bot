package net.lindseybot.economy.commands;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.profile.UserProfile;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import net.lindseybot.shared.worker.services.ProfileService;
import org.springframework.stereotype.Component;

@Component
public class CookiesBalance extends InteractionHandler {

    private final ProfileService profiles;

    public CookiesBalance(Messenger msg, ProfileService profiles) {
        super(msg);
        this.profiles = profiles;
    }

    @SlashCommand("cookies.balance")
    public void onBalance(SlashCommandInteractionEvent event) {
        UserProfile self = this.profiles.get(event.getUser());
        User target = this.getOption("user", event, User.class);
        if (target == null) {
            this.msg.reply(event, Label.of("commands.cookies.balance.self", self.getCookies()));
            return;
        }
        UserProfile other = this.profiles.get(target);
        if (self.getCookies() < other.getCookies()) {
            long diff = other.getCookies() - self.getCookies();
            this.msg.reply(event, Label.of("commands.cookies.balance.more",
                    target.getAsMention(), other.getCookies(), diff,
                    this.getPercentage(diff, self.getCookies())));
        } else {
            long diff = self.getCookies() - other.getCookies();
            this.msg.reply(event, Label.of("commands.cookies.balance.less",
                    target.getAsMention(), other.getCookies(), diff,
                    this.getPercentage(diff, other.getCookies())));
        }
    }

    private double getPercentage(long diff, long old) {
        return ((double) diff / old) * 100;
    }

}
