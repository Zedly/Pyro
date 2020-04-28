/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.pyro.features;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import static org.bukkit.Material.DISPENSER;
import static org.bukkit.Material.SNOWBALL;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import zedly.pyro.FireworkEffectPlayer;
import zedly.pyro.Storage;

/**
 *
 * @author Dennis
 */
public class BangSnowballs extends FeatureClass {

    public static final String BANG_LORE = ChatColor.GOLD + "Bang";
    public static final BangSnowballs INSTANCE = new BangSnowballs();
    private static final HashSet<Snowball> flyingBangSnowballs = new HashSet<>();
    private static final HashSet<Location> expectedBangDispensers = new HashSet<>();

    private BangSnowballs() {
    }

    @EventHandler // Bang Ball is dispensed
    public boolean onDispense(BlockDispenseEvent evt) {
        if (evt.getBlock().getType() == DISPENSER) {
            if (evt.getItem() != null) {
                ItemStack is = evt.getItem();
                if (evt.getItem().getType() == SNOWBALL) {
                    if (is.getItemMeta().hasLore()) {
                        if (is.getItemMeta().getLore().contains(BANG_LORE)) {
                            expectedBangDispensers.add(evt.getBlock().getLocation());
                        }
                    }
                }
            }
        }
        return true;
    }

    @EventHandler // Bang Ball is shot
    public boolean onProjectileLaunch(ProjectileLaunchEvent evt) {
        if (evt.getEntity().getType() != EntityType.SNOWBALL) {
            return true;
        }
        if (evt.getEntity().getShooter() == null) {
            for (Location l : expectedBangDispensers) {
                if (l.distance(evt.getEntity().getLocation()) < 2) {
                    flyingBangSnowballs.add((Snowball) evt.getEntity());
                    expectedBangDispensers.remove(l);
                    break;
                }
            }
        } else if (!(evt.getEntity().getShooter() instanceof HumanEntity)) {
            return true;
        }

        Player player = (Player) evt.getEntity().getShooter();

        if (player.getInventory().getItemInMainHand().getType().equals(SNOWBALL)) {
            if (player.getInventory().getItemInMainHand().getItemMeta().hasLore()
                    && player.getInventory().getItemInMainHand().getItemMeta().getLore().contains(BANG_LORE)) {
                flyingBangSnowballs.add((Snowball) evt.getEntity());
            }

        } else if (player.getInventory().getItemInOffHand().getType().equals(SNOWBALL)) {
            if (player.getInventory().getItemInOffHand().getItemMeta().hasLore()
                    && player.getInventory().getItemInOffHand().getItemMeta().getLore().contains(BANG_LORE)) {
                flyingBangSnowballs.add((Snowball) evt.getEntity());
            }
        }
        return true;
    }

    @EventHandler // Bang Ball hits object
    public boolean onProjectileHit(ProjectileHitEvent evt) {
        if (flyingBangSnowballs.contains(evt.getEntity())) {
            FireworkEffect.Builder bu = FireworkEffect.builder();
            bu = bu.withColor(org.bukkit.Color.fromRGB(Storage.RAINBOW_COLORS[Storage.rnd.nextInt(12)]));
            bu = bu.trail(true);
            bu = bu.with(FireworkEffect.Type.BALL);
            FireworkEffectPlayer.playFirework(evt.getEntity().getLocation(), bu.build());
            flyingBangSnowballs.remove((Snowball) evt.getEntity());
        }
        return true;
    }
}
