package zedly.pyro;

import java.util.*;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import static org.bukkit.Material.*;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class MFEffects implements Runnable {

    @Override
    public void run() {
        //Create Item Trails
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (!Storage.playerLocations.containsKey(player)) {
                Storage.playerLocations.put(player, player.getLocation());
            } else {
                if (player.getWorld().equals(Storage.playerLocations.get(player).getWorld())) {
                    if (player.getLocation().distance(Storage.playerLocations.get(player)) != 0 && player.getInventory().getChestplate() != null) {
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
                                        Storage.dropItem.add(player.getWorld().dropItemNaturally(player.getLocation(), s));
                                    }
                                }, counter);
                            }
                        }
                    }
                }
            }
            Storage.playerLocations.put(player, player.getLocation());
        }
        //Easter Eggs
        Iterator it = Storage.eastereggs.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Item, ItemStack> egg = (Entry<Item, ItemStack>) it.next();
            if (egg.getKey() == null || egg.getKey().isDead()) {
                it.remove();
            } else if (egg.getKey().getTicksLived() > 1200) {
                Location loc = egg.getKey().getLocation();
                ItemStack eggContent = egg.getValue();
                ItemStack eggItem = egg.getKey().getItemStack();
                egg.getKey().remove();
                it.remove();
                Item item = loc.getWorld().dropItem(loc, eggItem);
                item.setVelocity(new Vector(0, 0, 0));
                Storage.eastereggs.put(item, eggContent);
            }
        }
    }
}
