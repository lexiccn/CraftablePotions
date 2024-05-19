package dev.lexiccn.craftablepotions.enums;

import dev.lexiccn.craftablepotions.CraftablePotions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public enum PotionRecipe {

    //Basic
    WATER("water", null, Material.POTION, PotionType.WATER, PotionType.THICK, PotionType.MUNDANE),
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


    private final String id;
    private final PotionRecipe precursor;
    private final Material ingredient;
    private final PotionType type;
    private final PotionType upgraded;
    private final PotionType extended;

    PotionRecipe(String id, PotionRecipe precursor, Material ingredient, PotionType type, PotionType upgraded, PotionType extended) {
        this.id = id;
        this.precursor = precursor;
        this.ingredient = ingredient;
        this.type = type;
        this.upgraded = upgraded;
        this.extended = extended;
    }

    private ItemStack getBaseStack(int amount) {
        return getPotionItemStack(this.type, amount);
    }
    private ItemStack getUpgradedStack(int amount) {
        return getPotionItemStack(this.upgraded, amount);
    }
    private ItemStack getExtendedStack(int amount) {
        return getPotionItemStack(this.extended, amount);
    }

    private static ItemStack getPotionItemStack(@NotNull PotionType type, int amount) {
        ItemStack item = new ItemStack(Material.POTION, amount);
        if (!(item.getItemMeta() instanceof PotionMeta meta)) throw new IllegalArgumentException("Material.POTION does not have PotionMeta?");
        meta.setBasePotionType(type);
        item.setItemMeta(meta);
        return item;
    }

    private void registerPotionRecipes(CraftablePotions plugin) {
        //Don't add item if its recipe is itself
        if (this.precursor == null) return;

        List<Material> ingredients = new ArrayList<>();

        PotionRecipe potion = this;
        boolean upgrade = this.type.isUpgradeable();
        boolean extend = this.type.isExtendable();
        //Harming - Healing + Fermented
        //Healing - Awkward + Melon
        //Awkward - Water + Nether Wart
        //Water - Itself

        //If it has a precursor, add the ingredient
        //If it doesn't have a precursor, add itself
        while (potion.precursor != null) {
            //Creates a recipe for each precursor potion (i.e. healing_harming and awkward_harming and water_harming)
            ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, potion.precursor.id + "_" + this.id), this.getBaseStack(1));
            //Add ingredient from this potion to running list
            ingredients.add(potion.ingredient);
            //Add running list to this recipe
            ingredients.forEach(recipe::addIngredient);
            //Add the precursor potion itself
            recipe.addIngredient(potion.precursor.getBaseStack(1));
            //Register the recipe
            Bukkit.addRecipe(recipe);

            //Repeat for two potions, three potions
            recipe = new ShapelessRecipe(new NamespacedKey(plugin, "two_" + potion.precursor.id + "_" + this.id), this.getBaseStack(2));
            ingredients.forEach(recipe::addIngredient);
            recipe.addIngredient(potion.precursor.getBaseStack(1));
            recipe.addIngredient(potion.precursor.getBaseStack(1));
            Bukkit.addRecipe(recipe);

            recipe = new ShapelessRecipe(new NamespacedKey(plugin, "three_" + potion.precursor.id + "_" + this.id), this.getBaseStack(3));
            ingredients.forEach(recipe::addIngredient);
            recipe.addIngredient(potion.precursor.getBaseStack(1));
            recipe.addIngredient(potion.precursor.getBaseStack(1));
            recipe.addIngredient(potion.precursor.getBaseStack(1));
            Bukkit.addRecipe(recipe);

            if (upgrade) {
                //Create recipe of precursor basic + ingredients + glowstone = upgraded
                ShapelessRecipe upgradeRecipe = new ShapelessRecipe(new NamespacedKey(plugin, potion.precursor.type.getKey().getKey() + "_upgrade_" + this.id), this.getUpgradedStack(1));
                ingredients.forEach(upgradeRecipe::addIngredient);
                upgradeRecipe.addIngredient(Material.GLOWSTONE_DUST);
                upgradeRecipe.addIngredient(potion.precursor.getBaseStack(1));
                Bukkit.addRecipe(upgradeRecipe);

                //Repeat for two potions, three potions
                upgradeRecipe = new ShapelessRecipe(new NamespacedKey(plugin, "two_" + potion.precursor.type.getKey().getKey() + "_upgrade_" + this.id), this.getUpgradedStack(2));
                ingredients.forEach(upgradeRecipe::addIngredient);
                upgradeRecipe.addIngredient(Material.GLOWSTONE_DUST);
                upgradeRecipe.addIngredient(potion.precursor.getBaseStack(1));
                upgradeRecipe.addIngredient(potion.precursor.getBaseStack(1));
                Bukkit.addRecipe(upgradeRecipe);

                upgradeRecipe = new ShapelessRecipe(new NamespacedKey(plugin, "three_" + potion.precursor.type.getKey().getKey() + "_upgrade_" + this.id), this.getUpgradedStack(3));
                ingredients.forEach(upgradeRecipe::addIngredient);
                upgradeRecipe.addIngredient(Material.GLOWSTONE_DUST);
                upgradeRecipe.addIngredient(potion.precursor.getBaseStack(1));
                upgradeRecipe.addIngredient(potion.precursor.getBaseStack(1));
                upgradeRecipe.addIngredient(potion.precursor.getBaseStack(1));
                Bukkit.addRecipe(upgradeRecipe);

                if (potion.precursor.type.isUpgradeable()) {
                    //Create recipe of precursor upgraded + ingredients = upgraded
                    ShapelessRecipe upgradedRecipe = new ShapelessRecipe(new NamespacedKey(plugin, potion.precursor.upgraded.getKey().getKey() + "_upgraded_" + this.id), this.getUpgradedStack(1));
                    ingredients.forEach(upgradedRecipe::addIngredient);
                    upgradedRecipe.addIngredient(potion.precursor.getUpgradedStack(1));
                    Bukkit.addRecipe(upgradedRecipe);

                    //Repeat for two potions, three potions
                    upgradedRecipe = new ShapelessRecipe(new NamespacedKey(plugin, "two_" + potion.precursor.upgraded.getKey().getKey() + "_upgraded_" + this.id), this.getUpgradedStack(2));
                    ingredients.forEach(upgradedRecipe::addIngredient);
                    upgradedRecipe.addIngredient(potion.precursor.getUpgradedStack(1));
                    upgradedRecipe.addIngredient(potion.precursor.getUpgradedStack(1));
                    Bukkit.addRecipe(upgradedRecipe);

                    upgradedRecipe = new ShapelessRecipe(new NamespacedKey(plugin, "three_" + potion.precursor.upgraded.getKey().getKey() + "_upgraded_" + this.id), this.getUpgradedStack(3));
                    ingredients.forEach(upgradedRecipe::addIngredient);
                    upgradedRecipe.addIngredient(potion.precursor.getUpgradedStack(1));
                    upgradedRecipe.addIngredient(potion.precursor.getUpgradedStack(1));
                    upgradedRecipe.addIngredient(potion.precursor.getUpgradedStack(1));
                    Bukkit.addRecipe(upgradedRecipe);
                }
            }

            if (extend) {
                //Create recipe of precursor basic + ingredients + redstone = extended
                ShapelessRecipe extendRecipe = new ShapelessRecipe(new NamespacedKey(plugin, potion.precursor.type.getKey().getKey() + "_extend_" + this.id), this.getExtendedStack(1));
                ingredients.forEach(extendRecipe::addIngredient);
                extendRecipe.addIngredient(Material.REDSTONE);
                extendRecipe.addIngredient(potion.precursor.getBaseStack(1));
                Bukkit.addRecipe(extendRecipe);

                //Repeat for two potions, three potions
                extendRecipe = new ShapelessRecipe(new NamespacedKey(plugin, "two_" + potion.precursor.type.getKey().getKey() + "_extend_" + this.id), this.getExtendedStack(2));
                ingredients.forEach(extendRecipe::addIngredient);
                extendRecipe.addIngredient(Material.REDSTONE);
                extendRecipe.addIngredient(potion.precursor.getBaseStack(1));
                extendRecipe.addIngredient(potion.precursor.getBaseStack(1));
                Bukkit.addRecipe(extendRecipe);

                extendRecipe = new ShapelessRecipe(new NamespacedKey(plugin, "three_" + potion.precursor.type.getKey().getKey() + "_extend_" + this.id), this.getExtendedStack(3));
                ingredients.forEach(extendRecipe::addIngredient);
                extendRecipe.addIngredient(Material.REDSTONE);
                extendRecipe.addIngredient(potion.precursor.getBaseStack(1));
                extendRecipe.addIngredient(potion.precursor.getBaseStack(1));
                extendRecipe.addIngredient(potion.precursor.getBaseStack(1));
                Bukkit.addRecipe(extendRecipe);

                if (potion.precursor.type.isExtendable()) {
                    //Create recipe of precursor extended + ingredients = extended
                    ShapelessRecipe extendedRecipe = new ShapelessRecipe(new NamespacedKey(plugin, potion.precursor.extended.getKey().getKey() + "_extended_" + this.id), this.getBaseStack(1));
                    ingredients.forEach(extendedRecipe::addIngredient);
                    extendedRecipe.addIngredient(potion.precursor.getExtendedStack(1));
                    Bukkit.addRecipe(extendedRecipe);

                    //Repeat for two potions, three potions
                    extendedRecipe = new ShapelessRecipe(new NamespacedKey(plugin, "two_" + potion.precursor.extended.getKey().getKey() + "_extended_" + this.id), this.getBaseStack(2));
                    ingredients.forEach(extendedRecipe::addIngredient);
                    extendedRecipe.addIngredient(potion.precursor.getExtendedStack(1));
                    extendedRecipe.addIngredient(potion.precursor.getExtendedStack(1));
                    Bukkit.addRecipe(extendedRecipe);

                    extendedRecipe = new ShapelessRecipe(new NamespacedKey(plugin, "three_" + potion.precursor.extended.getKey().getKey() + "_extended_" + this.id), this.getBaseStack(3));
                    ingredients.forEach(extendedRecipe::addIngredient);
                    extendedRecipe.addIngredient(potion.precursor.getExtendedStack(1));
                    extendedRecipe.addIngredient(potion.precursor.getExtendedStack(1));
                    extendedRecipe.addIngredient(potion.precursor.getExtendedStack(1));
                    Bukkit.addRecipe(extendedRecipe);
                }
            }

            //Move back to precursor
            potion = potion.precursor;
        }
    }

    public static void registerRecipes(CraftablePotions plugin) {
        for (PotionRecipe r : PotionRecipe.values()) {
            r.registerPotionRecipes(plugin);
        }
    }
}
