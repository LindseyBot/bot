package net.lindseybot.shared.entities.items;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "items_background")
public class Background extends Item {

    private String fontColor;

}
