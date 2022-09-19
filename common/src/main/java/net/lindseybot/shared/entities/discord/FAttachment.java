package net.lindseybot.shared.entities.discord;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Data
@NoArgsConstructor
public class FAttachment {

    private String name;
    private InputStream stream;

    public FAttachment(String name, InputStream stream) {
        this.name = name;
        this.stream = stream;
    }

    public FAttachment(String name, byte[] bytes) {
        this(name, new ByteArrayInputStream(bytes));
    }

}
