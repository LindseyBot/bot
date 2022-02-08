package net.lindseybot.bot.listener;

import lombok.extern.slf4j.Slf4j;
import net.lindseybot.shared.worker.Metrics;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MetricsListener {

    private final Metrics metrics;

    public MetricsListener(Metrics metrics) {
        this.metrics = metrics;
    }

    @Scheduled(fixedRate = 15000)
    public void onPush() {
        this.metrics.push();
    }

}
