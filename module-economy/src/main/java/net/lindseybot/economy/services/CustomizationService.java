package net.lindseybot.economy.services;

import net.lindseybot.economy.repositories.sql.CustomizationRepository;
import net.lindseybot.shared.entities.profile.users.Customization;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class CustomizationService {

    private final CustomizationRepository customizations;

    public CustomizationService(CustomizationRepository customizations) {
        this.customizations = customizations;
    }

    public @NotNull Customization getCustomization(long user) {
        return this.customizations.findById(user)
                .orElse(new Customization(user));
    }

    public void save(Customization customization) {
        this.customizations.save(customization);
    }

}
