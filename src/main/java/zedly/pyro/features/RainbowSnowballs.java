/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.pyro.features;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import static org.bukkit.Material.AIR;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Dennis
 */
public class RainbowSnowballs extends FeatureClass {

    public static final String RAINBOW_LORE = ChatColor.GREEN + "Rainbow";
    public static final RainbowSnowballs INSTANCE = new RainbowSnowballs();
    public static final Material[] RAINBOW_GLASS = {Material.RED_STAINED_GLASS, Material.ORANGE_STAINED_GLASS, Material.YELLOW_STAINED_GLASS, Material.GREEN_STAINED_GLASS, Material.LIGHT_BLUE_STAINED_GLASS, Material.BLUE_STAINED_GLASS, Material.PURPLE_STAINED_GLASS};
    private static final HashMap<Block, Long> rainbowBlocks = new HashMap<>();
    private static final HashSet<Entity> snowballs = new HashSet<>();

    private RainbowSnowballs() {
    }

    @Override
    public int getTaskFrequency() {
        return 1;
    }

    @Override // Run every tick
    public void run() {
        //Create Glass Rainbows        
        snowballs.removeIf(Entity::isDead);

        for (Entity ent : snowballs) {
            Location snowballLoc = ent.getLocation().clone();
            double direction = ent.getLocation().getYaw() * Math.PI / 180.0;

            int discreteDx = (int) Math.round(Math.sin(direction));
            int discreteDz = (int) Math.round(Math.cos(direction));

            for (int c = 0; c < 7; c++) {
                Location rainbowBlockLoc = snowballLoc.clone().add(-discreteDx, -c, -discreteDz);
                if (rainbowBlockLoc.getBlock().getType() == AIR) {
                    rainbowBlockLoc.getBlock().setType(RAINBOW_GLASS[c]);
                    rainbowBlocks.put(rainbowBlockLoc.getBlock(), System.currentTimeMillis());
                }
            }
        }

        //Remove Glass Rainbows
        long currentTime = System.currentTimeMillis();
        Iterator<Entry<Block, Long>> it = rainbowBlocks.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Block, Long> entry = it.next();
            if (currentTime - entry.getValue() > 10000) {
                entry.getKey().setType(AIR);
                it.remove();
            }
        }
    }

    @EventHandler // Rainbow snowball is launched
    public void onRainbowSnowball(ProjectileLaunchEvent evt) {
        if (evt.getEntity().getType() == EntityType.SNOWBALL) {
            if (evt.getEntity().getShooter() != null) {
                if (evt.getEntity().getShooter() instanceof HumanEntity) {
                    Player player = (Player) evt.getEntity().getShooter();
                    if (isRainbowSnowball(player.getInventory().getItemInMainHand())
                            || isRainbowSnowball(player.getInventory().getItemInOffHand())) {
                        snowballs.add(evt.getEntity());
                    }
                }
            }
        }
    }

    private boolean isRainbowSnowball(ItemStack is) {
        return is.getType() == Material.SNOWBALL
                && is.hasItemMeta() && is.getItemMeta().hasLore()
                && is.getItemMeta().getLore().contains(RAINBOW_LORE);
    }
}
