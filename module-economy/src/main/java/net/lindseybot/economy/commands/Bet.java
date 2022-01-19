package net.lindseybot.economy.commands;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.lindseybot.economy.models.BetModel;
import net.lindseybot.economy.repositories.redis.BetRepository;
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

import java.util.Optional;
import java.util.Random;

@Component
public class Bet extends InteractionHandler {

    private final Random random = new Random();
    private final EconomyService service;
    private final BetRepository repository;

    public Bet(Messenger messenger,
               EconomyService service,
               BetRepository repository) {
        super(messenger);
        this.service = service;
        this.repository = repository;
    }

    @SlashCommand("bet")
    public void onCommand(SlashCommandEvent event) {
        if (!this.service.has(event.getUser(), 5)) {
            this.msg.error(event, Label.of("economy.not_enough"));
            return;
        } else if (this.repository.findById(event.getUser().getIdLong()).isPresent()) {
            this.msg.error(event, Label.of("commands.bet.running"));
            return;
        }
        this.service.deduct(event.getUser(), 5);
        if (this.random.nextBoolean()) {
            BetModel bet = new BetModel();
            bet.setId(event.getUser().getIdLong());
            bet.setCount(5);
            this.repository.save(bet);
            this.msg.reply(event, this.createDouble(5L, event.getUser().getIdLong()));
        } else {
            this.msg.reply(event, this.createNothing(5L));
        }
    }

    @Button("bet.bet")
    public void onBet(ButtonClickEvent event) {
        String target = event.getComponentId().split(":")[1];
        if (!target.equals(event.getUser().getId())) {
            this.msg.error(event, Label.of("commands.bet.owned"));
            return;
        }
        Optional<BetModel> oBet = this.repository.findById(event.getUser().getIdLong());
        if (oBet.isEmpty()) {
            this.msg.error(event, Label.of("commands.bet.expired"));
            return;
        }
        BetModel bet = oBet.get();
        if (this.random.nextBoolean()) {
            bet.setCount(bet.getCount() * 2);
            this.repository.save(bet);
            this.msg.edit(event, this.createDouble(bet.getCount(), event.getUser().getIdLong()));
        } else {
            this.repository.delete(bet);
            this.msg.edit(event, this.createNothing(bet.getCount()));
        }
    }

    @Button("bet.fold")
    public void onFold(ButtonClickEvent event) {
        String target = event.getComponentId().split(":")[1];
        if (!target.equals(event.getUser().getId())) {
            this.msg.error(event, Label.of("commands.bet.owned"));
            return;
        }
        Optional<BetModel> oBet = this.repository.findById(event.getUser().getIdLong());
        if (oBet.isEmpty()) {
            this.msg.error(event, Label.of("commands.bet.expired"));
            return;
        }
        BetModel bet = oBet.get();
        this.repository.delete(bet);
        this.service.pay(event.getUser(), bet.getCount());
        this.msg.edit(event, Label.of("commands.bet.end", bet.getCount()));
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

    private FMessage createNothing(long total) {
        MessageBuilder builder = new MessageBuilder();
        builder.content(Label.of("commands.bet.lose", total));
        return builder.build();
    }

}
