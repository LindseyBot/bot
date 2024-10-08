package net.lindseybot.shared.entities.items;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "categories")
public class ItemCategory {

    @Id
    private Long id;
    private String name;
    private String description;
    private String emote;

    @ManyToMany(mappedBy = "categories")
    public List<Item> items = new ArrayList<>();

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ItemCategory category) {
            return Objects.equals(category.id, this.id);
        }
        return false;
    }

}
