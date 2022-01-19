package net.lindseybot.shared.worker.impl;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;

public class DiscordInterceptor implements Interceptor {

    private volatile String host;
    private volatile Integer port;
    private volatile String scheme;

    public DiscordInterceptor(URL url) {
        this.host = url.getHost();
        this.scheme = url.getProtocol();
        if (url.getPort() != -1) {
            this.port = url.getPort();
        }
    }

    public DiscordInterceptor(String host) {
        this.host = host;
    }

    public DiscordInterceptor(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public DiscordInterceptor(String host, int port, String scheme) {
        this.host = host;
        this.port = port;
        this.scheme = scheme;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    @Override
    public @NotNull Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        if (!"discord.com".equals(request.url().host())) {
            return chain.proceed(request);
        }
        HttpUrl.Builder builder = request.url()
                .newBuilder();
        if (this.host != null) {
            builder.host(this.host);
        }
        if (this.port != null) {
            builder.port(this.port);
        }
        if (this.scheme != null) {
            builder.scheme(scheme);
        }
        return chain.proceed(request.newBuilder().url(builder.build()).build())
                .newBuilder()
                .removeHeader("X-RateLimit-Global")
                .removeHeader("X-RateLimit-Limit")
                .removeHeader("X-RateLimit-Remaining")
                .removeHeader("X-RateLimit-Reset")
                .removeHeader("X-RateLimit-Reset-After")
                .removeHeader("X-RateLimit-Bucket")
                .build();
    }

}
