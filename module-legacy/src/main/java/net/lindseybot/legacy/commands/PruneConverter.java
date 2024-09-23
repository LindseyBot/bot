package net.lindseybot.legacy.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.lindseybot.legacy.models.SlashConverter;
import net.lindseybot.legacy.services.FinderUtil;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.legacy.fake.FakeOptionMapping;
import net.lindseybot.legacy.fake.FakeSlashData;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

@Component
public class PruneConverter extends SlashConverter {

    private final Messenger msg;

    public PruneConverter(Messenger msg) {
        this.msg = msg;
    }

    @Override
    public String[] getNames() {
        return new String[]{"prune"};
    }

    @Override
    public FakeSlashData convert(MessageReceivedEvent event, String name, String[] args) {
        if (args.length == 0) {
            // TODO: HELP
            return null;
        }
        int count;
        try {
            count = Integer.parseInt(args[0]);
            if (count < 1) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException ex) {
            this.msg.reply(event, Label.raw("That's not a number."));
            return null;
        }
        FakeSlashData data = this.createData(event, "prune");

        FakeOptionMapping optCount = new FakeOptionMapping();
        optCount.setName("count");
        optCount.setType(OptionType.INTEGER);
        optCount.setValue(String.valueOf(count));
        data.getOptions().put(optCount.getName(), optCount);

        if (args.length > 1) {
            Member target = FinderUtil.findMember(args[1], event.getGuild());
            if (target == null) {
                this.msg.reply(event, Label.raw("User not found."));
                return null;
            }
            FakeOptionMapping optTarget = new FakeOptionMapping();
            optTarget.setName("user");
            optTarget.setType(OptionType.USER);
            optTarget.setValue(target.getId());
            data.getOptions().put(optTarget.getName(), optTarget);
        }
        return data;
    }

}
