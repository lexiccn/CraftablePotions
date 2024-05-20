package dev.lexiccn.craftablepotions.settings;

import dev.lexiccn.craftablepotions.CraftablePotions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class PotionSettings {
    private static final String POTIONS_SETTINGS = "potions.";
    private static final String RECIPES_SETTINGS = "recipes.";
    private static final String TYPE = ".type";
    private static final String POTION = ".potion";
    private static final String SPLASH = ".splash";
    private static final String LINGERING = ".lingering";
    private static final String BASE = ".base";
    private static final String UPGRADE = ".upgrade";
    private static final String EXTEND = ".extend";
    private static FileConfiguration config;

    public static void reloadConfig(CraftablePotions plugin) {
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
        ConfigurationSection section = config.getConfigurationSection(POTIONS_SETTINGS + id + lingering);
        if (section == null) return false;
        if (upgrade) return section.getBoolean("upgrade", false);
        if (extend) return section.getBoolean("extend", false);
        return section.getBoolean("base", false);
    }

    public static boolean isEnabledQuantity(int quantity) {
        return config.getBoolean(RECIPES_SETTINGS + "quantity." + quantity, false);
    }

    public static boolean isEnabledDynamicQuantity() {
        return config.getBoolean(RECIPES_SETTINGS + "quantity.dynamic", false);
    }

    public static boolean revealRecipesOnJoin() {
        return config.getBoolean(RECIPES_SETTINGS + "reveal.join", false);
    }

    public static boolean revealRecipeGroups() {
        return config.getBoolean(RECIPES_SETTINGS + "reveal.group", false);
    }

    public static boolean groupRecipesByType() {
        return config.getBoolean(RECIPES_SETTINGS + "group" + TYPE, false);
    }

    public static boolean groupRecipesByPotion() {
        return config.getBoolean(RECIPES_SETTINGS + "group" + POTION, false);
    }

    public static boolean groupRecipesBySplash() {
        return config.getBoolean(RECIPES_SETTINGS + "group" + SPLASH, false);
    }

    public static boolean groupRecipesByLingering() {
        return config.getBoolean(RECIPES_SETTINGS + "group" + LINGERING, false);
    }

    public static boolean groupRecipesByBaseType() {
        return config.getBoolean(RECIPES_SETTINGS + "group" + BASE, false);
    }

    public static boolean groupRecipesByUpgradeType() {
        return config.getBoolean(RECIPES_SETTINGS + "group" + UPGRADE, false);
    }

    public static boolean groupRecipesByExtendType() {
        return config.getBoolean(RECIPES_SETTINGS + "group" + EXTEND, false);
    }
}
