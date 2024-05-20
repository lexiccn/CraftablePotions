package dev.lexiccn.craftablepotions;

import dev.lexiccn.craftablepotions.objects.PotionRecipe;
import dev.lexiccn.craftablepotions.listeners.PotionListener;
import dev.lexiccn.craftablepotions.settings.PotionSettings;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftablePotions extends JavaPlugin {
    private static CraftablePotions INSTANCE;

    public static CraftablePotions getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        saveDefaultConfig();
        PotionSettings.reloadConfig(this);
        PotionRecipe.registerRecipes(this);
        PotionListener.registerEvents(this);
    }
}
