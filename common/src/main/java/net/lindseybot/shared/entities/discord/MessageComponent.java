package net.lindseybot.shared.entities.discord;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public interface MessageComponent {
}
