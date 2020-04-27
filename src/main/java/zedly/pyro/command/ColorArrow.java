/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.pyro.command;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import static org.bukkit.Material.ARROW;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import zedly.pyro.Storage;

/**
 *
 * @author Dennis
 */
public class ColorArrow extends PlayerCommand {

    @Override
    boolean onCommand(Player player, String[] args) {
        if (args.length < 1) {
            sendUsage(player);
        } else {
            int[] color = new int[]{0, 0, 0};
            try {
                color[0] = Math.min(Math.abs(Integer.parseInt(args[0])), 255);
                color[1] = Math.min(Math.abs(Integer.parseInt(args[1])), 255);
                color[2] = Math.min(Math.abs(Integer.parseInt(args[2])), 255);
            } catch (Exception e) {
                if (ArrayUtils.contains(Storage.FW_COLOR_FRIENDLY_NAMES, args[0].toLowerCase())) {
                    color = Storage.FW_COLOR_RGB_RICH[ArrayUtils.indexOf(Storage.FW_COLOR_FRIENDLY_NAMES, args[0].toLowerCase())];
                } else {
                    sendUsage(player);
                    return true;
                }
            }
            if (player.getInventory().getItemInMainHand().getType().equals(ARROW)) {
                ItemStack stk = player.getInventory().getItemInMainHand();
                ItemMeta meta = stk.getItemMeta();
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GOLD + "Color Arrow");
                lore.add(ChatColor.RED + "R: " + color[0] + ChatColor.WHITE + ", " + ChatColor.GREEN + "G: " + color[1] + ChatColor.WHITE + ", " + ChatColor.BLUE + "B: " + color[2]);
                meta.setLore(lore);
                stk.setItemMeta(meta);
                player.sendMessage(Storage.logo + " Color arrow created!");
            } else {
                player.sendMessage(Storage.logo + " You need to be holding an arrow!");
            }
        }
        return true;
    }

    @Override
    public String getSyntax() {
        return " /color <red [0..255]> <green [0..255]> <blue [0..255]> | /color <color name> | " + Storage.colorString;
    }

    @Override
    public String getDescription() {
        return "Adds a colorful dust trail to your Arrows";
    }

}
