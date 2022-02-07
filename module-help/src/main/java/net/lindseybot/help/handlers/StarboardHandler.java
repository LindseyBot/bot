package net.lindseybot.help.handlers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.lindseybot.help.models.ModuleHandler;
import net.lindseybot.help.services.HelpStarboardService;
import net.lindseybot.shared.entities.discord.FEmote;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.discord.builders.*;
import net.lindseybot.shared.entities.profile.servers.Starboard;
import net.lindseybot.shared.utils.GFXUtils;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SelectMenu;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

@Component
public class StarboardHandler extends InteractionHandler implements ModuleHandler {

    private final HelpStarboardService service;

    public StarboardHandler(Messenger msg, HelpStarboardService service) {
        super(msg);
        this.service = service;
    }

    @Override
    public String getSlug() {
        return "starboard";
    }

    @Override
    public Label getName() {
        return Label.of("commands.modules.starboard");
    }

    @Override
    public Label description() {
        return Label.of("commands.modules.starboard.text");
    }

    @Override
    public FMessage enable(Member member, Guild guild) {
        Starboard starboard = this.service.get(guild);
        starboard.setEnabled(true);
        this.service.save(starboard);
        return this.onStatus(member, guild);
    }

    @Override
    public FMessage disable(Member member, Guild guild) {
        Starboard starboard = this.service.get(guild);
        starboard.setEnabled(false);
        this.service.save(starboard);
        return this.onStatus(member, guild);
    }

    @Override
    public FMessage onStatus(Member member, Guild guild) {
        Starboard starboard = this.service.get(guild);
        MessageBuilder builder = new MessageBuilder();
        if (starboard.isEnabled()) {
            TextChannel channel = guild.getTextChannelById(starboard.getChannel());
            if (channel == null) {
                starboard.setEnabled(false);
                this.service.save(starboard);
                return this.onStatus(member, guild);
            }
            builder.content(Label.of("commands.modules.starboard.enabled", channel.getAsMention(), starboard.getMinStars()));
        } else {
            builder.content(Label.of("commands.modules.starboard.disabled"));
        }
        builder.addComponent(new ButtonBuilder()
                .danger("module-disable", Label.of("labels.disable"))
                .withData(this.getSlug())
                .disabled(!starboard.isEnabled())
                .build());
        builder.addComponent(new ButtonBuilder()
                .success("module-enable", Label.of("labels.enable"))
                .withData(this.getSlug())
                .disabled(starboard.isEnabled())
                .build());
        builder.addComponent(new ButtonBuilder()
                .secondary("module-configure", Label.of("labels.configure"))
                .withData(this.getSlug())
                .disabled(!starboard.isEnabled())
                .build());
        return builder.build();
    }

    @Override
    public FMessage onSetupStart(Member member, Guild guild) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.title(Label.raw("Starboard Setup (1/2)"));
        embed.description(Label.raw("Please select the starboard channel, this is the channel where starred messages will be displayed."));
        embed.color(GFXUtils.YELLOW);

        MessageBuilder builder = new MessageBuilder();
        builder.embed(embed.build());

        builder.addComponent(new SelectMenuBuilder("setup.starboard.1")
                .addOption(this.getTextChannels(guild))
                .withLabel(Label.raw("Channel"))
                .build()
        );
        return builder.build();
    }

    @SelectMenu("setup.starboard.1")
    public void onStep1(SelectMenuInteractionEvent event) {
        if (isNotSafe(event)) {
            return;
        } else if (event.getGuild() == null
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

        Starboard starboard = this.service.get(event.getGuild());
        starboard.setChannel(id);
        this.service.save(starboard);

        EmbedBuilder embed = new EmbedBuilder();
        embed.title(Label.raw("Starboard Setup (2/2)"));
        embed.description(Label.raw("Please select the amount of stars that a message must have to be added to the starboard."));
        embed.color(GFXUtils.YELLOW);

        MessageBuilder builder = new MessageBuilder();
        builder.embed(embed.build());
        builder.addComponent(new SelectMenuBuilder("setup.starboard.2")
                .addOption(new SelectOptionBuilder("3", Label.raw("3")).withEmote(FEmote.ofUnicode("\u2B50")).build())
                .addOption(new SelectOptionBuilder("5", Label.raw("5")).withEmote(FEmote.ofUnicode("\u2B50")).build())
                .addOption(new SelectOptionBuilder("10", Label.raw("10")).withEmote(FEmote.ofUnicode("\u2B50")).build())
                .withLabel(Label.raw("Channel"))
                .build()
        );
        this.msg.edit(event, builder.build());
    }

    @SelectMenu("setup.starboard.2")
    public void onStep2(SelectMenuInteractionEvent event) {
        if (isNotSafe(event)) {
            return;
        } else if (event.getGuild() == null
                || event.getSelectedOptions().isEmpty()) {
            return;
        }
        SelectOption count = event.getSelectedOptions().get(0);
        int stars = Integer.parseInt(count.getValue());

        Starboard starboard = this.service.get(event.getGuild());
        starboard.setMinStars(stars);
        this.service.save(starboard);

        this.msg.edit(event, this.onStatus(event.getMember(), event.getGuild()));
    }

}
