package dev.lexiccn.craftablepotions.listeners;

import dev.lexiccn.craftablepotions.CraftablePotions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.util.*;

public class PotionListener implements Listener {
    private static final Map<Material, Material> FORMATS = new HashMap<>();
    private static final Map<Material, PotionType> INGREDIENTS = new HashMap<>();
    private static final Map<PotionType, PotionType> UPGRADED = new HashMap<>();
    private static final Map<PotionType, PotionType> EXTENDED = new HashMap<>();
    private static final Map<PotionType, PotionType> CORRUPTED = new HashMap<>();


    private static final Material POTION_MATERIAL = Material.POTION;
    private static final Material EFFECT_MATERIAL = Material.NETHER_WART;
    private static final Material UPGRADE_MATERIAL = Material.GLOWSTONE_DUST;
    private static final Material EXTEND_MATERIAL = Material.REDSTONE;
    private static final Material CORRUPT_MATERIAL = Material.FERMENTED_SPIDER_EYE;

    static {
        FORMATS.put(Material.GUNPOWDER, Material.SPLASH_POTION);
        FORMATS.put(Material.DRAGON_BREATH, Material.LINGERING_POTION);

        INGREDIENTS.put(Material.SUGAR, PotionType.SPEED);
        INGREDIENTS.put(Material.RABBIT_FOOT, PotionType.JUMP);
        INGREDIENTS.put(Material.GLISTERING_MELON_SLICE, PotionType.INSTANT_HEAL);
        INGREDIENTS.put(Material.SPIDER_EYE, PotionType.POISON);
        INGREDIENTS.put(Material.PUFFERFISH, PotionType.WATER_BREATHING);
        INGREDIENTS.put(Material.MAGMA_CREAM, PotionType.FIRE_RESISTANCE);
        INGREDIENTS.put(Material.GOLDEN_CARROT, PotionType.NIGHT_VISION);
        INGREDIENTS.put(Material.BLAZE_POWDER, PotionType.STRENGTH);
        INGREDIENTS.put(Material.GHAST_TEAR, PotionType.REGEN);
        INGREDIENTS.put(Material.TURTLE_HELMET, PotionType.TURTLE_MASTER);
        INGREDIENTS.put(Material.PHANTOM_MEMBRANE, PotionType.SLOW_FALLING);

        UPGRADED.put(PotionType.WATER, PotionType.THICK);
        UPGRADED.put(PotionType.JUMP, PotionType.STRONG_LEAPING);
        UPGRADED.put(PotionType.INSTANT_HEAL, PotionType.STRONG_HEALING);
        UPGRADED.put(PotionType.POISON, PotionType.STRONG_POISON);
        UPGRADED.put(PotionType.STRENGTH, PotionType.STRONG_STRENGTH);
        UPGRADED.put(PotionType.REGEN, PotionType.STRONG_REGENERATION);
        UPGRADED.put(PotionType.TURTLE_MASTER, PotionType.STRONG_TURTLE_MASTER);
        UPGRADED.put(PotionType.INSTANT_DAMAGE, PotionType.STRONG_HARMING);
        UPGRADED.put(PotionType.SLOWNESS, PotionType.STRONG_SLOWNESS);

        EXTENDED.put(PotionType.WATER, PotionType.MUNDANE);
        EXTENDED.put(PotionType.SPEED, PotionType.LONG_SWIFTNESS);
        EXTENDED.put(PotionType.JUMP, PotionType.LONG_LEAPING);
        EXTENDED.put(PotionType.POISON, PotionType.LONG_POISON);
        EXTENDED.put(PotionType.WATER_BREATHING, PotionType.LONG_WATER_BREATHING);
        EXTENDED.put(PotionType.FIRE_RESISTANCE, PotionType.LONG_FIRE_RESISTANCE);
        EXTENDED.put(PotionType.NIGHT_VISION, PotionType.LONG_NIGHT_VISION);
        EXTENDED.put(PotionType.STRENGTH, PotionType.LONG_STRENGTH);
        EXTENDED.put(PotionType.REGEN, PotionType.LONG_REGENERATION);
        EXTENDED.put(PotionType.TURTLE_MASTER, PotionType.LONG_TURTLE_MASTER);
        EXTENDED.put(PotionType.SLOW_FALLING, PotionType.LONG_SLOW_FALLING);
        EXTENDED.put(PotionType.INVISIBILITY, PotionType.LONG_INVISIBILITY);
        EXTENDED.put(PotionType.SLOWNESS, PotionType.LONG_SLOWNESS);
        EXTENDED.put(PotionType.WEAKNESS, PotionType.LONG_WEAKNESS);

        CORRUPTED.put(PotionType.WATER, PotionType.WEAKNESS);
        CORRUPTED.put(PotionType.SPEED, PotionType.SLOWNESS);
        CORRUPTED.put(PotionType.JUMP, PotionType.SLOWNESS);
        CORRUPTED.put(PotionType.INSTANT_HEAL, PotionType.INSTANT_DAMAGE);
        CORRUPTED.put(PotionType.POISON, PotionType.INSTANT_DAMAGE);
        CORRUPTED.put(PotionType.NIGHT_VISION, PotionType.INVISIBILITY);
        CORRUPTED.put(PotionType.STRENGTH, PotionType.WEAKNESS);
    }

    @EventHandler
    public void onPotionCraft(PrepareItemCraftEvent event) {
        if (event.getInventory().getRecipe() != null) return;
        if (event.getInventory().getResult() != null) return;

        List<Integer> potions = new ArrayList<>();
        ItemStack ingredient = null;
        ItemStack corrupt = null;
        ItemStack upgrade = null;
        ItemStack extend = null;
        ItemStack effect = null;
        ItemStack format = null;

        ItemStack[] matrix = event.getInventory().getMatrix().clone();
        for (int i=0; i<matrix.length; i++) {
            ItemStack item = matrix[i];
            if (item == null) continue;

            Material type = item.getType();
            if (type.isAir()) continue;

            if (potions.size() <= 3 && type == POTION_MATERIAL) {
                potions.add(i);
                continue;
            }

            if (effect == null && type == EFFECT_MATERIAL) {
                effect = item.clone();
                continue;
            }

            if (corrupt == null && type == CORRUPT_MATERIAL) {
                corrupt = item.clone();
                continue;
            }

            if (upgrade == null && type == UPGRADE_MATERIAL) {
                upgrade = item.clone();
                continue;
            }

            if (extend == null && type == EXTEND_MATERIAL) {
                extend = item.clone();
                continue;
            }

            if (format == null && FORMATS.containsKey(type)) {
                format = item.clone();
                continue;
            }

            if (ingredient == null && INGREDIENTS.containsKey(type)) {
                ingredient = item.clone();
                continue;
            }

            //Failed to match an item - no result will be made
            return;
        }

        if (upgrade != null && extend != null) return;

        boolean formatted = false;
        boolean effected = false;
        boolean ingrediented = false;
        boolean corrupted = false;
        boolean upgraded = false;
        boolean extended = false;

        for (Integer slot : potions) {
            ItemStack potion = matrix[slot].clone();

            if (!(potion.getItemMeta() instanceof PotionMeta meta)) return;
            PotionType type = meta.getBasePotionType();

            if (format != null) {
                formatted = true;
                potion = new ItemStack(FORMATS.get(format.getType()), potion.getAmount());
            }

            //Effect turns WATER to AWKWARD
            if (effect != null && type == PotionType.WATER) {
                //Potion is now an awkward potion
                effected = true;
                type = PotionType.AWKWARD;
            }

            //Ingredient turns AWKWARD to EFFECT
            if (ingredient != null && type == PotionType.AWKWARD) {
                //Potion is now an effect potion
                ingrediented = true;
                type = INGREDIENTS.get(ingredient.getType());
            }

            //Corrupt turns most to EFFECT
            //Must be before upgrade and extend
            if (corrupt != null && CORRUPTED.containsKey(type)) {
                //Potion is now corrupted potion
                corrupted = true;
                type = CORRUPTED.get(type);
            }

            //Upgrade turns most to UPGRADE
            if (upgrade != null && UPGRADED.containsKey(type)) {
                //Potion is now upgraded potion
                upgraded = true;
                type = UPGRADED.get(type);
            }

            //Extend turns most to EXTEND
            if (extend != null && EXTENDED.containsKey(type)) {
                //Potion is now extended potion
                extended = true;
                type = EXTENDED.get(type);
            }

            meta.setBasePotionType(type);

            potion.setItemMeta(meta);

            matrix[slot] = potion;
        }

        /*if (formatted) format.subtract();
        if (effected) effect.subtract();
        if (ingrediented) ingredient.subtract();
        if (corrupted) corrupt.subtract();
        if (upgraded) upgrade.subtract();
        if (extended) extend.subtract();*/
        
        event.getInventory().setMatrix(matrix);
    }

    public static void registerEvents(CraftablePotions plugin) {
        Bukkit.getPluginManager().registerEvents(new PotionListener(), plugin);
    }
}
