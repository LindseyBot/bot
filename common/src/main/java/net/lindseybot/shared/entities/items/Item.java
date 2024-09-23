package net.lindseybot.shared.entities.items;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "items")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Item {

    @Id
    private Long id;
    private String name;
    private String description;
    private Double price;

    @Enumerated(EnumType.STRING)
    private ItemType type;

    @ManyToMany
    @JoinTable(name = "items_categories",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<ItemCategory> categories = new ArrayList<>();

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Item item) {
            return Objects.equals(item.id, this.id);
        }
        return false;
    }

}
