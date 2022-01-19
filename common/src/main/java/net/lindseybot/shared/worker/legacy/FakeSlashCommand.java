package net.lindseybot.shared.worker.legacy;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.lindseybot.shared.worker.legacy.proxy.ProxyRestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FakeSlashCommand extends SlashCommandEvent {

    private final ShardManager api;
    private final FakeSlashData data;

    private User user;
    private Member member;

    public FakeSlashCommand(@NotNull ShardManager api, @NotNull FakeSlashData data) {
        super(api.getShards().get(0), data.getResponseNumber(), null);
        this.api = api;
        this.data = data;
    }

    @NotNull
    @Override
    public String getCommandPath() {
        return data.getPath();
    }

    @Nullable
    @Override
    public Guild getGuild() {
        return this.api.getGuildById(data.getGuildId());
    }

    @NotNull
    @Override
    public TextChannel getTextChannel() {
        return this.getGuild().getTextChannelById(data.getChannelId());
    }

    @Nullable
    @Override
    public Member getMember() {
        if (this.member != null) {
            return this.member;
        }
        this.member = this.getGuild().retrieveMemberById(data.getMemberId())
                .complete();
        return this.member;
    }

    @NotNull
    @Override
    public User getUser() {
        if (this.user != null) {
            return this.user;
        }
        this.user = this.api.retrieveUserById(data.getMemberId())
                .complete();
        return this.user;
    }

    @NotNull
    @Override
    public MessageChannel getChannel() {
        return (MessageChannel) this.getGuild().getGuildChannelById(data.getChannelId());
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
    public ReplyAction reply(@NotNull Message message) {
        return new ProxyRestAction(this).withMessage(message);
    }

    @NotNull
    @Override
    public ReplyAction deferReply() {
        return new ProxyRestAction(this);
    }

    @NotNull
    @Override
    public ReplyAction deferReply(boolean ephemeral) {
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
        return new OptionMapping(dataObject, fake.getResolved());
    }

}
