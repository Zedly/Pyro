/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.pyro.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import zedly.pyro.Storage;

/**
 *
 * @author Dennis
 */
public class EEgg extends PlayerCommand {

    @Override
    boolean onCommand(Player player, String[] args) {
        ItemStack items = player.getInventory().getItemInMainHand();
        if (items == null || items.getType() == Material.AIR) {
            player.sendMessage(Storage.logo + " You must have something in your hand!");
            return true;
        }
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        ItemStack disguise;
        if (Storage.pyro.getConfig().getBoolean("xmas-instead-of-eggs", false)) {
            disguise = new ItemStack(Material.CHEST);
            ItemMeta meta = disguise.getItemMeta();
            meta.setDisplayName(ChatColor.MAGIC + "Transient");
            disguise.setItemMeta(meta);
        } else {
            disguise = new ItemStack(Storage.EASTER_EGG_TYPES[Storage.rnd.nextInt(Storage.EASTER_EGG_TYPES.length)]);
            ItemMeta meta = disguise.getItemMeta();
            meta.setDisplayName(ChatColor.MAGIC + "Transient");
            disguise.setItemMeta(meta);
        }
        Item ent = (Item) player.getWorld().dropItem(player.getLocation().add(new Vector(0, 1, 0)), disguise);
        ent.setVelocity(player.getLocation().getDirection().multiply(0.5));
        Storage.eastereggs.put(ent, items);
        ent.setPickupDelay(128);
        return true;
    }

    @Override
    public String getSyntax() {
        return "/eegg";
    }

    @Override
    public String getDescription() {
        return "Drops the item you are holding disguised as a monster egg or a chest";
    }

}
