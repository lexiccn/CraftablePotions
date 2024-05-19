package dev.lexiccn.craftablepotions;

import dev.lexiccn.craftablepotions.enums.PotionRecipe;
import dev.lexiccn.craftablepotions.listeners.PotionListener;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftablePotions extends JavaPlugin {
    @Override
    public void onEnable() {
        PotionRecipe.registerRecipes(this);
        PotionListener.registerEvents(this);
    }
}
