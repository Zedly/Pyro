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
import static org.bukkit.Material.COAL;
import static org.bukkit.Material.DIAMOND;
import static org.bukkit.Material.EMERALD;
import static org.bukkit.Material.GOLD_INGOT;
import static org.bukkit.Material.INK_SACK;
import static org.bukkit.Material.IRON_INGOT;
import static org.bukkit.Material.QUARTZ;
import static org.bukkit.Material.REDSTONE;
import static org.bukkit.Material.RED_ROSE;
import static org.bukkit.Material.WOOL;
import static org.bukkit.Material.YELLOW_FLOWER;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Dennis
 */
public class TaskItemTrails implements Runnable {

    private final HashSet<Item> trailItems = new HashSet<>();
    private final HashMap<Player, Location> lastTrailEmitLocations = new HashMap<>();

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
                            toDrop.add(new ItemStack(YELLOW_FLOWER, 0, (short) 0));
                            for (short x = 0; x < 8; x++) {
                                toDrop.add(new ItemStack(RED_ROSE, 0, (short) x));
                            }
                        }
                        if (lore.contains(ChatColor.GOLD + "Mineral Trail")) {
                            Material[] materials = new Material[]{GOLD_INGOT, REDSTONE, DIAMOND, IRON_INGOT, EMERALD, COAL, QUARTZ, DIAMOND};
                            toDrop.add(new ItemStack(INK_SACK, 0, (short) 4));
                            for (short x = 0; x < 8; x++) {
                                toDrop.add(new ItemStack(materials[x], 0, (short) 0));
                            }
                        }
                        if (lore.contains(ChatColor.GOLD + "Color Trail")) {
                            Material mat = WOOL;
                            short[] ints = {14, 6, 1, 4, 5, 13, 11, 3, 2};
                            for (short x = 0; x < 8; x++) {
                                toDrop.add(new ItemStack(WOOL, 0, (short) ints[x]));
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
                                    toDrop.add(new ItemStack(mat, 0, (short) data));
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
                            final ItemStack s = stk;
                            getServer().getScheduler().scheduleSyncDelayedTask(Storage.pyro, new Runnable() {
                                @Override
                                public void run() {
                                    trailItems.add(player.getWorld().dropItemNaturally(player.getLocation(), s));
                                }
                            }, counter);
                        }
                    }
                }
            }
            lastTrailEmitLocations.put(player, player.getLocation());
        }

    }

}
