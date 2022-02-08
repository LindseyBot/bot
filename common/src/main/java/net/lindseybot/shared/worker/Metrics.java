package net.lindseybot.shared.worker;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Summary;
import io.prometheus.client.exporter.PushGateway;
import lombok.extern.slf4j.Slf4j;
import net.lindseybot.shared.properties.PrometheusProperties;

import java.io.IOException;

@Slf4j
public class Metrics {

    private final PushGateway pushgateway;
    private final CollectorRegistry registry = new CollectorRegistry();

    public Metrics(PrometheusProperties config) {
        this.pushgateway = new PushGateway(config.getHost());
    }

    public void push() {
        try {
            this.pushgateway.push(registry, "lindsey_bot");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final Summary commands = Summary.build()
            .name("lindsey_commands")
            .help("Command execution timings.")
            .labelNames("name", "slash")
            .register(registry);

    public Summary.Child commands(String name, boolean slash) {
        return this.commands.labels(name, String.valueOf(slash));
    }

}
