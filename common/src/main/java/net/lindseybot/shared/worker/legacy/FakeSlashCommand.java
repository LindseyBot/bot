package net.lindseybot.shared.worker.legacy;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.lindseybot.shared.worker.legacy.proxy.ProxyRestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FakeSlashCommand extends SlashCommandInteractionEvent {

    private final FakeSlashData data;
    private final Message message;

    public FakeSlashCommand(@NotNull ShardManager api, @NotNull FakeSlashData data, @NotNull Message message) {
        super(api.getShards().get(0), data.getResponseNumber(), null);
        this.data = data;
        this.message = message;
    }

    @NotNull
    @Override
    public String getName() {
        if (this.data.getPath().contains("/")) {
            String[] data = this.data.getPath().split("/");
            return data[0];
        } else {
            return this.data.getPath();
        }
    }

    public @NotNull Message getMessage() {
        return this.message;
    }

    @NotNull
    @Override
    public String getFullCommandName() {
        return data.getPath();
    }

    @Nullable
    @Override
    public Guild getGuild() {
        return this.message.getGuild();
    }

    @NotNull
    public TextChannel getTextChannel() {
        return (TextChannel) this.message.getChannel();
    }

    @Nullable
    @Override
    public Member getMember() {
        return this.message.getMember();
    }

    @NotNull
    @Override
    public User getUser() {
        return this.message.getAuthor();
    }

    @NotNull
    @Override
    public MessageChannelUnion getChannel() {
        return this.message.getChannel();
    }

    @Override
    public boolean isFromGuild() {
        return true;
    }

    @NotNull
    @Override
    public ChannelType getChannelType() {
        return this.getChannel().getType();
    }

    @NotNull
    @Override
    public ReplyCallbackAction reply(@NotNull String content) {
        MessageCreateData data = new MessageCreateBuilder().setContent(content).build();
        return new ProxyRestAction(this)
                .withMessage(data)
                .setAllowedMentions(List.of(Message.MentionType.EMOJI, Message.MentionType.CHANNEL));
    }

    @Override
    public ReplyCallbackAction deferReply() {
        return new ProxyRestAction(this);
    }

    @Override
    public ReplyCallbackAction deferReply(boolean ephemeral) {
        return this.deferReply();
    }

    @Override
    public boolean isAcknowledged() {
        return false;
    }

    @Nullable
    @Override
    public OptionMapping getOption(@NotNull String name) {
        FakeOptionMapping fake = this.data.getOptions()
                .getOrDefault(name, null);
        if (fake == null) {
            return null;
        }
        DataObject dataObject = DataObject.empty();
        dataObject.put("name", fake.getName());
        dataObject.put("value", fake.getValue());
        dataObject.put("type", fake.getType().getKey());
        return new OptionMapping(dataObject, fake.getResolved(), message.getJDA(), message.getGuild());
    }

}
