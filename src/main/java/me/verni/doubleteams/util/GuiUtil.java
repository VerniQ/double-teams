package me.verni.doubleteams.util;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GuiUtil {

    public static ItemStack itemStackBuilder(Material material, String name, List<String> lore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static Component textComponent(String text, NamedTextColor color, TextDecoration decoration) {
        return Component.text().content(text).color(color).decoration(decoration, true)
                .decoration(TextDecoration.ITALIC, false).build();
    }


    public static void addButtons(PaginatedGui gui){
        gui.setItem(22, ItemBuilder.from(Material.PAPER)
                .name(textComponent("Strona: ", NamedTextColor.YELLOW, TextDecoration.BOLD)
                        .append(textComponent("" + gui.getCurrentPageNum(), NamedTextColor.RED, TextDecoration.BOLD)))
                .asGuiItem());

        gui.setItem(23, ItemBuilder.from(Material.PAPER)
                .name(textComponent("Następna Strona", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .asGuiItem(event -> {
                    gui.next();
                    gui.updateItem(22, ItemBuilder.from(Material.PAPER)
                            .name(textComponent("Strona: ", NamedTextColor.YELLOW, TextDecoration.BOLD)
                                    .append(textComponent("" + gui.getCurrentPageNum(), NamedTextColor.RED, TextDecoration.BOLD)))
                            .asGuiItem());

                }));
        gui.setItem(21, ItemBuilder.from(Material.PAPER)
                .name(textComponent("Poprzednia Strona", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .asGuiItem(event -> {
                    gui.previous();
                    gui.updateItem(22, ItemBuilder.from(Material.PAPER)
                            .name(textComponent("Strona: ", NamedTextColor.YELLOW, TextDecoration.BOLD)
                                    .append(textComponent("" + gui.getCurrentPageNum(), NamedTextColor.RED, TextDecoration.BOLD)))
                            .asGuiItem());
                }));
    }
}
