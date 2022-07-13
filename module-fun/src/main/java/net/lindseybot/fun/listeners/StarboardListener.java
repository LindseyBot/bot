package net.lindseybot.fun.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.lindseybot.fun.entities.StarboardMessage;
import net.lindseybot.fun.services.StarboardService;
import net.lindseybot.shared.entities.profile.servers.Starboard;
import net.lindseybot.shared.utils.GFXUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class StarboardListener extends ListenerAdapter {

    private final StarboardService service;

    public StarboardListener(IEventManager api, StarboardService service) {
        this.service = service;
        api.register(this);
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        MessageReaction reaction = event.getReaction();
        if (!reaction.getEmoji().getName().equals("\u2B50")) {
            return;
        } else if (!event.isFromGuild()) {
            return;
        }
        this.handleStarboard(event.getReaction(), event.getGuild());
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        MessageReaction reaction = event.getReaction();
        if (!reaction.getEmoji().getName().equals("\u2B50")) {
            return;
        } else if (!event.isFromGuild()) {
            return;
        }
        this.handleStarboard(event.getReaction(), event.getGuild());
    }

    private void handleStarboard(MessageReaction reaction, Guild guild) {
        Starboard settings = this.service.getSettings(guild);
        TextChannel starboardChannel = this.service.getChannel(guild, settings);
        if (starboardChannel == null) {
            return;
        } else if (reaction.getChannelType() != ChannelType.TEXT) {
            return;
        }
        TextChannel channel = (TextChannel) reaction.getChannel();
        if (channel.isNSFW() && !starboardChannel.isNSFW()) {
            return;
        }
        channel.retrieveMessageById(reaction.getMessageId()).queue(message -> {
            Optional<MessageReaction> rcc = message.getReactions().stream()
                    .filter(rc -> rc.getEmoji().getAsReactionCode().equals("\u2B50"))
                    .findFirst();
            StarboardMessage starboard = service.findMessage(reaction, guild, starboardChannel);
            if (rcc.isPresent()) {
                int count = rcc.get().getCount();
                if (count >= settings.getMinStars()) {
                    Message embed = this.createEmbed(count, message);
                    if (starboard.getMessageId() != null) {
                        starboardChannel.editMessageById(starboard.getMessageId(), embed)
                                .queue(this.noop(), this.noop());
                    } else {
                        starboardChannel.sendMessage(embed).queue(msg -> {
                            starboard.setMessageId(msg.getIdLong());
                            service.save(starboard);
                        }, this.noop());
                    }
                } else {
                    if (starboard.getMessageId() != null) {
                        starboardChannel.deleteMessageById(starboard.getMessageId())
                                .queue(this.noop(), this.noop());
                    }
                    service.delete(starboard);
                }
            } else {
                if (starboard.getMessageId() != null) {
                    starboardChannel.deleteMessageById(starboard.getMessageId())
                            .queue(this.noop(), this.noop());
                }
                service.delete(starboard);
            }
        }, this.noop());
    }

    private Message createEmbed(int stars, Message message) {
        if (message == null) return null;
        MessageBuilder messageBuilder = new MessageBuilder();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(message.getAuthor().getName(),
                message.getJumpUrl(), message.getAuthor().getEffectiveAvatarUrl());

        if (!message.getContentDisplay().isEmpty()) {
            builder.setDescription(message.getContentDisplay()
                    .replace("@everyone", "(at)everyone").replace("@here", "(at)here"));
        }
        if (!message.getAttachments().isEmpty()) {
            builder.setImage(message.getAttachments().get(0).getUrl());
        } else {
            try {
                URL url = new URL(message.getContentDisplay());
                if (isFile(url.getPath())) {
                    builder.setDescription(null);
                    builder.setImage(url.toString());
                }
            } catch (Exception ignored) {
                // Ignored
            }
        }
        builder.setColor(this.getColor(stars));
        builder.setTimestamp(message.getTimeCreated());
        messageBuilder.append(getIcon(stars))
                .append(" ").append(String.valueOf(stars))
                .append(" ").append(message.getTextChannel().getAsMention())
                .append(" ID: ").append(message.getId());
        messageBuilder.setEmbeds(builder.build());
        messageBuilder.setAllowedMentions(
                Arrays.asList(Message.MentionType.EMOJI, Message.MentionType.CHANNEL));
        Button button = Button.link(message.getJumpUrl(), "Jump")
                .withEmoji(Emoji.fromUnicode("\uD83D\uDD17"));
        messageBuilder.setActionRows(ActionRow.of(button));
        return messageBuilder.build();
    }

    private boolean isFile(String path) {
        return path.endsWith("gif") || path.endsWith("png") || path.endsWith("jpg") || path.endsWith("webp")
               || path.endsWith("mp4") || path.endsWith("jpeg");
    }

    private String getIcon(int stars) {
        if (stars < 5) {
            return "\u2B50"; // Star
        } else if (stars < 10) {
            return "\uD83C\uDF1F"; // Glowing
        } else if (stars < 15) {
            return "\uD83D\uDCAB"; // Dizzy
        } else if (stars < 50) {
            return "\u2728"; // Sparkles
        } else {
            return "\uD83C\uDF20"; // Shooting Star
        }
    }

    private Color getColor(int stars) {
        if (stars < 5) { // 5
            return GFXUtils.getColor("#FFEF99"); // Star
        } else if (stars < 10) { // 15
            return GFXUtils.getColor("#FFE34C"); // Glowing
        } else if (stars < 15) { //
            return GFXUtils.getColor("#FFD700"); // Dizzy
        } else if (stars < 50) {
            return GFXUtils.getColor("#D700FF"); // Sparkles
        } else {
            return GFXUtils.getColor("#0028FF"); // Shooting Star
        }
    }

    public <T> Consumer<T> noop() {
        return t -> {
        };
    }

}
