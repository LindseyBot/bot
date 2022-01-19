package net.lindseybot.bot.services;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.lindseybot.bot.repositories.sql.NotificationRepository;
import net.lindseybot.shared.entities.Notification;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.utils.Snowflake;
import net.lindseybot.shared.worker.services.NotificationService;
import net.lindseybot.shared.worker.services.Translator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    private final Snowflake snowflake;
    private final Translator translator;
    private final NotificationRepository repository;

    public NotificationServiceImpl(Snowflake snowflake, Translator translator,
                                   NotificationRepository repository) {
        this.snowflake = snowflake;
        this.translator = translator;
        this.repository = repository;
    }

    @Override
    public void notify(Guild guild, Label message) {
        String msg;
        if (message.isLiteral()) {
            msg = message.getName();
        } else {
            msg = this.translator.get(guild, message.getName(), message.getArguments());
        }
        Notification notification = new Notification();
        notification.setId(this.snowflake.next());
        notification.setMessage(msg);
        notification.setTimestamp(System.currentTimeMillis());
        notification.setGuildId(guild.getIdLong());
        this.repository.save(notification);
    }

    @Transactional
    @Scheduled(cron = "0 0 * * * *")
    public void onDaily() {
        long time = Instant.ofEpochMilli(System.currentTimeMillis())
                .truncatedTo(ChronoUnit.DAYS)
                .minus(15, ChronoUnit.DAYS)
                .toEpochMilli();
        int deleted = this.repository.deleteOutdated(time);
        log.info("Deleted {} outdated log entries.", deleted);
    }

    @Override
    public List<Notification> findRecent(Guild guild) {
        return this.repository.findByGuild(guild.getIdLong())
                .stream().limit(15)
                .toList();
    }

}
