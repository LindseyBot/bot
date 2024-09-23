package net.lindseybot.economy.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.jodah.expiringmap.ExpiringMap;
import net.lindseybot.economy.services.EconomyService;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.discord.builders.ButtonBuilder;
import net.lindseybot.shared.entities.discord.builders.MessageBuilder;
import net.lindseybot.shared.worker.Button;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class Bet extends InteractionHandler {

    private final Random random = new Random();
    private final EconomyService service;
    private final Map<Long, Integer> cache = ExpiringMap.builder()
            .expiration(5, TimeUnit.MINUTES)
            .build();

    public Bet(Messenger messenger, EconomyService service) {
        super(messenger);
        this.service = service;
    }

    @SlashCommand("bet")
    public void onCommand(SlashCommandInteractionEvent event) {
        if (!this.service.has(event.getUser(), 5)) {
            this.msg.error(event, Label.of("economy.not_enough"));
            return;
        } else if (cache.containsKey(event.getUser().getIdLong())) {
            this.msg.error(event, Label.of("commands.bet.running"));
            return;
        }
        this.service.deduct(event.getUser(), 5);
        if (this.random.nextBoolean()) {
            this.cache.put(event.getUser().getIdLong(), 5);
            this.msg.reply(event, this.createDouble(5L, event.getUser().getIdLong()));
        } else {
            this.msg.reply(event, this.createNothing(5L, event.getUser().getIdLong()));
        }
    }

    @Button("bet.bet")
    public void onBet(ButtonInteractionEvent event) {
        String target = this.getData(event);
        if (!event.getUser().getId().equals(target)) {
            this.msg.error(event, Label.of("commands.bet.owned"));
            return;
        }
        Integer bet = this.cache.get(event.getUser().getIdLong());
        if (bet == null) {
            this.msg.error(event, Label.of("commands.bet.expired"));
        } else if (this.random.nextBoolean()) {
            this.cache.put(event.getUser().getIdLong(), bet * 2);
            this.msg.edit(event, this.createDouble(bet * 2, event.getUser().getIdLong()));
        } else {
            this.cache.remove(event.getUser().getIdLong());
            this.msg.edit(event, this.createNothing(bet, event.getUser().getIdLong()));
        }
    }

    @Button("bet.fold")
    public void onFold(ButtonInteractionEvent event) {
        String target = this.getData(event);
        if (!event.getUser().getId().equals(target)) {
            this.msg.error(event, Label.of("commands.bet.owned"));
            return;
        }
        Integer bet = this.cache.get(event.getUser().getIdLong());
        if (bet == null) {
            this.msg.error(event, Label.of("commands.bet.expired"));
            return;
        }
        this.cache.remove(event.getUser().getIdLong());
        this.service.pay(event.getUser(), bet);
        // --
        MessageBuilder builder = new MessageBuilder();
        builder.content(Label.of("commands.bet.end", bet));
        builder.addComponent(new ButtonBuilder()
                .secondary("bet.reset", Label.raw("Try again")).withData(event.getUser().getId())
                .build());
        this.msg.edit(event, builder.build());
    }

    @Button("bet.reset")
    public void onReset(ButtonInteractionEvent event) {
        if (!this.service.has(event.getUser(), 5)) {
            this.msg.error(event, Label.of("economy.not_enough"));
            return;
        } else if (cache.containsKey(event.getUser().getIdLong())) {
            this.msg.error(event, Label.of("commands.bet.running"));
            return;
        }
        String target = this.getData(event);
        if (!event.getUser().getId().equals(target)) {
            this.msg.error(event, Label.of("commands.bet.owned"));
            return;
        }
        this.service.deduct(event.getUser(), 5);
        if (this.random.nextBoolean()) {
            this.cache.put(event.getUser().getIdLong(), 5);
            this.msg.edit(event, this.createDouble(5L, event.getUser().getIdLong()));
        } else {
            this.msg.edit(event, this.createNothing(5L, event.getUser().getIdLong()));
        }
    }

    private FMessage createDouble(long total, long userId) {
        MessageBuilder builder = new MessageBuilder();
        builder.content(Label.of("commands.bet.win", total));
        builder.addComponent(new ButtonBuilder()
                .primary("bet.bet", Label.of("commands.bet.double"))
                .withData(String.valueOf(userId))
                .build());
        builder.addComponent(new ButtonBuilder()
                .secondary("bet.fold", Label.of("commands.bet.fold"))
                .withData(String.valueOf(userId))
                .build());
        return builder.build();
    }

    private FMessage createNothing(long total, long userId) {
        MessageBuilder builder = new MessageBuilder();
        builder.content(Label.of("commands.bet.lose", total));
        builder.addComponent(new ButtonBuilder()
                .secondary("bet.reset", Label.raw("Try again")).withData(String.valueOf(userId))
                .build());
        return builder.build();
    }

}
