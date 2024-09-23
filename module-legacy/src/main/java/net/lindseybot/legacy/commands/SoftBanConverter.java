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
public class SoftBanConverter extends SlashConverter {

    private final Messenger msg;

    public SoftBanConverter(Messenger msg) {
        this.msg = msg;
    }

    @Override
    public String[] getNames() {
        return new String[]{"softban"};
    }

    @Override
    public FakeSlashData convert(MessageReceivedEvent event, String name, String[] args) {
        if (args.length == 0) {
            // TODO: HELP
            return null;
        }
        Member target = FinderUtil.findMember(args[0], event.getGuild());
        if (target == null) {
            this.msg.reply(event, Label.raw("User not found."));
            return null;
        }
        FakeSlashData data = this.createData(event, "softban");

        FakeOptionMapping optUser = new FakeOptionMapping();
        optUser.setName("user");
        optUser.setType(OptionType.USER);
        optUser.setValue(target.getId());
        data.getOptions().put(optUser.getName(), optUser);

        if (args.length > 1) {
            FakeOptionMapping optReason = new FakeOptionMapping();
            optReason.setName("reason");
            optReason.setType(OptionType.STRING);
            optReason.setValue(argsToString(args, 1));
            data.getOptions().put(optReason.getName(), optReason);
        }

        return data;
    }

}
