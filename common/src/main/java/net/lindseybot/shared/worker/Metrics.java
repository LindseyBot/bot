package net.lindseybot.shared.worker;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Summary;
import io.prometheus.client.exporter.HTTPServer;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;

@Slf4j
public class Metrics implements Closeable {

    private final HTTPServer server;

    private final Summary commands;

    public Metrics() throws IOException {
        CollectorRegistry registry = new CollectorRegistry();
        this.server = new HTTPServer.Builder()
                .withPort(1234)
                .withRegistry(registry)
                .build();
        this.commands = Summary.build()
                .name("lindsey_commands")
                .help("Command execution timings.")
                .labelNames("name", "slash")
                .register(registry);
    }

    @Override
    public void close() {
        this.server.close();
    }

    public Summary.Child commands(String name, boolean slash) {
        return this.commands.labels(name, String.valueOf(slash));
    }

}
