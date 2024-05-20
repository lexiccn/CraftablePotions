package dev.lexiccn.craftablepotions.listeners;

import dev.lexiccn.craftablepotions.CraftablePotions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.ArrayList;
import java.util.List;

public class PotionListener implements Listener {

    @EventHandler
    public void onPreparePotionCraft(PrepareItemCraftEvent event) {
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
        if (amount == 1) return;
        result.setAmount(1);

        if (event.isShiftClick() && amount == 3) {
            List<ItemStack> bottles = new ArrayList<>();
            int smallestBottlesAmount = 0;

            for (ItemStack item : inventory.getMatrix()) {
                if (item == null) continue;
                if (item.getType() != Material.GLASS_BOTTLE) continue;

                if (smallestBottlesAmount == 0) smallestBottlesAmount = item.getAmount();
                else if (smallestBottlesAmount < item.getAmount()) smallestBottlesAmount = item.getAmount();

                bottles.add(item);
            }

            int emptySlots = event.getWhoClicked().getInventory().getStorageContents().length;
            for (ItemStack item : event.getWhoClicked().getInventory().getStorageContents()) {
                if (item == null) continue;
                if (item.getType().isEmpty()) continue;
                emptySlots--;
            }

            //Number of crafts that can partially fit in inventory (all fully except maximum of one)
            int craftAmount = emptySlots / 3;
            if (emptySlots % 3 > 0) craftAmount++;

            //Minimum number of full crafts possible without depleting a stack
            craftAmount = Math.min((smallestBottlesAmount * bottles.size()) / 3, craftAmount);

            List<ItemStack> matrix = new ArrayList<>();

            for (ItemStack item : inventory.getMatrix()) {
                if (item == null) continue;
                if (item.getType() == Material.GLASS_BOTTLE) continue;
                craftAmount = Math.min(item.getAmount(), craftAmount);
                matrix.add(item);
            }

            event.setResult(Event.Result.DENY);

            //Remove one ingredient per craft
            int finalCraftAmount = craftAmount;
            int totalPotions = finalCraftAmount * 3;

            //Add directly to inventory
            for (int i = 0; i < totalPotions; i++) {
                event.getWhoClicked().getInventory().addItem(result.clone());
            }

            matrix.forEach(itemStack -> itemStack.subtract(finalCraftAmount));

            int bottlesPerStack = totalPotions / bottles.size();
            int remainderBottles = totalPotions % bottles.size();
            for (ItemStack bottle : bottles) {
                bottle.subtract(bottlesPerStack);
                if (remainderBottles > 0 && bottle.getAmount() >= remainderBottles) {
                    bottle.subtract(remainderBottles);
                    remainderBottles = 0;
                }
            }


            //Refreshes the result
            Bukkit.getScheduler().runTask(CraftablePotions.getInstance(), () -> {
                inventory.setMatrix(inventory.getMatrix());
            });

            //Discovers the recipe if player does not have it
            if (!(event.getRecipe() instanceof CraftingRecipe recipe)) return;
            if (event.getWhoClicked().hasDiscoveredRecipe(recipe.getKey())) return;
            event.getWhoClicked().discoverRecipe(recipe.getKey());

            return;
        }

        List<ItemStack> bottles = new ArrayList<>();

        for (ItemStack item : inventory.getMatrix()) {
            if (item == null) continue;
            if (item.getType() != Material.GLASS_BOTTLE) continue;

            bottles.add(item);
        }

        int remainder = amount % bottles.size();
        for (ItemStack bottle : bottles) {
            if (bottle.getAmount() >= remainder) {
                bottle.subtract(remainder);
                break;
            }
        }

        //TODO: one tick later to prevent replacing the actual result (e.g by pressing 1)
        for (int i = 1; i < amount; i++) {
            event.getWhoClicked().getInventory().addItem(result.clone());
        }
    }

    public static void registerEvents(CraftablePotions plugin) {
        Bukkit.getPluginManager().registerEvents(new PotionListener(), plugin);
    }
}
