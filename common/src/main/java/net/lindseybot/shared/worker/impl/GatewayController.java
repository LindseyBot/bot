package net.lindseybot.shared.worker.impl;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.exceptions.AccountTypeException;
import net.dv8tion.jda.api.utils.ConcurrentSessionController;
import net.lindseybot.shared.properties.BotProperties;
import net.lindseybot.shared.properties.ShardProperties;
import org.jetbrains.annotations.NotNull;

public class GatewayController extends ConcurrentSessionController {

    private final BotProperties config;

    public GatewayController(BotProperties config) {
        this.config = config;
    }

    @NotNull
    @Override
    public String getGateway() {
        return config.getGateway();
    }

    @NotNull
    @Override
    public ShardedGateway getShardedGateway(@NotNull JDA api) {
        AccountTypeException.check(api.getAccountType(), AccountType.BOT);
        ShardProperties shards = config.getShards();
        int concurrency = shards.getTotal() > 2 ? 16 : shards.getTotal();
        return new ShardedGateway(config.getGateway(), shards.getTotal(), concurrency);
    }

}
