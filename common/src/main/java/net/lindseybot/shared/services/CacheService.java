package net.lindseybot.shared.services;

import lombok.Getter;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import net.lindseybot.shared.entities.profile.servers.AntiAd;
import net.lindseybot.shared.entities.profile.servers.AntiScam;
import net.lindseybot.shared.entities.profile.servers.Registration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Getter
public class CacheService {

    private final Map<Long, Boolean> roleHistory = ExpiringMap.builder()
            .expirationPolicy(ExpirationPolicy.ACCESSED)
            .expiration(10, TimeUnit.MINUTES)
            .maxSize(50_000)
            .build();

    private final Map<Long, AntiScam> antiScam = ExpiringMap.builder()
            .expirationPolicy(ExpirationPolicy.ACCESSED)
            .expiration(10, TimeUnit.MINUTES)
            .maxSize(50_000)
            .build();

    private final Map<Long, AntiAd> antiAd = ExpiringMap.builder()
            .expirationPolicy(ExpirationPolicy.ACCESSED)
            .expiration(30, TimeUnit.MINUTES)
            .maxSize(50_000)
            .build();

    private final Map<Long, Registration> registration = ExpiringMap.builder()
            .expirationPolicy(ExpirationPolicy.ACCESSED)
            .expiration(30, TimeUnit.MINUTES)
            .maxSize(50_000)
            .build();

}
