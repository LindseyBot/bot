package net.lindseybot.shared.worker.services;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.lindseybot.shared.entities.discord.*;
import net.lindseybot.shared.enums.Language;
import net.lindseybot.shared.utils.StandardEmotes;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DiscordAdapter {

    private final Translator i18n;
    private final List<Message.MentionType> DEFAULT_ALLOWED_MENTIONS;

    public DiscordAdapter(Translator i18n) {
        this.i18n = i18n;
        this.DEFAULT_ALLOWED_MENTIONS = new ArrayList<>();
        this.DEFAULT_ALLOWED_MENTIONS.add(Message.MentionType.CHANNEL);
        this.DEFAULT_ALLOWED_MENTIONS.add(Message.MentionType.EMOTE);
    }

    public String getLabel(Label msg, ISnowflake snowflake) {
        if (msg.isLiteral()) {
            return this.format(msg.getName());
        } else if (snowflake instanceof User user) {
            return this.format(this.i18n.get(user, msg.getName(), msg.getArguments()));
        } else if (snowflake instanceof Guild guild) {
            return this.format(this.i18n.get(guild, msg.getName(), msg.getArguments()));
        } else if (snowflake instanceof Member member) {
            return this.format(this.i18n.get(member, msg.getName(), msg.getArguments()));
        } else {
            return this.format(this.i18n.get(Language.en_US, msg.getName(), msg.getArguments()));
        }
    }

    private String format(String label) {
        if (!label.contains(":")) {
            return label;
        }
        return label
                .replace(":CHECK:", StandardEmotes.CHECK.asMention())
                .replace(":XCHECK:", StandardEmotes.XCHECK.asMention())
                .replace(":EMPTY:", StandardEmotes.EMPTY.asMention())
                .replace(":LOADING:", StandardEmotes.LOADING.asMention())
                .replace(":TYPING:", StandardEmotes.TYPING.asMention());
    }

    public Message getMessage(FMessage fake, ISnowflake snowflake) {
        MessageBuilder builder = new MessageBuilder();
        if (fake.getContent() != null) {
            builder.setContent(this.getLabel(fake.getContent(), snowflake));
        }
        if (fake.getAllowedMentions() != null) {
            builder.setAllowedMentions(this.allowed(fake));
        }
        if (fake.getEmbed() != null) {
            builder.setEmbeds(this.createEmbed(fake.getEmbed(), snowflake));
        }
        if (!fake.getComponents().isEmpty()) {
            List<ActionRow> rows = new ArrayList<>();
            List<ItemComponent> components = new ArrayList<>();
            int i = 0;
            for (MessageComponent component : fake.getComponents()) {
                if (component instanceof FButton button) {
                    components.add(this.createButton(button, snowflake));
                } else if (component instanceof FSelectMenu menu) {
                    if (!components.isEmpty()) {
                        ActionRow row = ActionRow.of(components);
                        rows.add(row);
                        components.clear();
                        i = 0;
                    }
                    rows.add(ActionRow.of(this.createSelectMenu(menu, snowflake)));
                    continue;
                }
                i++;
                if (i % 5 == 0) {
                    ActionRow row = ActionRow.of(components);
                    rows.add(row);
                    components.clear();
                    i = 0;
                }
            }
            if (!components.isEmpty()) {
                ActionRow row = ActionRow.of(components);
                rows.add(row);
            }
            builder.setActionRows(rows);
        }
        return builder.build();
    }

    private Button createButton(FButton model, ISnowflake snowflake) {
        ButtonStyle style = ButtonStyle.valueOf(model.getStyle().name());
        String id;
        if (style == ButtonStyle.LINK) {
            id = model.getIdOrUrl();
        } else {
            id = model.getIdOrUrl() + (model.getData() != null ? ":" + model.getData() : "");
            if (id.codePointCount(0, id.length()) > 100) {
                throw new IllegalStateException("Button id too big " + model.getIdOrUrl());
            }
        }
        Button button = Button.of(style, id, this.getLabel(model.getLabel(), snowflake));
        if (model.getEmote() != null) {
            button = button.withEmoji(this.toEmoji(model.getEmote()));
        }
        if (model.isDisabled()) {
            return button.asDisabled();
        } else {
            return button;
        }
    }

    private SelectMenu createSelectMenu(FSelectMenu model, ISnowflake snowflake) {
        List<SelectOption> options = this.createSelectOptions(model.getOptions(), snowflake);
        SelectMenu.Builder builder = SelectMenu.create(model.getId())
                .setRequiredRange(model.getMin(), model.getMax())
                .addOptions(options)
                .setDisabled(model.isDisabled());
        if (model.getLabel() != null) {
            builder.setPlaceholder(this.getLabel(model.getLabel(), snowflake));
        }
        return builder.build();
    }

    private List<SelectOption> createSelectOptions(List<FSelectOption> models, ISnowflake snowflake) {
        List<SelectOption> options = new ArrayList<>();
        for (FSelectOption model : models) {
            SelectOption option = SelectOption
                    .of(this.getLabel(model.getLabel(), snowflake), model.getId());
            if (model.getEmote() != null) {
                option = option.withEmoji(toEmoji(model.getEmote()));
            }
            if (model.getDescription() != null) {
                option = option.withDescription(this.getLabel(model.getDescription(), snowflake));
            }
            if (model.isDefault()) {
                option = option.withDefault(model.isDefault());
            }
            options.add(option);
        }
        return options;
    }

    private Emoji toEmoji(FEmote emote) {
        if (emote.isUnicode()) {
            return Emoji.fromUnicode(emote.getName());
        } else {
            return Emoji.fromEmote(emote.getName(), emote.getId(), emote.isAnimated());
        }
    }

    public MessageEmbed createEmbed(FEmbed request, ISnowflake snowflake) {
        EmbedBuilder builder = new EmbedBuilder();
        if (request.getTitle() != null) {
            String text = this.getLabel(request.getTitle(), snowflake);
            builder.setTitle(text, request.getUrl());
        }
        if (request.getColor() != null) {
            builder.setColor(request.getColor());
        }
        if (request.getDescription() != null) {
            String text = this.getLabel(request.getDescription(), snowflake);
            builder.setDescription(text);
        }
        if (request.getImage() != null) {
            builder.setImage(request.getImage());
        }
        if (request.getThumbnail() != null) {
            builder.setThumbnail(request.getThumbnail());
        }
        if (request.getTimestamp() != null) {
            OffsetDateTime time = OffsetDateTime.ofInstant(Instant.ofEpochMilli(request.getTimestamp()), ZoneOffset.UTC);
            builder.setTimestamp(time);
        }
        if (request.getAuthor() != null) {
            String name = this.getLabel(request.getAuthor().getName(), snowflake);
            builder.setAuthor(name, request.getAuthor().getUrl(), request.getAuthor().getIcon());
        }
        if (request.getFooter() != null) {
            String text = this.getLabel(request.getFooter().getText(), snowflake);
            builder.setFooter(text, request.getFooter().getIcon());
        }
        if (!request.getFields().isEmpty()) {
            for (FEmbed.Field field : request.getFields()) {
                String name = this.getLabel(field.getName(), snowflake);
                String value = this.getLabel(field.getValue(), snowflake);
                builder.addField(name, value, field.isInline());
            }
        }
        return builder.build();
    }

    public List<Message.MentionType> allowed(FMessage fake) {
        if (fake.getAllowedMentions().isEmpty()) {
            return DEFAULT_ALLOWED_MENTIONS;
        }
        List<Message.MentionType> types = new ArrayList<>();
        for (MentionType type : fake.getAllowedMentions()) {
            try {
                types.add(Message.MentionType.valueOf(type.name()));
            } catch (IllegalArgumentException ex) {
                log.error("Invalid mention type " + type.name());
            }
        }
        return types;
    }

}
