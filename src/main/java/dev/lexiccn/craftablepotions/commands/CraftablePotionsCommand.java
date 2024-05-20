package dev.lexiccn.craftablepotions.commands;

import dev.lexiccn.craftablepotions.CraftablePotions;
import dev.lexiccn.craftablepotions.objects.PotionRecipe;
import dev.lexiccn.craftablepotions.settings.PotionSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CraftablePotionsCommand implements CommandExecutor {
    private static final Component PREFIX = Component.text("[").color(NamedTextColor.DARK_PURPLE)
            .append(Component.text("CraftablePotions").color(NamedTextColor.LIGHT_PURPLE))
            .append(Component.text("] ").color(NamedTextColor.DARK_PURPLE));

    private static final TextColor COLOR = NamedTextColor.LIGHT_PURPLE;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) return handleHelp(sender);
        return switch (args[0]) {
            case "reload" -> handleReload(sender);
            default -> handleHelp(sender);
        };
    }

    private void sendHelpMessage(CommandSender sender) {
        sendFormattedMessage(sender, "Usage: /craftablepotions <reload>");
    }

    private void sendNoPermissionsMessage(CommandSender sender) {
        sendFormattedMessage(sender, "You do not have the required permissions for this command.", NamedTextColor.RED);
    }

    private void sendFormattedMessage(CommandSender sender, String msg) {
        sendFormattedMessage(sender, msg, null);
    }

    private void sendFormattedMessage(CommandSender sender, String msg, TextColor override) {
        TextColor color = (override != null) ? override : COLOR;

        Component message = PREFIX.append(Component.text(msg).color(color));

        sender.sendMessage(message);
    }

    private boolean handleHelp(CommandSender sender) {
        sendHelpMessage(sender);
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("dev.lexiccn.craftablepotions.command.reload")) {
            sendNoPermissionsMessage(sender);
            return true;
        }

        PotionRecipe.reloadRecipes(CraftablePotions.getInstance());
        PotionSettings.reloadConfig(CraftablePotions.getInstance());

        sendFormattedMessage(sender, "Configuration file successfully reloaded.");

        return true;
    }
}
