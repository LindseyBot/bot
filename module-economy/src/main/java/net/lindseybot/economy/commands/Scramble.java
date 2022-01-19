package net.lindseybot.economy.commands;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.lindseybot.economy.listeners.ScrambleListener;
import net.lindseybot.economy.models.ScrambleModel;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.enums.Language;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class Scramble extends InteractionHandler {

    private final ScrambleListener listener;

    public Scramble(Messenger msg, ScrambleListener listener) {
        super(msg);
        this.listener = listener;
    }

    @SlashCommand("scramble")
    public void onCommand(SlashCommandEvent event) {
        if (event.getGuild() == null) {
            return;
        } else if (this.listener.has(event.getGuild())) {
            ScrambleModel model = this.listener.get(event.getGuild());
            this.msg.error(event, Label.of("commands.scramble.already", model.getScrambled()));
            return;
        }
        String word = this.getWord(Language.en_US);
        if (word == null) {
            this.msg.error(event, Label.of("internal.error"));
            return;
        }
        ScrambleModel model = new ScrambleModel();
        model.setId(event.getGuild().getIdLong());
        model.setWord(word);
        model.setScrambled(this.scramble(word));
        model.setChannelId(event.getChannel().getIdLong());
        model.setStartTime(System.currentTimeMillis());
        this.listener.createGame(model);
        this.msg.reply(event, Label.of("commands.scramble.started", model.getScrambled()));
    }

    @SuppressWarnings("SameParameterValue")
    private String getWord(Language language) {
        File file = new File("scramble-en_US.txt");
        if (!file.exists()) {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .build();
                Request request = new Request.Builder()
                        .url("https://cdn.lindseybot.net/i18n/words/" + language.name() + ".txt")
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        ResponseBody body = response.body();
                        if (body == null) {
                            return null;
                        }
                        Files.copy(body.byteStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        body.close();
                    }
                } catch (IOException ex) {
                    log.error("Failed to load language " + language.name(), ex);
                }
            } catch (Exception ex) {
                return null;
            }
        }
        int random = new Random().nextInt(1762);
        try (Stream<String> stream = Files.lines(file.toPath())) {
            return stream.skip(random).findAny()
                    .orElse(null);
        } catch (IOException e) {
            return null;
        }
    }

    private String scramble(String word) {
        Random random = new Random();
        // Convert your string into a simple char array:
        char[] a = word.toCharArray();
        // Scramble the letters using the standard Fisher-Yates shuffle,
        for (int i = 0; i < a.length; i++) {
            int j = random.nextInt(a.length);
            // Swap letters
            char temp = a[i];
            a[i] = a[j];
            a[j] = temp;
        }
        String norM = new String(a);
        List<Character> chars = new ArrayList<>();
        for (char c : norM.toCharArray()) {
            if (random.nextBoolean()) {
                chars.add(c);
                chars.add('\u2063');
            } else {
                chars.add(c);
            }
        }
        return chars.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

}
