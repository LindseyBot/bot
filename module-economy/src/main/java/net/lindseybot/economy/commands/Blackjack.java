package net.lindseybot.economy.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.lindseybot.economy.models.BlackjackModel;
import net.lindseybot.economy.services.BlackJackService;
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

import java.util.*;
import java.util.regex.Pattern;

@Component
public class Blackjack extends InteractionHandler {

    private static final int COST = 10;
    private static final int REWARD = 25;
    private static final String VERSO = " \uD83C\uDCA1";
    private final EconomyService service;
    private final BlackJackService repository;
    private final Random rnd = new Random();
    private final List<Integer> playerHand = new ArrayList<>();

    public Blackjack(Messenger messenger, EconomyService service, BlackJackService repository) {
        super(messenger);
        this.service = service;
        this.repository = repository;
    }

    @SlashCommand("blackjack")
    public void onStart(SlashCommandInteractionEvent event) {
        Long userId = event.getUser().getIdLong();
        if (!this.service.has(event.getUser(), COST)) {
            this.msg.error(event, Label.of("economy.not_enough"));
            return;
        } else if (this.repository.findById(userId).isPresent()) {
            this.msg.error(event, Label.of("commands.blackjack.running"));
            return;
        }

        this.service.deduct(event.getUser(), COST);
        int i = rnd.nextInt(12) + 1;
        playerHand.clear();
        playerHand.add(i);
        save(userId);

        MessageBuilder message = loadMessage(userId, null);
        this.msg.reply(event, message.build());
    }

    @Button("blackjack.draw")
    public void draw(ButtonInteractionEvent event) {
        long userId = event.getUser().getIdLong();

        String target = event.getComponentId().split(":")[1];
        if (!target.equals(event.getUser().getId())) {
            this.msg.error(event, Label.of("commands.blackjack.owned"));
            return;
        }

        Optional<BlackjackModel> oBlackjack = this.repository.findById(userId);
        if (oBlackjack.isEmpty()) {
            this.msg.error(event, Label.of("commands.blackjack.expired"));
            return;
        }

        BlackjackModel model = oBlackjack.get();
        playerHand.clear();
        playerHand.addAll(model.getHand());

        int i = rnd.nextInt(12) + 1;
        playerHand.add(i);
        save(userId);

        if (model.getSum() >= 21) {
            this.msg.edit(event, badEnd(model));
            return;
        } else if (model.getSum() == 21) {
            this.msg.edit(event, goodEnd(model, event.getMember()));
            return;
        } else if (playerHand.size() == 5) {
            this.msg.edit(event, end(model, event.getMember()));
            return;
        }

        MessageBuilder message = loadMessage(userId, model);
        this.msg.edit(event, message.build());
    }

    @Button("blackjack.stop")
    public void stop(ButtonInteractionEvent event) {
        long userId = event.getUser().getIdLong();

        String target = event.getComponentId().split(":")[1];
        if (!target.equals(event.getUser().getId())) {
            this.msg.error(event, Label.of("commands.blackjack.owned"));
            return;
        }

        Optional<BlackjackModel> oBlackjack = this.repository.findById(userId);
        if (oBlackjack.isEmpty()) {
            this.msg.reply(event, Label.of("commands.blackjack.expired"));
            return;
        }

        this.msg.edit(event, end(oBlackjack.get(), event.getMember()));
    }

    public FMessage end(BlackjackModel model, Member member) {
        playerHand.clear();
        playerHand.addAll(model.getHand());

        int botHandCount = 0;
        for (int i = 0; i < 5; i++) {
            int r = rnd.nextInt(12) + 1;
            model.getBotHand().add(r);
            botHandCount = botHandCount + r;

            if (botHandCount > 21) {
                return goodEnd(model, member);
            }
            if (botHandCount > model.getSum()) {
                return badEnd(model);
            }
        }
        return goodEnd(model, member);
    }

    public FMessage goodEnd(BlackjackModel model, Member member) {
        this.service.pay(member, REWARD);
        MessageBuilder message = new MessageBuilder();
        message.content(loadTemplate(true, model));
        repository.delete(model);
        return message.build();
    }

    public FMessage badEnd(BlackjackModel model) {
        MessageBuilder message = new MessageBuilder();
        message.content(loadTemplate(false, model));
        repository.delete(model);
        return message.build();
    }

    private Label loadTemplate(Boolean win, BlackjackModel model) {
        if (win == null) {
            return Label.of("commands.blackjack.template",
                    buildArgs(false, model),
                    buildArgs(true, model));
        }
        if (win) {
            return Label.of("commands.blackjack.templateWin",
                    buildArgs(false, model),
                    buildArgs(true, model));
        }
        return Label.of("commands.blackjack.templateLost",
                buildArgs(false, model),
                buildArgs(true, model));
    }

    private String buildArgs(boolean bot, BlackjackModel model) {
        String message = "\n" + VERSO + VERSO + VERSO + VERSO + VERSO + "\n";

        if (!bot) {
            for (int i : playerHand) {
                message = message.replaceFirst(Pattern.quote(VERSO), getCard(i));
            }
            return message;
        }
        if (!Objects.isNull(model)) {
            List<Integer> botHand = model.getBotHand();
            for (int i : botHand) {
                message = message.replaceFirst(Pattern.quote(VERSO), getCard(i));
            }
        }
        return message;
    }

    private MessageBuilder loadMessage(Long userId, BlackjackModel model) {
        MessageBuilder message = new MessageBuilder();
        message.content(loadTemplate(null, model));
        message.addComponent(new ButtonBuilder()
                .danger("blackjack.stop", Label.raw("Stop"))
                .withData(String.valueOf(userId))
                .build()
        );
        message.addComponent(new ButtonBuilder()
                .success("blackjack.draw", Label.raw("Draw"))
                .withData(String.valueOf(userId))
                .build()
        );
        return message;
    }

    public void save(long id) {
        BlackjackModel model = new BlackjackModel();
        model.setId(id);
        model.setCount(10);
        model.setHand(playerHand);
        repository.save(model);
    }

    private String getCard(int i) {
        return switch (i) {
            case 1 -> " A";
            case 11 -> " J";
            case 12 -> " Q";
            case 13 -> " K";
            default -> " " + i;
        };
    }

}
