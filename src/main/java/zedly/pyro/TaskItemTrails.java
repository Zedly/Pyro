/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.pyro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import static org.bukkit.Material.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

/**
 *
 * @author Dennis
 */
public class TaskItemTrails implements Runnable {

    private static final Material[] FLOWER_TRAIL = {POPPY, DANDELION, AZURE_BLUET, ROSE_BUSH, LILAC, BLUE_ORCHID, ALLIUM,
        RED_TULIP, ORANGE_TULIP, WHITE_TULIP, PINK_TULIP, OXEYE_DAISY, CORNFLOWER, LILY_OF_THE_VALLEY};
    private static final Material[] MINERAL_TRAIL = {GOLD_INGOT, REDSTONE, IRON_INGOT, EMERALD, COAL, QUARTZ, DIAMOND, IRON_NUGGET, GOLD_NUGGET};
    private static final Material[] COLOR_TRAIL = {RED_WOOL, ORANGE_WOOL, YELLOW_WOOL, LIME_WOOL, GREEN_WOOL, CYAN_WOOL, LIGHT_BLUE_WOOL, BLUE_WOOL, PURPLE_WOOL, PINK_WOOL};
    

    public static final HashSet<Item> trailItems = new HashSet<>();
    private final HashMap<Player, Location> lastTrailEmitLocations = new HashMap<>();
    private static final ArrayList<String> VOLATILE_LORE = new ArrayList<String>(1);

    static {
        VOLATILE_LORE.add(ChatColor.GRAY + "Volatile");
    }

    public void run() {
        //Remove Item Trails
        Iterator dropIt = trailItems.iterator();
        while (dropIt.hasNext()) {
            Item item = (Item) dropIt.next();
            if (item.getTicksLived() >= 100) {
                item.remove();
                dropIt.remove();
            }
        }

        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (!lastTrailEmitLocations.containsKey(player)) {
                lastTrailEmitLocations.put(player, player.getLocation());
            } else if (player.getWorld().equals(lastTrailEmitLocations.get(player).getWorld())) {
                if (player.getLocation().distance(lastTrailEmitLocations.get(player)) >= 0.25 && player.getInventory().getChestplate() != null
                        && player.getInventory().getChestplate().hasItemMeta()) {
                    if (player.getInventory().getChestplate().getItemMeta().hasLore()) {
                        List<String> lore = player.getInventory().getChestplate().getItemMeta().getLore();
                        ArrayList<ItemStack> toDrop = new ArrayList<>();
                        if (lore.contains(ChatColor.GOLD + "Flower Trail")) {
                            for (Material m : FLOWER_TRAIL) {
                                toDrop.add(new ItemStack(m));
                            }
                        }
                        if (lore.contains(ChatColor.GOLD + "Mineral Trail")) {
                            for (Material m : MINERAL_TRAIL) {
                                toDrop.add(new ItemStack(m));
                            }
                        }
                        if (lore.contains(ChatColor.GOLD + "Color Trail")) {
                            for (Material m : COLOR_TRAIL) {
                                toDrop.add(new ItemStack(m));
                            }
                        }
                        
                        
                        if (lore.contains(ChatColor.GOLD + "Custom Trail")) {
                            for (String str : lore) {
                                str = ChatColor.stripColor(str).toUpperCase();
                                Material tempMat = Material.matchMaterial(str);
                                if (tempMat != null && tempMat != Material.AIR) {
                                    toDrop.add(new ItemStack(tempMat));
                                }
                            }
                        }
                        int counter = 0;
                        for (ItemStack stk : toDrop) {
                            if (counter < 10) {
                                counter++;
                            } else {
                                counter = 1;
                            }
                            ItemMeta meta = stk.getItemMeta();
                            meta.setDisplayName(ChatColor.MAGIC + "Transient");
                            meta.setLore(VOLATILE_LORE);
                            stk.setItemMeta(meta);
                            final ItemStack s = stk;
                            getServer().getScheduler().scheduleSyncDelayedTask(Storage.pyro, new Runnable() {
                                @Override
                                public void run() {
                                    Item item = player.getWorld().dropItemNaturally(player.getLocation().add(new Vector(-0.5, 0, -0.5)), s);
                                    item.setPickupDelay(Integer.MAX_VALUE);
                                    trailItems.add(item);
                                }
                            }, 2 * counter);
                        }
                    }
                }
            }
            lastTrailEmitLocations.put(player, player.getLocation());
        }

    }

}
