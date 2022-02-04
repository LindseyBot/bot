package net.lindseybot.shared.worker.impl;

import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;

@Slf4j
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
        Response response = chain.proceed(request.newBuilder()
                .url(builder.build()).build());
        if (response.code() == 429) {
            if (response.body() != null) {
                response.close();
            }
            log.warn("Encountered 429 on route: " + request.url().pathSegments() + ".. retrying.");
            response = chain.proceed(request.newBuilder()
                    .url(builder.build()).build());
            if (response.code() == 429) {
                log.warn("Encountered another 429 on route: " + request.url().pathSegments() + ". Failing request.");
                return replaceStatus(response);
            } else {
                return stripHeaders(response);
            }
        } else {
            return this.stripHeaders(response);
        }
    }

    private Response replaceStatus(Response response) {
        return stripHeaders(response.newBuilder().code(400).build());
    }

    private Response stripHeaders(Response response) {
        return response.newBuilder()
                .removeHeader("X-RateLimit-Global")
                .removeHeader("X-RateLimit-Global".toLowerCase())
                .removeHeader("X-RateLimit-Limit")
                .removeHeader("X-RateLimit-Limit".toLowerCase())
                .removeHeader("X-RateLimit-Remaining")
                .removeHeader("X-RateLimit-Remaining".toLowerCase())
                .removeHeader("X-RateLimit-Reset")
                .removeHeader("X-RateLimit-Reset".toLowerCase())
                .removeHeader("X-RateLimit-Reset-After")
                .removeHeader("X-RateLimit-Reset-After".toLowerCase())
                .removeHeader("X-RateLimit-Bucket")
                .removeHeader("X-RateLimit-Bucket".toLowerCase())
                .build();
    }

}
