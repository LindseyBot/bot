package net.lindseybot.shared.entities.discord;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.utils.AttachmentOption;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Data
@NoArgsConstructor
public class FAttachment {

    private String name;
    private InputStream stream;
    private AttachmentOption[] flags = new AttachmentOption[0];

    public FAttachment(String name, InputStream stream, AttachmentOption... flags) {
        this.name = name;
        this.stream = stream;
        this.flags = flags;
    }

    public FAttachment(String name, byte[] bytes, AttachmentOption... flags) {
        this(name, new ByteArrayInputStream(bytes), flags);
    }

    public void setFlags(AttachmentOption... flags) {
        this.flags = flags;
    }

}
