package net.lindseybot.legacy.commands;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.lindseybot.legacy.models.SlashConverter;
import net.lindseybot.legacy.services.FinderUtil;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.legacy.fake.FakeOptionMapping;
import net.lindseybot.legacy.fake.FakeSlashData;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class VoiceConverter extends SlashConverter {

    private final Messenger msg;

    public VoiceConverter(Messenger msg) {
        this.msg = msg;
    }

    @Override
    public String[] getNames() {
        return new String[]{"voice"};
    }

    @Override
    public FakeSlashData convert(MessageReceivedEvent event, String name, String[] args) {
        if (args.length < 3) {
            // TODO: HELP
            return null;
        }
        if (args[0].equalsIgnoreCase("split")) {
            Optional<TextChannel> oLeft = FinderUtil.findTextChannel(args[1], event.getGuild());
            if (oLeft.isEmpty()) {
                this.msg.reply(event, Label.raw("Left channel not found."));
                return null;
            }
            Optional<TextChannel> oRight = FinderUtil.findTextChannel(args[2], event.getGuild());
            if (oRight.isEmpty()) {
                this.msg.reply(event, Label.raw("Right channel not found."));
                return null;
            }
            FakeSlashData data = this.createData(event, "voice/split");

            FakeOptionMapping optFrom = new FakeOptionMapping();
            optFrom.setName("from");
            optFrom.setType(OptionType.CHANNEL);
            optFrom.setValue(oLeft.get().getId());
            data.getOptions().put(optFrom.getName(), optFrom);

            FakeOptionMapping optTo = new FakeOptionMapping();
            optTo.setName("to");
            optTo.setType(OptionType.CHANNEL);
            optTo.setValue(oRight.get().getId());
            data.getOptions().put(optTo.getName(), optTo);

            return data;
        } else {
            Optional<TextChannel> oLeft = FinderUtil.findTextChannel(args[1], event.getGuild());
            if (oLeft.isEmpty()) {
                this.msg.reply(event, Label.raw("Left channel not found."));
                return null;
            }
            Optional<TextChannel> oRight = FinderUtil.findTextChannel(args[2], event.getGuild());
            if (oRight.isEmpty()) {
                this.msg.reply(event, Label.raw("Right channel not found."));
                return null;
            }
            FakeSlashData data = this.createData(event, "voice/move");

            FakeOptionMapping optFrom = new FakeOptionMapping();
            optFrom.setName("from");
            optFrom.setType(OptionType.CHANNEL);
            optFrom.setValue(oLeft.get().getId());
            data.getOptions().put(optFrom.getName(), optFrom);

            FakeOptionMapping optTo = new FakeOptionMapping();
            optTo.setName("to");
            optTo.setType(OptionType.CHANNEL);
            optTo.setValue(oRight.get().getId());
            data.getOptions().put(optTo.getName(), optTo);

            return data;
        }
    }

}
