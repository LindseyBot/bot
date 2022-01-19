package net.lindseybot.shared.utils;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Assets {

    private static final ExpiringMap<String, byte[]> cache = ExpiringMap.builder()
            .maxSize(150)
            .expirationPolicy(ExpirationPolicy.ACCESSED)
            .expiration(15, TimeUnit.MINUTES)
            .build();
    private static final OkHttpClient httpClient = new OkHttpClient()
            .newBuilder()
            .followSslRedirects(true)
            .build();

    public static BufferedImage getImage(String url) {
        return createImageFromBytes(getBytes(url));
    }

    private static BufferedImage createImageFromBytes(byte[] imageData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] getBytes(String url) {
        if (cache.containsKey(url)) {
            return cache.get(url);
        }
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Lindsey/1.0")
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IllegalStateException("Failed to fetch asset " + url);
            }
            ResponseBody body = response.body();
            if (body == null) {
                throw new IllegalStateException("Failed to parse asset response " + url);
            }
            byte[] bytes = body.bytes();
            cache.put(url, bytes);
            return bytes;
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to connect to asset cdn " + url);
        }
    }

}
