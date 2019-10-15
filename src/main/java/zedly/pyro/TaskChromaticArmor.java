/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.pyro;

import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import static org.bukkit.Material.LEATHER_BOOTS;
import static org.bukkit.Material.LEATHER_CHESTPLATE;
import static org.bukkit.Material.LEATHER_HELMET;
import static org.bukkit.Material.LEATHER_LEGGINGS;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 *
 * @author Dennis
 */
public class TaskChromaticArmor implements Runnable {

    private final HashMap<Integer, Integer> chromaticColorProgress = new HashMap<>();

    public void run() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            int entityId = player.getEntityId();
            int counter = 0;
            if (chromaticColorProgress.containsKey(entityId)) {
                counter = chromaticColorProgress.get(entityId);
            }
            chromaticColorProgress.put(entityId, counter + 1);
            for (ItemStack stk : player.getInventory().getArmorContents()) {
                if (stk != null && stk.hasItemMeta() && stk.getItemMeta().hasLore()
                        && stk.getItemMeta().getLore().size() > 1) {
                    if (ArrayUtils.contains(Storage.leather, stk.getType())) {
                        List<String> lore = stk.getItemMeta().getLore();
                        if (lore.get(0).contains(ChatColor.GREEN + "Chromatic Armor") && !lore.get(0).contains("Not Configured")) {
                            stk.setDurability((short) 0); // Chromatic armor does not wear
                            LeatherArmorMeta meta = (LeatherArmorMeta) stk.getItemMeta();
                            int[] color;
                            try {
                                //TODO: Cache this
                                double[] params = Utilities.parseParameters(ChatColor.stripColor(lore.get(1)));
                                if (ArrayUtils.contains(Storage.FW_COLOR_FRIENDLY_NAMES, ChatColor.stripColor(lore.get(0).toLowerCase().split(": ")[1].replace(" ", "_")))) {
                                    color = Utilities.getThemedColor(params, counter);
                                } else {
                                    color = Utilities.getColor(params, counter);
                                }
                                if (Storage.vanishedPlayers.contains(player)) {
                                    int i = Storage.rnd.nextInt(30);
                                    if (Storage.rnd.nextInt(50) == 10) {
                                        color = new int[]{255, 255, 255};
                                    } else {
                                        color = new int[]{Storage.rnd.nextInt(20) + 75 + i, Storage.rnd.nextInt(20) + 75 + i, Storage.rnd.nextInt(20) + 75 + i};
                                    }
                                }
                                meta.setColor(Color.fromRGB(Utilities.clamp(color[0]), Utilities.clamp(color[1]), Utilities.clamp(color[2])));
                                stk.setItemMeta(meta);
                                switch (stk.getType()) {
                                    case LEATHER_HELMET:
                                        player.getInventory().setHelmet(stk);
                                        break;
                                    case LEATHER_CHESTPLATE:
                                        player.getInventory().setChestplate(stk);
                                        break;
                                    case LEATHER_LEGGINGS:
                                        player.getInventory().setLeggings(stk);
                                        break;
                                    case LEATHER_BOOTS:
                                        player.getInventory().setBoots(stk);
                                        break;
                                    default:
                                        break;
                                }
                            } catch (Exception e) {
                                lore.set(0, ChatColor.GREEN + "Chromatic Armor: " + ChatColor.GOLD + "Not Configured");
                                lore.set(1, ChatColor.GRAY + "Not Configured");
                                meta.setLore(lore);
                                meta.setLore(lore);
                                stk.setItemMeta(meta);
                                return;
                            }
                            if ((player.isFlying() || player.isSprinting()) && Storage.rnd.nextBoolean() && !Storage.vanishedPlayers.contains(player)) {
                                Location loc = player.getLocation().clone();
                                loc.subtract(player.getLocation().getDirection());
                                float heightAboveFeetPos = 0;
                                switch (stk.getType()) {
                                    case LEATHER_HELMET:
                                        heightAboveFeetPos = 1.75f;
                                        break;
                                    case LEATHER_CHESTPLATE:
                                        heightAboveFeetPos = 1.25f;
                                        break;
                                    case LEATHER_LEGGINGS:
                                        heightAboveFeetPos = .83f;
                                        break;
                                    case LEATHER_BOOTS:
                                        heightAboveFeetPos = .38f;
                                        break;
                                }
                                loc.setY(loc.getY() + heightAboveFeetPos + (Storage.rnd.nextFloat() / 4) * (Storage.rnd.nextInt(2) * 2 - 1));
                                loc.setX(loc.getX() + (Storage.rnd.nextFloat() / 4) * (Storage.rnd.nextInt(2) * 2 - 1));
                                loc.setZ(loc.getZ() + (Storage.rnd.nextFloat() / 4) * (Storage.rnd.nextInt(2) * 2 - 1));
                                Color col = Color.fromRGB(Utilities.clamp(color[0]), Utilities.clamp(color[1]), Utilities.clamp(color[2]));
                                //ParticleEffect.REDSTONE.display(null, loc, col, 32, 0, 0, 0, 1, 1);
                                Particle.DustOptions dustOptions = new Particle.DustOptions(col, 1);
                                loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 32, 0, 0, 0, 0, dustOptions, true);
                            }
                        }
                    }
                }
            }
        }
    }

}
