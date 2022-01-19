package net.lindseybot.economy.commands;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.lindseybot.economy.repositories.sql.UserProfileRepository;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.discord.builders.EmbedBuilder;
import net.lindseybot.shared.entities.profile.UserProfile;
import net.lindseybot.shared.enums.LeaderboardType;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Leaderboard extends InteractionHandler {

    private final UserProfileRepository repository;

    public Leaderboard(Messenger msg, UserProfileRepository repository) {
        super(msg);
        this.repository = repository;
    }

    @SlashCommand("leaderboard")
    @SuppressWarnings("ConstantConditions")
    public void onCommand(SlashCommandEvent event) {
        LeaderboardType type = LeaderboardType.fromString(event.getOption("name").getAsString());
        StringBuilder msg = new StringBuilder("```\n");
        int pos = 1;
        for (UserProfile profile : this.getPage(type, 0)) {
            msg.append("#").append(pos)
                    .append(": ").append(profile.getName())
                    .append(" - ").append(this.getValue(type, profile))
                    .append("\n");
            pos++;
        }
        msg.append("```");
        EmbedBuilder embed = new EmbedBuilder();
        embed.description(Label.raw(msg.toString()));
        embed.title(Label.raw(type.getPrettyName()));
        this.msg.reply(event, FMessage.of(embed.build()));
    }

    private String getValue(LeaderboardType type, UserProfile profile) {
        return "" + switch (type) {
            case COOKIES -> profile.getCookies();
            case SLOT_WINS -> profile.getSlotWins();
            case DAILY_STREAK -> profile.getCookieStreak();
        };
    }

    public Page<UserProfile> getPage(LeaderboardType type, int number) {
        Sort sort = switch (type) {
            case COOKIES -> Sort.by(Sort.Direction.DESC, "cookies");
            case SLOT_WINS -> Sort.by(Sort.Direction.DESC, "slotWins");
            case DAILY_STREAK -> Sort.by(Sort.Direction.DESC, "cookieStreak");
        };
        Pageable pageable = PageRequest.of(number, 15, sort);
        return this.repository.findAll(pageable);
    }

}
