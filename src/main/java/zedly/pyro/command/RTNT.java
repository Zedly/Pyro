/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.pyro.command;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.ChatColor;
import static org.bukkit.Material.NAME_TAG;
import static org.bukkit.Material.OAK_SIGN;
import static org.bukkit.Material.TNT;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import zedly.pyro.Storage;

/**
 *
 * @author Dennis
 */
public class RTNT extends PlayerCommand {

    @Override
    boolean onCommand(Player player, String[] args) {
        if (args.length < 2) {
            sendUsage(player);
            return true;
        }

        ItemStack is = player.getInventory().getItemInMainHand();
        if (is.getType() != TNT && is.getType() != NAME_TAG && is.getType() != OAK_SIGN) {
            player.sendMessage(Storage.logo + " You need to be holding TNT, a Name Tag, or a Sign!");
            return true;
        }
        ItemMeta meta = is.getItemMeta();
        List<String> lore;
        if (meta.hasLore()) {
            lore = meta.getLore();
            if (meta.getLore().get(0).contains(ChatColor.GREEN + "Remote")) {
                lore.remove(0);
            }
        } else {
            lore = new LinkedList<>();
        }
        if (args[0].equalsIgnoreCase("add") && !lore.contains(ChatColor.GOLD + args[1])) {
            if (is.getType() == OAK_SIGN && lore.size() > 2) {
                lore.remove(2);
            }
            lore.add(0, ChatColor.GOLD + args[1]);
            player.sendMessage(Storage.logo + " Added remote channel!");
        } else if (args[0].equalsIgnoreCase("del") && lore.contains(ChatColor.GOLD + args[1])) {
            lore.remove(ChatColor.GOLD + args[1]);
            player.sendMessage(Storage.logo + " Removed remote channel!");
        }
        switch (is.getType()) {
            case TNT:
                lore.add(0, ChatColor.GREEN + "Remote Controlled");
                break;
            case NAME_TAG:
                lore.add(0, ChatColor.GREEN + "Remote Detonator");
                break;
            case OAK_SIGN:
                lore.add(0, ChatColor.GREEN + "Remote Detonate Sign");
                break;
            default:
                break;
        }
        if (lore.size() > 1) {
            meta.setLore(lore);
        } else {
            meta.setLore(new LinkedList<String>());
        }
        is.setItemMeta(meta);
        return true;
    }

    @Override
    public String getSyntax() {
        return "/remote <add/del> <channel name>";
    }

    @Override
    public String getDescription() {
        return "Creates remote triggers for TNT. Use on both TNT and a Name Tag to pair them";
    }

}
