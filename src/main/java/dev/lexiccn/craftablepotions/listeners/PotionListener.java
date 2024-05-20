package dev.lexiccn.craftablepotions.listeners;

import dev.lexiccn.craftablepotions.CraftablePotions;
import dev.lexiccn.craftablepotions.objects.PotionRecipe;
import dev.lexiccn.craftablepotions.settings.PotionSettings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.*;

public class PotionListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (PotionSettings.revealRecipesOnJoin()) {
            event.getPlayer().discoverRecipes(PotionRecipe.getAllRecipes());
        }
    }

    @EventHandler
    public void onPreparePotionCraft(PrepareItemCraftEvent event) {
        if (!PotionSettings.isEnabledDynamicQuantity()) return;

        CraftingInventory inventory = event.getInventory();
        if (inventory.getResult() == null) return;
        if (!inventory.getResult().hasItemMeta()) return;
        if (!(inventory.getResult().getItemMeta() instanceof PotionMeta)) return;

        //Check matrix for glass bottle, add to total
        int bottles = 0;
        for (ItemStack item : inventory.getMatrix()) {
            if (item == null) continue;
            if (item.getType() != Material.GLASS_BOTTLE) continue;

            bottles+=item.getAmount();
        }

        if (bottles >= 3) inventory.getResult().setAmount(3);
        else if (bottles == 2) inventory.getResult().setAmount(2);
    }

    @EventHandler
    public void onPotionCraft(CraftItemEvent event) {
        CraftingInventory inventory = event.getInventory();
        if (inventory.getResult() == null) return;

        ItemStack result = inventory.getResult();
        if (!result.hasItemMeta()) return;
        if (!(result.getItemMeta() instanceof PotionMeta)) return;

        int amount = result.getAmount();
        int numPotions = amount - 1;
        if (amount == 1) return;
        result.setAmount(1);

        HumanEntity clicker = event.getWhoClicked();

        List<ItemStack> bottles = new ArrayList<>();

        int minBottles = 0;
        int extraBottles = 0;

        for (ItemStack item : inventory.getMatrix()) {
            if (item == null) continue;
            if (item.getType() != Material.GLASS_BOTTLE) continue;

            if (minBottles == 0) minBottles = item.getAmount();

            if (item.getAmount() > minBottles) {
                extraBottles++;
            }

            if (item.getAmount() < minBottles) {
                minBottles = item.getAmount();
                extraBottles = bottles.size();
            }

            bottles.add(item);
        }

        if (bottles.isEmpty()) return;

        if (event.isShiftClick()) {
            int maxBottles = minBottles * bottles.size() + extraBottles;
            int numCrafts = maxBottles / amount;
            numPotions = maxBottles - (maxBottles % amount);

            int emptySlots = clicker.getInventory().getStorageContents().length;
            for (ItemStack item : clicker.getInventory().getStorageContents()) {
                if (item == null) continue;
                if (item.getType().isEmpty()) continue;
                emptySlots--;
            }

            //Number of crafts that can partially fit in inventory (all fully except maximum of one)
            numCrafts = Math.min((int) Math.ceil((double) emptySlots / amount), numCrafts);

            List<ItemStack> matrix = new ArrayList<>();

            for (ItemStack item : inventory.getMatrix()) {
                if (item == null) continue;

                if (item.getType() == Material.GLASS_BOTTLE) continue;

                numCrafts = Math.min(numCrafts, item.getAmount());

                matrix.add(item);
            }

            event.setResult(Event.Result.DENY);

            final int finalNumCrafts = numCrafts;
            matrix.forEach(itemStack -> itemStack.subtract(finalNumCrafts));

            if (event.getRecipe() instanceof CraftingRecipe recipe) clicker.discoverRecipe(recipe.getKey());

            //Refreshes result despite cancelled event
            inventory.setMatrix(inventory.getMatrix());
        }

        int numBottles = numPotions / bottles.size();
        int remainderBottles = numPotions % bottles.size();

        for (ItemStack bottle : bottles) {
            if (remainderBottles > 0 && bottle.getAmount() >= 1) {
                bottle.subtract();
                remainderBottles--;
            }
            bottle.subtract(numBottles);
        }

        ItemStack[] potions = new ItemStack[numPotions];

        for (int i = 0; i < numPotions; i++) {
            potions[i] = result.clone();
        }

        Bukkit.getScheduler().runTask(CraftablePotions.getInstance(), () -> {
            Map<Integer, ItemStack> droppedItems = clicker.getInventory().addItem(potions);
            droppedItems.values().forEach(itemStack -> clicker.getWorld().dropItem(clicker.getLocation(), itemStack));
        });
    }

    public static void registerEvents(CraftablePotions plugin) {
        Bukkit.getPluginManager().registerEvents(new PotionListener(), plugin);
    }
}
