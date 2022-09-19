package net.lindseybot.shared.worker.legacy.proxy;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.lindseybot.shared.worker.legacy.FakeSlashCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class ProxyRestAction implements ReplyCallbackAction {

    private MessageCreateData response;
    private EnumSet<Message.MentionType> mentions;
    private final FakeSlashCommand cmd;

    public ProxyRestAction(FakeSlashCommand cmd) {
        this.cmd = cmd;
        this.mentions = EnumSet.of(Message.MentionType.EMOJI, Message.MentionType.CHANNEL);
    }

    public ReplyCallbackAction withMessage(MessageCreateData message) {
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
        this.cmd.getTextChannel().sendMessage(this.response).setAllowedMentions(mentions).queue((a) -> {
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
    public ReplyCallbackAction setEphemeral(boolean ephemeral) {
        return this;
    }

    @NotNull
    @Override
    public ReplyCallbackAction setAllowedMentions(@Nullable Collection<Message.MentionType> allowedMentions) {
        if (allowedMentions != null) {
            this.mentions = EnumSet.copyOf(allowedMentions);
        }
        return this;
    }

    @NotNull
    @Override
    public ReplyCallbackAction mention(@NotNull Collection<? extends IMentionable> mentions) {
        return null;
    }

    // ------------------------------------

    @Override
    public JDA getJDA() {
        return null;
    }

    @NotNull
    @Override
    public ReplyCallbackAction setCheck(@Nullable BooleanSupplier checks) {
        return null;
    }

    @NotNull
    @Override
    public ReplyCallbackAction timeout(long timeout, @NotNull TimeUnit unit) {
        return null;
    }

    @NotNull
    @Override
    public ReplyCallbackAction deadline(long timestamp) {
        return null;
    }

    @NotNull
    @Override
    public ReplyCallbackAction closeResources() {
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
    public ReplyCallbackAction addContent(@NotNull String content) {
        return null;
    }

    @NotNull
    @Override
    public ReplyCallbackAction addEmbeds(@NotNull Collection<? extends MessageEmbed> embeds) {
        return null;
    }

    @NotNull
    @Override
    public ReplyCallbackAction addComponents(@NotNull Collection<? extends LayoutComponent> components) {
        return null;
    }

    @NotNull
    @Override
    public ReplyCallbackAction addFiles(@NotNull Collection<? extends FileUpload> files) {
        return null;
    }

    @NotNull
    @Override
    public String getContent() {
        return null;
    }

    @NotNull
    @Override
    public List<MessageEmbed> getEmbeds() {
        return null;
    }

    @NotNull
    @Override
    public List<LayoutComponent> getComponents() {
        return null;
    }

    @NotNull
    @Override
    public List<FileUpload> getAttachments() {
        return null;
    }

    @Override
    public boolean isSuppressEmbeds() {
        return false;
    }

    @NotNull
    @Override
    public Set<String> getMentionedUsers() {
        return null;
    }

    @NotNull
    @Override
    public Set<String> getMentionedRoles() {
        return null;
    }

    @NotNull
    @Override
    public EnumSet<Message.MentionType> getAllowedMentions() {
        return null;
    }

    @Override
    public boolean isMentionRepliedUser() {
        return false;
    }

    @NotNull
    @Override
    public ReplyCallbackAction setContent(@Nullable String content) {
        return null;
    }

    @NotNull
    @Override
    public ReplyCallbackAction setEmbeds(@NotNull Collection<? extends MessageEmbed> embeds) {
        return null;
    }

    @NotNull
    @Override
    public ReplyCallbackAction setComponents(@NotNull Collection<? extends LayoutComponent> components) {
        return null;
    }

    @NotNull
    @Override
    public ReplyCallbackAction setSuppressEmbeds(boolean suppress) {
        return null;
    }

    @NotNull
    @Override
    public ReplyCallbackAction setFiles(@Nullable Collection<? extends FileUpload> files) {
        return null;
    }

    @NotNull
    @Override
    public ReplyCallbackAction setTTS(boolean isTTS) {
        return null;
    }

    @NotNull
    @Override
    public ReplyCallbackAction mentionRepliedUser(boolean mention) {
        return null;
    }

    @NotNull
    @Override
    public ReplyCallbackAction mention(@NotNull IMentionable... mentions) {
        return null;
    }

    @NotNull
    @Override
    public ReplyCallbackAction mentionUsers(@NotNull Collection<String> userIds) {
        return null;
    }

    @NotNull
    @Override
    public ReplyCallbackAction mentionUsers(@NotNull String... userIds) {
        return null;
    }

    @NotNull
    @Override
    public ReplyCallbackAction mentionRoles(@NotNull Collection<String> roleIds) {
        return null;
    }

    @NotNull
    @Override
    public ReplyCallbackAction mentionRoles(@NotNull String... roleIds) {
        return null;
    }

}
