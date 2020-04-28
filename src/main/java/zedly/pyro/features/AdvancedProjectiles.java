/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.pyro.features;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import static org.bukkit.Material.AIR;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import zedly.pyro.Utilities;
import zedly.pyro.projectiles.AdvancedProjectile;

/**
 *
 * @author Dennis
 */
public class AdvancedProjectiles extends FeatureClass {

    public static final AdvancedProjectiles INSTANCE = new AdvancedProjectiles();
    //Collections
    public static final HashMap<Entity, AdvancedProjectile> advancedProjectiles = new HashMap<>();
    private final HashMap<Location, String> advancedFireballsDispensing = new HashMap<>();

    private AdvancedProjectiles() {
    }
    
    @Override
    public int getTaskFrequency() {
        return 2;
    }

    public void run() {
        Iterator<Entry<Entity, AdvancedProjectile>> it = advancedProjectiles.entrySet().iterator();
        while(it.hasNext()) {
            Entry<Entity, AdvancedProjectile> e = it.next();
            if (e.getValue().isAlive()) {
                e.getValue().trail();
                e.getValue().incrementTick();
            } else {
                it.remove();
            }
        }
    }

    @EventHandler // Advanced Projectiles in Dispensers
    public void onParticleDispenser(final BlockDispenseEvent evt) {
        ItemStack stk = (ItemStack) evt.getItem();

        // Advanced Projectiles in Dispensers
        if (AdvancedProjectile.isAdvancedFireball(stk)) {
            advancedFireballsDispensing.put(evt.getBlock().getLocation(), stk.getItemMeta().getLore().get(0));
            return;
        }
    }

    @EventHandler // Advanced Projectile launched
    public boolean onDetonate(final PlayerInteractEvent evt) throws Exception {
        if (evt.getAction() == RIGHT_CLICK_AIR
                && Utilities.matchItemStack(evt.getItem(), Material.FIRE_CHARGE, null, null)
                && evt.getItem().getItemMeta().hasLore()) {
            ItemStack is = evt.getItem();
            if (AdvancedProjectile.isAdvancedFireball(is)) {
                SmallFireball sf = (SmallFireball) evt.getPlayer().getWorld().spawnEntity(evt.getPlayer().getLocation().add(new Vector(0, 1.62, 0)).add(evt.getPlayer().getLocation().getDirection().multiply(2.5)), EntityType.SMALL_FIREBALL);
                sf.setVelocity(evt.getPlayer().getLocation().getDirection().multiply(1.5));
                sf.setIsIncendiary(false);
                AdvancedProjectile ap = AdvancedProjectile.create(is, sf);
                advancedProjectiles.put(sf, ap);
                if (is.getAmount() == 1) {
                    evt.getPlayer().getInventory().setItemInMainHand(new ItemStack(AIR));
                } else {
                    is.setAmount(is.getAmount() - 1);
                    if (evt.getHand() == EquipmentSlot.HAND) {
                        evt.getPlayer().getInventory().setItemInMainHand(is);
                    } else {
                        evt.getPlayer().getInventory().setItemInMainHand(is);
                    }
                }
                return true;
            }
        }
        return true;
    }

    @EventHandler // Advanced Projectiles & Lore Bows
    public void onAdvancedProjectileHit(ProjectileHitEvent evt) {
        // Advanced Projectiles
        if (advancedProjectiles.containsKey(evt.getEntity())) {
            advancedProjectiles.get(evt.getEntity()).impact();
            advancedProjectiles.remove(evt.getEntity());
        }
    }

    @EventHandler // Advanced Projectiles in Dispensers
    public void onAdvancedProjectileLaunch(ProjectileLaunchEvent evt) {
        Set<Location> toDie = new HashSet<>();
        for (Location l : advancedFireballsDispensing.keySet()) {
            if (l.distance(evt.getEntity().getLocation()) < 2) {
                String loreString = advancedFireballsDispensing.get(l);
                AdvancedProjectile ap = AdvancedProjectile.create(loreString, (SmallFireball) evt.getEntity());
                advancedProjectiles.put(evt.getEntity(), ap);
                toDie.add(l);
            }
        }
        for (Location l : toDie) {
            advancedFireballsDispensing.remove(l);
        }
    }
}
