package me.verni.doubleteams.util;

import me.verni.doubleteams.member.MemberType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;

public class SkullUtil {

    public static ItemStack getOfflinePlayerSkull(OfflinePlayer player, MemberType memberType) {

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwningPlayer(player);

        if (memberType == MemberType.LEADER) {
            skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName()));

            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Status: &4*&cWłaściciel Działki"));
            lore.add(ChatColor.translateAlternateColorCodes('&', " "));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Założyciel działki, nie można go zdegradować!"));

            skullMeta.setLore(lore);
        }

        if (memberType == MemberType.VICELEADER) {
            skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName()));

            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Status: &cWłaściciel Działki"));
            lore.add(ChatColor.translateAlternateColorCodes('&', " "));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Kliknij aby zdegradować właściciela działki"));

            skullMeta.setLore(lore);
        }

        if (memberType == MemberType.MEMBER) {
            skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e" + player.getName()));

            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7ꜱᴛᴀᴛᴜꜱ: &eCzłonek"));
            lore.add(ChatColor.translateAlternateColorCodes('&', " "));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7ᴘᴜɴᴋᴛʏ: "));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7ᴄᴢᴌᴏɴᴇᴋ ᴏᴅ:"));
            lore.add(ChatColor.translateAlternateColorCodes('&', " "));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7&oKliknij aby wyrzucić gracza"));

            skullMeta.setLore(lore);
        }

        skull.setItemMeta(skullMeta);
        return skull;
    }
    public static ItemStack getPlayerSkull(Player player) {

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwningPlayer(player);

        skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e" + player.getName()));

        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7&oKliknij aby zaprosić do Teamu!"));

        skullMeta.setLore(lore);

        skull.setItemMeta(skullMeta);
        return skull;
    }
}
