package net.lindseybot.shared.worker.legacy.proxy;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import net.dv8tion.jda.api.utils.AttachmentOption;
import net.lindseybot.shared.worker.legacy.FakeSlashCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.Collection;
import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class ProxyRestAction implements ReplyAction {

    private Message response;
    private EnumSet<Message.MentionType> mentions;
    private final FakeSlashCommand cmd;

    public ProxyRestAction(FakeSlashCommand cmd) {
        this.cmd = cmd;
        this.mentions = EnumSet.of(Message.MentionType.EMOTE, Message.MentionType.CHANNEL);
    }

    public ReplyAction withMessage(Message message) {
        this.response = message;
        return this;
    }

    @Override
    public void queue(@Nullable Consumer<? super InteractionHook> success,
                      @Nullable Consumer<? super Throwable> failure) {
        if (this.response == null) {
            if (success != null) {
                success.accept(null);
            }
            return;
        }
        this.cmd.getTextChannel().sendMessage(this.response).allowedMentions(mentions).queue((a) -> {
            if (success != null) {
                success.accept(null);
            }
        }, (b) -> {
            if (failure != null) {
                failure.accept(b);
            }
        });
    }

    @NotNull
    @Override
    public ReplyAction setEphemeral(boolean ephemeral) {
        return this;
    }

    @NotNull
    @Override
    public ReplyAction allowedMentions(@Nullable Collection<Message.MentionType> allowedMentions) {
        if (allowedMentions != null) {
            this.mentions = EnumSet.copyOf(allowedMentions);
        }
        return this;
    }

    // ------------------------------------

    @Override
    public JDA getJDA() {
        return null;
    }

    @NotNull
    @Override
    public ReplyAction setCheck(@Nullable BooleanSupplier checks) {
        return null;
    }

    @NotNull
    @Override
    public ReplyAction timeout(long timeout, @NotNull TimeUnit unit) {
        return null;
    }

    @NotNull
    @Override
    public ReplyAction deadline(long timestamp) {
        return null;
    }

    @Override
    public InteractionHook complete(boolean shouldQueue) throws RateLimitedException {
        return null;
    }

    @NotNull
    @Override
    public CompletableFuture<InteractionHook> submit(boolean shouldQueue) {
        return null;
    }

    @NotNull
    @Override
    public ReplyAction addEmbeds(@NotNull Collection<? extends MessageEmbed> embeds) {
        return null;
    }

    @NotNull
    @Override
    public ReplyAction addActionRows(@NotNull ActionRow... rows) {
        return null;
    }

    @NotNull
    @Override
    public ReplyAction setContent(@Nullable String content) {
        return null;
    }

    @NotNull
    @Override
    public ReplyAction setTTS(boolean isTTS) {
        return null;
    }

    @NotNull
    @Override
    public ReplyAction addFile(@NotNull InputStream data, @NotNull String name, @NotNull AttachmentOption... options) {
        return null;
    }

    @NotNull
    @Override
    public ReplyAction mentionRepliedUser(boolean mention) {
        return null;
    }

    @NotNull
    @Override
    public ReplyAction mention(@NotNull IMentionable... mentions) {
        return null;
    }

    @NotNull
    @Override
    public ReplyAction mentionUsers(@NotNull String... userIds) {
        return null;
    }

    @NotNull
    @Override
    public ReplyAction mentionRoles(@NotNull String... roleIds) {
        return null;
    }

}
