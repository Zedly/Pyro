/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.pyro.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import static org.bukkit.Material.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import zedly.pyro.features.EasterEggs;
import zedly.pyro.Storage;

/**
 *
 * @author Dennis
 */
public class EEgg extends PlayerCommand {

    public static final Material[] EASTER_EGG_TYPES = {
        ELDER_GUARDIAN_SPAWN_EGG, WITHER_SKELETON_SPAWN_EGG, STRAY_SPAWN_EGG, HUSK_SPAWN_EGG,
        ZOMBIE_VILLAGER_SPAWN_EGG, SKELETON_HORSE_SPAWN_EGG, ZOMBIE_HORSE_SPAWN_EGG,
        DONKEY_SPAWN_EGG, MULE_SPAWN_EGG, EVOKER_SPAWN_EGG, VEX_SPAWN_EGG,
        VINDICATOR_SPAWN_EGG, CREEPER_SPAWN_EGG, SKELETON_SPAWN_EGG, SPIDER_SPAWN_EGG,
        ZOMBIE_SPAWN_EGG, SLIME_SPAWN_EGG, GHAST_SPAWN_EGG, ZOMBIE_PIGMAN_SPAWN_EGG,
        ENDERMAN_SPAWN_EGG, CAVE_SPIDER_SPAWN_EGG, SILVERFISH_SPAWN_EGG,
        BLAZE_SPAWN_EGG, MAGMA_CUBE_SPAWN_EGG, WITHER_SKELETON_SPAWN_EGG, BAT_SPAWN_EGG,
        WITCH_SPAWN_EGG, ENDERMITE_SPAWN_EGG, GUARDIAN_SPAWN_EGG, SHULKER_SPAWN_EGG,
        PIG_SPAWN_EGG, SHEEP_SPAWN_EGG, COW_SPAWN_EGG, CHICKEN_SPAWN_EGG,
        SQUID_SPAWN_EGG, WOLF_SPAWN_EGG, MOOSHROOM_SPAWN_EGG, OCELOT_SPAWN_EGG, HORSE_SPAWN_EGG,
        RABBIT_SPAWN_EGG, POLAR_BEAR_SPAWN_EGG, LLAMA_SPAWN_EGG, VILLAGER_SPAWN_EGG
    };

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
            disguise = new ItemStack(EASTER_EGG_TYPES[Storage.rnd.nextInt(EASTER_EGG_TYPES.length)]);
            ItemMeta meta = disguise.getItemMeta();
            meta.setDisplayName(EasterEggs.TRANSIENT_NAME);
            disguise.setItemMeta(meta);
        }
        Item ent = (Item) player.getWorld().dropItem(player.getLocation().add(new Vector(0, 1, 0)), disguise);
        ent.setVelocity(player.getLocation().getDirection().multiply(0.5));
        EasterEggs.eastereggs.put(ent, items);
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
