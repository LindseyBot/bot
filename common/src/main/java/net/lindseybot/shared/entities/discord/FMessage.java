package net.lindseybot.shared.entities.discord;

import lombok.Data;
import net.lindseybot.shared.entities.discord.builders.MessageBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
public class FMessage {

    private Label content;
    private boolean ephemeral = false;
    private Long selfDestruct;

    private FEmbed embed;
    private List<MentionType> allowedMentions = new ArrayList<>();
    private List<MessageComponent> components = new ArrayList<>();

    /**
     * Starts a builder to create a message.
     *
     * @return A new MessageBuilder.
     */
    public static MessageBuilder builder() {
        return new MessageBuilder();
    }

    /**
     * Creates a message from a label.
     *
     * @param label Label.
     * @return Message.
     */
    public static FMessage of(Label label) {
        FMessage message = new FMessage();
        message.setContent(label);
        return message;
    }

    /**
     * Creates a message from a label, with ephemeral flag.
     *
     * @param label     Label.
     * @param ephemeral flag.
     * @return Message.
     */
    public static FMessage of(Label label, boolean ephemeral) {
        FMessage message = new FMessage();
        message.setContent(label);
        message.setEphemeral(ephemeral);
        return message;
    }

    /**
     * Creates a message from an embed.
     *
     * @param embed Embed.
     * @return Message.
     */
    public static FMessage of(FEmbed embed) {
        FMessage message = new FMessage();
        message.setEmbed(embed);
        return message;
    }

    /**
     * Creates a message from an embed, with ephemeral flag.
     *
     * @param embed     Embed.
     * @param ephemeral flag.
     * @return Message.
     */
    public static FMessage of(FEmbed embed, boolean ephemeral) {
        FMessage message = new FMessage();
        message.setEmbed(embed);
        message.setEphemeral(ephemeral);
        return message;
    }

}
