package net.lindseybot.shared.entities.items;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "items_background")
public class Background extends Item {

    private String fontColor;

}
