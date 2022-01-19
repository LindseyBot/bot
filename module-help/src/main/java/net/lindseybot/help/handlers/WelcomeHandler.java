package net.lindseybot.help.handlers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.lindseybot.help.models.ModuleHandler;
import net.lindseybot.help.services.HelpWelcomeService;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.discord.builders.ButtonBuilder;
import net.lindseybot.shared.entities.discord.builders.EmbedBuilder;
import net.lindseybot.shared.entities.discord.builders.MessageBuilder;
import net.lindseybot.shared.entities.discord.builders.SelectMenuBuilder;
import net.lindseybot.shared.entities.profile.servers.Welcome;
import net.lindseybot.shared.utils.GFXUtils;
import net.lindseybot.shared.utils.StandardEmotes;
import net.lindseybot.shared.worker.Button;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SelectMenu;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WelcomeHandler extends InteractionHandler implements ModuleHandler {

    private final HelpWelcomeService service;

    public WelcomeHandler(Messenger msg, HelpWelcomeService service) {
        super(msg);
        this.service = service;
    }

    @Override
    public String getSlug() {
        return "welcome";
    }

    @Override
    public Label getName() {
        return Label.raw("Welcome");
    }

    @Override
    public Label description() {
        return Label.raw("Send welcome messages to new users!");
    }

    @Override
    public FMessage enable(Member member, Guild guild) {
        Welcome welcome = this.service.get(guild);
        welcome.setEnabled(true);
        this.service.save(welcome);
        return this.onStatus(member, guild, false);
    }

    @Override
    public FMessage disable(Member member, Guild guild) {
        Welcome welcome = this.service.get(guild);
        welcome.setEnabled(false);
        this.service.save(welcome);
        return this.onStatus(member, guild, false);
    }

    @Override
    public FMessage onStatus(Member member, Guild guild, boolean setup) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.title(this.getName());
        if (setup) {
            embed.description(Label.raw(StandardEmotes.CHECK.asMention() + " **Setup Finished!**\n\nSend welcome messages to new users when they join the server."));
        } else {
            embed.description(Label.raw("Send welcome messages to new users when they join the server."));
        }
        embed.color(GFXUtils.GREEN);
        embed.image("https://cdn.lindseybot.net/showcases/welcome.gif");

        Welcome welcome = this.service.get(guild);

        MessageBuilder builder = new MessageBuilder();
        builder.embed(embed.build());
        builder.addComponent(new ButtonBuilder()
                .danger("module-disable", Label.raw("Disable"))
                .withData(this.getSlug())
                .disabled(!welcome.isEnabled())
                .build());
        builder.addComponent(new ButtonBuilder()
                .success("module-enable", Label.raw("Enable"))
                .withData(this.getSlug())
                .disabled(welcome.isEnabled())
                .build());
        builder.addComponent(new ButtonBuilder()
                .secondary("module-configure", Label.raw("Configure"))
                .withData(this.getSlug())
                .disabled(!welcome.isEnabled())
                .build());
        return builder.build();
    }

    @Override
    public FMessage onSetupStart(Member member, Guild guild) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.title(Label.raw("Welcome Setup (1/2)"));
        embed.description(Label.raw("Please select the welcoming channel, this is the channel where welcome messages will be sent."));
        embed.color(GFXUtils.GREEN);

        MessageBuilder builder = new MessageBuilder();
        builder.embed(embed.build());

        builder.addComponent(new SelectMenuBuilder("setup.welcome.1")
                .addOption(this.getTextChannels(guild))
                .withLabel(Label.raw("Channel"))
                .build()
        );
        return builder.build();
    }

    @SelectMenu("setup.welcome.1")
    public void onStep1(SelectionMenuEvent event) {
        if (this.isNotSafe(event)) {
            return;
        } else if (event.getGuild() == null
                || event.getSelectedOptions() == null
                || event.getSelectedOptions().isEmpty()) {
            return;
        }
        SelectOption channel = event.getSelectedOptions().get(0);
        long id = Long.parseLong(channel.getValue());

        TextChannel target = event.getGuild().getTextChannelById(id);
        if (target == null) {
            this.msg.error(event, Label.of("search.channel"));
            return;
        } else if (!target.canTalk()) {
            this.msg.error(event, Label.of("permissions.talk"));
            return;
        }

        Welcome welcome = this.service.get(event.getGuild());
        welcome.setChannelId(id);
        this.service.save(welcome);

        EmbedBuilder embed = new EmbedBuilder();
        embed.title(Label.raw("Welcome Setup (2/2)"));
        embed.description(Label.raw("Please type the desired welcome message in chat, and when you are done click the Finish button below. Remember you can use placeholders to fill" +
                " information like the user's name."));
        embed.color(GFXUtils.GREEN);

        MessageBuilder builder = new MessageBuilder();
        builder.embed(embed.build());
        builder.addComponent(new ButtonBuilder()
                .success("setup.welcome.2", Label.raw("Finish"))
                .withData(event.getUser().getId())
                .build()
        );
        this.msg.edit(event, builder.build());
    }

    @Button("setup.welcome.2")
    public void onStep2(ButtonClickEvent event) {
        if (this.isNotSafe(event)) {
            return;
        } else if (event.getGuild() == null) {
            return;
        }
        String userId = this.getData(event);
        if (userId == null || !event.getUser().getId().equals(userId)) {
            return;
        }
        List<Message> past;
        try {
            past = event.getMessageChannel()
                    .getHistory()
                    .retrievePast(5)
                    .complete();
        } catch (Exception ex) {
            this.msg.error(event, Label.of("discord.error", ex.getMessage()));
            return;
        }
        Message message = past.stream()
                .filter(m -> m.getAuthor().getId().equals(userId))
                .findFirst().orElse(null);
        if (message == null) {
            this.msg.error(event, Label.raw("Failed to find a welcome message."));
            return;
        }
        String content = message.getContentRaw();

        Welcome welcome = this.service.get(event.getGuild());
        welcome.setMessage(content);
        this.service.save(welcome);

        this.msg.edit(event, this.onStatus(event.getMember(), event.getGuild(), true));
        message.delete()
                .reason("Welcome Message Setup")
                .queue(noop(), noop());
    }

}
