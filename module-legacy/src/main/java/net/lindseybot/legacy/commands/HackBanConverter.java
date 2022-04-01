package net.lindseybot.legacy.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.lindseybot.legacy.models.SlashConverter;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.worker.legacy.FakeOptionMapping;
import net.lindseybot.shared.worker.legacy.FakeSlashData;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

@Component
public class HackBanConverter extends SlashConverter {

    private final Messenger msg;

    public HackBanConverter(Messenger msg) {
        this.msg = msg;
    }

    @Override
    public String[] getNames() {
        return new String[]{"hackban"};
    }

    @Override
    public FakeSlashData convert(MessageReceivedEvent event, String name, String[] args) {
        if (args.length < 2) {
            return null;
        }
        long id;
        try {
            id = Long.parseLong(args[0]);
        } catch (IllegalArgumentException ex) {
            this.msg.reply(event, Label.raw("That's not a number"));
            return null;
        }
        FakeSlashData data = this.createData(event, "hackban");

        FakeOptionMapping optUser = new FakeOptionMapping();
        optUser.setName("user");
        optUser.setType(OptionType.USER);
        optUser.setValue(String.valueOf(id));
        data.getOptions().put(optUser.getName(), optUser);

        FakeOptionMapping optReason = new FakeOptionMapping();
        optReason.setName("reason");
        optReason.setType(OptionType.STRING);
        optReason.setValue(argsToString(args, 1));
        data.getOptions().put(optReason.getName(), optReason);

        return data;
    }

}
