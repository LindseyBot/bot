package net.lindseybot.shared.entities.discord;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FSelectMenu implements MessageComponent {

    private String id;
    private Label label;
    private int min = 1;
    private int max = 1;
    private boolean disabled;
    private List<FSelectOption> options = new ArrayList<>();

}
