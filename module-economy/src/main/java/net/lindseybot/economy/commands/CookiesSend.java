package net.lindseybot.economy.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.lindseybot.economy.services.EconomyService;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

@Component
public class CookiesSend extends InteractionHandler {

    private final EconomyService economy;

    public CookiesSend(Messenger msg, EconomyService economy) {
        super(msg);
        this.economy = economy;
    }

    @SlashCommand("cookies.send")
    public void onCommand(SlashCommandInteractionEvent event) {
        Member target = this.getOption("target", event, Member.class);
        if (target == null) {
            this.msg.error(event, Label.of("search.member"));
            return;
        }
        Long amount = this.getOption("amount", event, Long.class);
        if (amount == null || amount <= 0) {
            this.msg.error(event, Label.of("commands.cookies.send.invalid"));
            return;
        }
        User self = event.getUser();
        if (!this.economy.has(self, amount)) {
            this.msg.error(event, Label.of("economy.not_enough"));
            return;
        } else if (self.getId().equals(target.getId())) {
            this.msg.error(event, Label.of("validation.self"));
            return;
        }
        this.economy.pay(self, -amount);
        this.economy.pay(target, amount);
        this.msg.reply(event, Label.of("commands.cookies.send.sent", self.getAsMention(), amount, target.getAsMention()));
    }

}
