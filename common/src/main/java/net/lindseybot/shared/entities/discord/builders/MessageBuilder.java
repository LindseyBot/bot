package net.lindseybot.shared.entities.discord.builders;

import net.lindseybot.shared.entities.discord.FEmbed;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.discord.MessageComponent;
import net.lindseybot.shared.entities.discord.MentionType;

import java.util.Arrays;

public class MessageBuilder {

    private final FMessage message;

    public MessageBuilder() {
        this.message = new FMessage();
    }

    /**
     * Creates a MessageBuilder with label as base.
     *
     * @param label Label.
     */
    public MessageBuilder(Label label) {
        this.message = new FMessage();
        this.content(label);
    }

    /**
     * Updates the content of this message.
     *
     * @param label Label.
     * @return Builder for chaining.
     */
    public MessageBuilder content(Label label) {
        this.message.setContent(label);
        return this;
    }

    /**
     * Specifies the list of allowed mentions, used for allowing everyone/here mentions.
     *
     * @param types List of allowed mention types.
     * @return Builder for chaining.
     */
    public MessageBuilder allowedMentions(MentionType... types) {
        this.message.setAllowedMentions(Arrays.asList(types));
        return this;
    }

    /**
     * Updates the embed of this message. A message may contain an embed, or be just an embed.
     *
     * @param embed Embed. See {@link EmbedBuilder}.
     * @return Builder for chaining.
     */
    public MessageBuilder embed(FEmbed embed) {
        this.message.setEmbed(embed);
        return this;
    }

    /**
     * Specifies the list of components for this message.
     * See {@link ButtonBuilder}.
     *
     * @param components List of components.
     * @return Builder for chaining.
     */
    public MessageBuilder components(MessageComponent... components) {
        this.message.setComponents(Arrays.asList(components));
        return this;
    }

    /**
     * Adds a component to this message.
     * See {@link ButtonBuilder}.
     *
     * @param component The component to add.
     * @return Builder for chaining.
     */
    public MessageBuilder addComponent(MessageComponent component) {
        this.message.getComponents().add(component);
        return this;
    }

    /**
     * Marks this message as ephemeral, meaning it will only appear to the target user
     * and cannot be updated or deleted. This can only be used on interactions and will
     * be ignored if the meta does not support ephemeral messages.
     *
     * @return Builder for chaining.
     */
    public MessageBuilder ephemeral() {
        this.message.setEphemeral(true);
        return this;
    }

    /**
     * Builds this message.
     *
     * @return Message.
     */
    public FMessage build() {
        return this.message;
    }

}
