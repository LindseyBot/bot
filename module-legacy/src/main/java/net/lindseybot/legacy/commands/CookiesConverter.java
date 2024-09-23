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
public class CookiesConverter extends SlashConverter {

    private final Messenger msg;

    public CookiesConverter(Messenger msg) {
        this.msg = msg;
    }

    @Override
    public String[] getNames() {
        return new String[]{"cookies", "cookie"};
    }

    @Override
    public FakeSlashData convert(MessageReceivedEvent event, String name, String[] args) {
        if (args.length == 0) {
            return this.createData(event, "cookies/balance");
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("daily")) {
                return this.createData(event, "cookies/daily");
            }
            this.msg.reply(event, Label.raw("Invalid subcommand."));
            return null;
        } else {
            Member target = FinderUtil.findMember(args[0], event.getGuild());
            if (target == null) {
                this.msg.reply(event, Label.raw("User Not Found! I will keep this cookie to myself! nom nom nom"));
                return null;
            }
            int cookies;
            try {
                cookies = Integer.parseInt(args[1]);
                if (cookies < 1) {
                    throw new IllegalArgumentException();
                }
            } catch (IllegalArgumentException ex) {
                this.msg.reply(event, Label.raw("That's not a cookie count! Choose the amount of cookies you want to send."));
                return null;
            }
            FakeSlashData data = this.createData(event, "cookies/send");
            {
                FakeOptionMapping option = new FakeOptionMapping();
                option.setName("target");
                option.setType(OptionType.USER);
                option.setValue(target.getId());
                data.getOptions().put(option.getName(), option);
            }
            {
                FakeOptionMapping option = new FakeOptionMapping();
                option.setName("amount");
                option.setType(OptionType.INTEGER);
                option.setValue(String.valueOf(cookies));
                data.getOptions().put(option.getName(), option);
            }
            return data;
        }
    }

}
