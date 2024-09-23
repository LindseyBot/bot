package net.lindseybot.bot.scheduled;

import lombok.extern.slf4j.Slf4j;
import net.lindseybot.bot.repositories.sql.ServerRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CleanupTask {

    private final ServerRepository servers;

    public CleanupTask(ServerRepository servers) {
        this.servers = servers;
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    public void onCleanup() {
        long time = Instant.ofEpochMilli(System.currentTimeMillis())
                .truncatedTo(ChronoUnit.DAYS)
                .minus(90, ChronoUnit.DAYS)
                .toEpochMilli();
        var servers = this.servers.findGuildByLastSeenLessThan(time);
        if (!servers.isEmpty()) {
            log.info("Pruning data from {} servers", servers.size());
        }
    }

}
