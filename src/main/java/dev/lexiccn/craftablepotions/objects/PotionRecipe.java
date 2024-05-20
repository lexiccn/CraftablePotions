package dev.lexiccn.craftablepotions.objects;

import dev.lexiccn.craftablepotions.CraftablePotions;
import dev.lexiccn.craftablepotions.settings.PotionSettings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.util.*;

public enum PotionRecipe {

    //Basic
    WATER("water", null, Material.AIR, PotionType.WATER, PotionType.THICK, PotionType.MUNDANE),
    AWKWARD("awkward", WATER, Material.NETHER_WART, PotionType.AWKWARD, null, null),

    //Effects
    WEAKNESS("weakness", WATER, Material.FERMENTED_SPIDER_EYE, PotionType.WEAKNESS, null, PotionType.LONG_WEAKNESS),
    HEALING("healing", AWKWARD, Material.GLISTERING_MELON_SLICE, PotionType.INSTANT_HEAL, PotionType.STRONG_HEALING, null),
    FIRE_RESISTANCE("fire_resistance", AWKWARD, Material.MAGMA_CREAM, PotionType.FIRE_RESISTANCE, null, PotionType.LONG_FIRE_RESISTANCE),
    REGENERATION("regeneration", AWKWARD, Material.GHAST_TEAR, PotionType.REGEN, PotionType.STRONG_REGENERATION, PotionType.LONG_REGENERATION),
    STRENGTH("strength", AWKWARD, Material.BLAZE_POWDER, PotionType.STRENGTH, PotionType.STRONG_STRENGTH, PotionType.LONG_STRENGTH),
    SWIFTNESS("swiftness", AWKWARD, Material.SUGAR, PotionType.SPEED, PotionType.STRONG_SWIFTNESS, PotionType.LONG_SWIFTNESS),
    NIGHT_VISION("night_vision", AWKWARD, Material.GOLDEN_CARROT, PotionType.NIGHT_VISION, null, PotionType.LONG_NIGHT_VISION),
    WATER_BREATHING("water_breathing", AWKWARD, Material.PUFFERFISH, PotionType.WATER_BREATHING, null, PotionType.LONG_WATER_BREATHING),
    LEAPING("leaping", AWKWARD, Material.RABBIT_FOOT, PotionType.JUMP, PotionType.STRONG_LEAPING, PotionType.LONG_LEAPING),
    SLOW_FALLING("slow_falling", AWKWARD, Material.PHANTOM_MEMBRANE, PotionType.SLOW_FALLING, null, PotionType.LONG_SLOW_FALLING),
    POISON("poison", AWKWARD, Material.SPIDER_EYE, PotionType.POISON, PotionType.STRONG_POISON, PotionType.LONG_POISON),
    TURTLE_MASTER("turtle_master", AWKWARD, Material.TURTLE_HELMET, PotionType.TURTLE_MASTER, PotionType.STRONG_TURTLE_MASTER, PotionType.LONG_TURTLE_MASTER),

    //Corrupt effects
    CORRUPT_NIGHT_VISION("night_vision_corrupt", NIGHT_VISION, Material.FERMENTED_SPIDER_EYE, PotionType.INVISIBILITY, null, PotionType.LONG_INVISIBILITY),
    CORRUPT_HEALING("healing_corrupt", HEALING, Material.FERMENTED_SPIDER_EYE, PotionType.INSTANT_DAMAGE, PotionType.STRONG_HARMING, null),
    CORRUPT_POISON("poison_corrupt", POISON, Material.FERMENTED_SPIDER_EYE, PotionType.INSTANT_DAMAGE, PotionType.STRONG_HARMING, null),
    CORRUPT_SWIFTNESS("swiftness_corrupt", SWIFTNESS, Material.FERMENTED_SPIDER_EYE, PotionType.SLOWNESS, PotionType.STRONG_SLOWNESS, PotionType.LONG_SLOWNESS),
    CORRUPT_LEAPING("leaping_corrupt", LEAPING, Material.FERMENTED_SPIDER_EYE, PotionType.SLOWNESS, PotionType.STRONG_SLOWNESS, PotionType.LONG_SLOWNESS),
    CORRUPT_STRENGTH("strength_corrupt", STRENGTH, Material.FERMENTED_SPIDER_EYE, PotionType.WEAKNESS, null, PotionType.LONG_WEAKNESS);


    private static final List<NamespacedKey> RECIPES = new ArrayList<>();
    private static final Map<NamespacedKey, String> GROUPS = new HashMap<>();
    private static final Map<String, List<NamespacedKey>> GROUPED_RECIPES = new HashMap<>();

    private final String id;
    private final PotionRecipe precursor;
    private final Material ingredient;
    private final PotionType type;
    private final PotionType upgraded;
    private final PotionType extended;

    public static void registerRecipes(CraftablePotions plugin) {
        for (PotionRecipe r : PotionRecipe.values()) {
            r.registerPotionRecipes(plugin);
        }
    }

    public static void unregisterRecipes() {
        RECIPES.forEach(Bukkit::removeRecipe);

        RECIPES.clear();
        GROUPS.clear();
        GROUPED_RECIPES.clear();
    }

    public static void reloadRecipes(CraftablePotions plugin) {
        unregisterRecipes();
        registerRecipes(plugin);
        Bukkit.updateRecipes();
    }

    public static List<NamespacedKey> getAllRecipes() {
        return Collections.unmodifiableList(RECIPES);
    }

    public static List<NamespacedKey> getGroupedRecipes(NamespacedKey key) {
        if (!GROUPS.containsKey(key)) return List.of();

        String group = GROUPS.get(key);

        if (!GROUPED_RECIPES.containsKey(group)) return List.of();

        return Collections.unmodifiableList(GROUPED_RECIPES.get(group));
    }

    PotionRecipe(String id, PotionRecipe precursor, Material ingredient, PotionType type, PotionType upgraded, PotionType extended) {
        this.id = id;
        this.precursor = precursor;
        this.ingredient = ingredient;
        this.type = type;
        this.upgraded = upgraded;
        this.extended = extended;
    }

    private ItemStack getItemStack(int amount, boolean upgrade, boolean extend, Material type) {
        ItemStack item = new ItemStack(type, amount);

        if (upgrade && extend) throw new IllegalArgumentException("Potion can not be both upgraded and extended");
        if (!(item.getItemMeta() instanceof PotionMeta meta)) throw new IllegalArgumentException("Material type does not have PotionMeta");

        if (upgrade) meta.setBasePotionType(this.upgraded);
        else if (extend) meta.setBasePotionType(this.extended);
        else meta.setBasePotionType(this.type);

        item.setItemMeta(meta);
        return item;
    }

    private void addRecipe(CraftingRecipe recipe) {
        RECIPES.add(recipe.getKey());
        GROUPS.put(recipe.getKey(), recipe.getGroup());
        if (!GROUPED_RECIPES.containsKey(recipe.getGroup())) GROUPED_RECIPES.put(recipe.getGroup(), new ArrayList<>());
        GROUPED_RECIPES.get(recipe.getGroup()).add(recipe.getKey());
        Bukkit.addRecipe(recipe);
    }

    private void createRecipe(CraftablePotions plugin, List<Material> ingredients, boolean upgrade, boolean extend, Material type) {
        ingredients = new ArrayList<>(ingredients);

        String prefix = this.id;
        String group = PotionSettings.groupRecipesByType() ? this.type.getKey().getKey() + "_" : "craftablepotions_";

        if (upgrade) {
            prefix += "upgraded_";
            group += (PotionSettings.groupRecipesByUpgradeType() ? "upgraded_" : "");
            ingredients.add(Material.GLOWSTONE_DUST);
        }
        else if (extend) {
            prefix += "extended_";
            group += (PotionSettings.groupRecipesByExtendType() ? "extended_" : "");
            ingredients.add(Material.REDSTONE);
        }
        else {
            group += (PotionSettings.groupRecipesByBaseType() ? "base_" : "");
        }

        switch (type) {
            case POTION: {
                if (!PotionSettings.isEnabledPotion(this.id, upgrade, extend)) return;
                group += (PotionSettings.groupRecipesByPotion()) ? "potion_" : "";
                break;
            }
            case SPLASH_POTION: {
                if (!PotionSettings.isEnabledSplash(this.id, upgrade, extend)) return;
                prefix+="splash_";
                group += (PotionSettings.groupRecipesBySplash()) ? "splash_" : "";
                ingredients.add(Material.GUNPOWDER);
                break;
            }
            case LINGERING_POTION: {
                if (!PotionSettings.isEnabledLingering(this.id, upgrade, extend)) return;
                prefix+="lingering_";
                group += (PotionSettings.groupRecipesByLingering()) ? "lingering_" : "";
                ingredients.add(Material.DRAGON_BREATH);
                break;
            }
            default: {
                return;
            }
        }

        if (PotionSettings.isEnabledQuantity(1)) {
            ingredients.add(0, Material.GLASS_BOTTLE);
            ShapelessRecipe singleBottle = new ShapelessRecipe(new NamespacedKey(plugin, prefix + this.id), this.getItemStack(1, upgrade, extend, type));
            singleBottle.setGroup(group);
            ingredients.forEach(singleBottle::addIngredient);
            addRecipe(singleBottle);
        }

        if (PotionSettings.isEnabledQuantity(2)) {
            ingredients.add(0, Material.GLASS_BOTTLE);
            ShapelessRecipe doubleBottle = new ShapelessRecipe(new NamespacedKey(plugin, prefix + "double_" + this.id), this.getItemStack(2, upgrade, extend, type));
            doubleBottle.setGroup(group);
            ingredients.forEach(doubleBottle::addIngredient);
            addRecipe(doubleBottle);
        }

        if (PotionSettings.isEnabledQuantity(3)) {
            ingredients.add(0, Material.GLASS_BOTTLE);
            ShapelessRecipe tripleBottle = new ShapelessRecipe(new NamespacedKey(plugin, prefix + "triple_" + this.id), this.getItemStack(3, upgrade, extend, type));
            tripleBottle.setGroup(group);
            ingredients.forEach(tripleBottle::addIngredient);
            addRecipe(tripleBottle);
        }
    }

    private void registerPotionRecipes(CraftablePotions plugin) {
        if (!PotionSettings.isEnabled(this.id)) return;

        boolean canUpgrade = this.type.isUpgradeable();
        boolean canExtend = this.type.isExtendable();

        List<Material> ingredients = new ArrayList<>();

        PotionRecipe potion = this;

        //If it has a precursor, add the ingredient to first index, otherwise ignore
        while (potion.precursor != null) {
            ingredients.add(0, potion.ingredient);
            potion = potion.precursor;
        }

        createRecipe(plugin, ingredients, false, false, Material.POTION);
        if (canUpgrade) createRecipe(plugin, ingredients, true, false, Material.POTION);
        if (canExtend) createRecipe(plugin, ingredients, false, true, Material.POTION);

        createRecipe(plugin, ingredients, false, false, Material.SPLASH_POTION);
        if (canUpgrade) createRecipe(plugin, ingredients, true, false, Material.SPLASH_POTION);
        if (canExtend) createRecipe(plugin, ingredients, false, true, Material.SPLASH_POTION);

        createRecipe(plugin, ingredients, false, false, Material.LINGERING_POTION);
        if (canUpgrade) createRecipe(plugin, ingredients, true, false, Material.LINGERING_POTION);
        if (canExtend) createRecipe(plugin, ingredients, false, true, Material.LINGERING_POTION);
    }
}
