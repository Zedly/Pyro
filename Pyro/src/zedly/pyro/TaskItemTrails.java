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
import static org.bukkit.Material.*;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TaskItemTrails implements Runnable {

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
                        List<ItemStack> toDrop = new ArrayList<>();
                        if (lore.contains(ChatColor.GOLD + "Flower Trail")) {
                        	toDrop.addAll(Storage.COMPATIBILITY_ADAPTER.SmallFlowers().getEnumList());
                        }
                        if (lore.contains(ChatColor.GOLD + "Mineral Trail")) {
                            Material[] materials = new Material[]{GOLD_INGOT, REDSTONE, IRON_INGOT, EMERALD, COAL, QUARTZ, DIAMOND, LAPIS_LAZULI};
                            for (short x = 0; x < 8; x++) {
                                toDrop.add(new ItemStack(materials[x], 1));
                            }
                        }
                        if (lore.contains(ChatColor.GOLD + "Color Trail")) {
                            Material[] mats = {RED_WOOL, PINK_WOOL, ORANGE_WOOL, YELLOW_WOOL, LIME_WOOL, GREEN_WOOL,
	                            BLUE_WOOL, LIGHT_BLUE_WOOL, MAGENTA_WOOL};
                            for (short x = 0; x < 8; x++) {
                                toDrop.add(new ItemStack(mats[x], 1));
                            }
                        }
                        if (lore.contains(ChatColor.GOLD + "Custom Trail")) {
                            for (String str : lore) {
                                str = ChatColor.stripColor(str.replace(" ", "_").toUpperCase());
                                String tempMat = null;
                                short data = 0;
                                if (str.contains(":")) {
                                    String[] split = str.split(":");
                                    tempMat = split[0];
                                    try {
                                        data = Short.parseShort(split[1]);
                                    } catch (NumberFormatException e) {
                                    }
                                }
                                if (Material.getMaterial(tempMat) != null && Material.getMaterial(tempMat) != Material.AIR) {
                                    Material mat = Material.getMaterial(tempMat);
                                    toDrop.add(new ItemStack(mat, 1, (short) data));
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
                                    Item item = player.getWorld().dropItemNaturally(player.getLocation(), s);
                                    item.setPickupDelay(Integer.MAX_VALUE);
                                    // s.setAmount(0);
                                    // item.setItemStack(s);
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
