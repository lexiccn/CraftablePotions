package dev.lexiccn.craftablepotions.settings;

import dev.lexiccn.craftablepotions.CraftablePotions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class PotionSettings {
    private static final String POTIONS = "potions.";
    private static final String POTION = ".potion";
    private static final String SPLASH = ".splash";
    private static final String LINGERING = ".lingering";
    private static FileConfiguration config;

    public static void registerConfig(CraftablePotions plugin) {
        config = plugin.getConfig();
    }

    public static boolean isEnabled(String id) {
        return config.getBoolean("potions." + id + ".enabled");
    }

    public static boolean isEnabledPotion(String id, boolean upgrade, boolean extend) {
        return isEnabledRecipe(id, upgrade, extend, POTION);
    }

    public static boolean isEnabledSplash(String id, boolean upgrade, boolean extend) {
        return isEnabledRecipe(id, upgrade, extend, SPLASH);
    }

    public static boolean isEnabledLingering(String id, boolean upgrade, boolean extend) {
        return isEnabledRecipe(id, upgrade, extend, LINGERING);
    }

    private static boolean isEnabledRecipe(String id, boolean upgrade, boolean extend, String lingering) {
        ConfigurationSection section = config.getConfigurationSection(POTIONS + id + lingering);
        if (section == null) return false;
        if (upgrade) return section.getBoolean("upgrade", false);
        if (extend) return section.getBoolean("extend", false);
        return section.getBoolean("base", false);
    }
}
