package net.lindseybot.help.handlers;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.lindseybot.help.models.ModuleHandler;
import net.lindseybot.help.services.HelpRegisterService;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.discord.builders.ButtonBuilder;
import net.lindseybot.shared.entities.discord.builders.EmbedBuilder;
import net.lindseybot.shared.entities.discord.builders.MessageBuilder;
import net.lindseybot.shared.entities.discord.builders.SelectMenuBuilder;
import net.lindseybot.shared.entities.profile.servers.Registration;
import net.lindseybot.shared.utils.GFXUtils;
import net.lindseybot.shared.worker.Button;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SelectMenu;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RegisterHandler extends InteractionHandler implements ModuleHandler {

    private final HelpRegisterService service;

    public RegisterHandler(Messenger msg, HelpRegisterService service) {
        super(msg);
        this.service = service;
    }

    @Override
    public String getSlug() {
        return "registration";
    }

    @Override
    public Label getName() {
        return Label.of("commands.modules.register");
    }

    @Override
    public Label description() {
        return Label.of("commands.modules.register.text");
    }

    @Override
    public FMessage enable(Member member, Guild guild) {
        Registration registration = this.service.get(guild);
        registration.setEnabled(true);
        this.service.save(registration);
        return this.onStatus(member, guild);
    }

    @Override
    public FMessage disable(Member member, Guild guild) {
        Registration registration = this.service.get(guild);
        registration.setEnabled(false);
        this.service.save(registration);
        return this.onStatus(member, guild);
    }

    @Override
    public FMessage onStatus(Member member, Guild guild) {
        Registration registration = this.service.get(guild);
        MessageBuilder builder = new MessageBuilder();
        if (registration.isEnabled()) {
            TextChannel channel = guild.getTextChannelById(registration.getChannelId());
            if (channel == null) {
                registration.setEnabled(false);
                this.service.save(registration);
                return this.onStatus(member, guild);
            }
            builder.content(Label.of("commands.modules.register.enabled", channel.getAsMention()));
        } else {
            builder.content(Label.of("commands.modules.register.disabled"));
        }
        builder.addComponent(new ButtonBuilder()
                .danger("module-disable", Label.of("labels.disable"))
                .withData(this.getSlug())
                .disabled(!registration.isEnabled())
                .build());
        builder.addComponent(new ButtonBuilder()
                .success("module-enable", Label.of("labels.enable"))
                .withData(this.getSlug())
                .disabled(registration.isEnabled())
                .build());
        builder.addComponent(new ButtonBuilder()
                .secondary("module-configure", Label.of("labels.configure"))
                .withData(this.getSlug())
                .disabled(!registration.isEnabled())
                .build());
        return builder.build();
    }

    @Override
    public FMessage onSetupStart(Member member, Guild guild) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.title(Label.raw("Registration Setup (1/3)"));
        embed.description(Label.raw("Please select the registration channel, this is the channel where users will have to type the registration message."));
        embed.color(GFXUtils.BLUE);

        MessageBuilder builder = new MessageBuilder();
        builder.embed(embed.build());

        builder.addComponent(new SelectMenuBuilder("setup.register.1")
                .addOption(this.getTextChannels(guild))
                .withLabel(Label.raw("Channel"))
                .build()
        );
        return builder.build();
    }

    @SelectMenu("setup.register.1")
    public void onStep1(SelectMenuInteractionEvent event) {
        if (this.isNotSafe(event)) {
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
        } else if (!event.getGuild().getSelfMember()
                .hasPermission(target, Permission.VIEW_CHANNEL)) {
            this.msg.error(event, Label.of("permissions.read"));
            return;
        }

        Registration registration = this.service.get(event.getGuild());
        registration.setChannelId(id);
        this.service.save(registration);

        EmbedBuilder embed = new EmbedBuilder();
        embed.title(Label.raw("Registration Setup (2/3)"));
        embed.description(Label.raw("Please select the registration role, this is the role registered users will receive."));
        embed.color(GFXUtils.BLUE);

        MessageBuilder builder = new MessageBuilder();
        builder.embed(embed.build());

        builder.addComponent(new SelectMenuBuilder("setup.register.2")
                .addOption(this.getRoles(event.getGuild()))
                .withLabel(Label.raw("Role"))
                .build()
        );
        this.msg.edit(event, builder.build());
    }

    @SelectMenu("setup.register.2")
    public void onStep2(SelectMenuInteractionEvent event) {
        if (this.isNotSafe(event)) {
            return;
        } else if (event.getGuild() == null
                || event.getSelectedOptions().isEmpty()) {
            return;
        }
        SelectOption roleOpt = event.getSelectedOptions().get(0);
        long id = Long.parseLong(roleOpt.getValue());

        Role role = event.getGuild().getRoleById(id);
        if (role == null) {
            return;
        } else if (!event.getGuild().getSelfMember()
                .canInteract(role)) {
            this.msg.error(event, Label.of("permissions.hierarchy"));
            return;
        }

        Registration registration = this.service.get(event.getGuild());
        registration.setRoleId(id);
        this.service.save(registration);

        EmbedBuilder embed = new EmbedBuilder();
        embed.title(Label.raw("Registration Setup (3/3)"));
        embed.description(Label.raw("Please type the desired register phrase/word in chat, and when you are done click the Finish button below. Remember you can use placeholders to fill" +
                " information like the user's name."));
        embed.color(GFXUtils.BLUE);

        MessageBuilder builder = new MessageBuilder();
        builder.embed(embed.build());
        builder.addComponent(new ButtonBuilder()
                .success("setup.register.3", Label.raw("Finish"))
                .withData(event.getUser().getId())
                .build()
        );
        this.msg.edit(event, builder.build());
    }

    @Button("setup.register.3")
    public void onStep3(ButtonInteractionEvent event) {
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
            this.msg.error(event, Label.of("error.discord", ex.getMessage()));
            return;
        }
        Message message = past.stream()
                .filter(m -> m.getAuthor().getId().equals(userId))
                .findFirst().orElse(null);
        if (message == null) {
            this.msg.error(event, Label.raw("Failed to find a registration message."));
            return;
        }
        String content = message.getContentRaw();

        Registration registration = this.service.get(event.getGuild());
        registration.setPhrase(content);
        this.service.save(registration);

        this.msg.edit(event, this.onStatus(event.getMember(), event.getGuild()));
        message.delete()
                .reason("Registration Setup")
                .queue(noop(), noop());
    }

}
