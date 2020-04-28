/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.pyro.features;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import static org.bukkit.GameMode.CREATIVE;
import org.bukkit.Location;
import static org.bukkit.Material.AIR;
import static org.bukkit.Material.NETHERRACK;
import static org.bukkit.Material.TNT;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import zedly.pyro.Storage;
import zedly.pyro.TNTExplosion;
import zedly.pyro.Utilities;

/**
 *
 * @author Dennis
 */
public class FireworkTNT extends FeatureClass {

    public static final FireworkTNT INSTANCE = new FireworkTNT();
    public static final HashMap<Block, TNTExplosion> fireworkTNTBlocks = new HashMap<>();
    private static final HashMap<Integer, TNTExplosion> fireworkTNTEntities = new HashMap<>();
    private static final HashSet<TNTPrimed> tntEntities = new HashSet<>();

    private FireworkTNT() {
    }

    @Override
    public int getTaskFrequency() {
        return 1;
    }

    @Override
    public void run() {
        //Detect recently ignited TNT entities and match their location with known modified TNT blocks
        HashSet<Block> changedModifiedTNTBlocks = new HashSet<>();
        for (Block block : fireworkTNTBlocks.keySet()) {
            if (block.getType() != TNT && block.getType() != NETHERRACK) {
                changedModifiedTNTBlocks.add(block);
            }
        }
        if (!changedModifiedTNTBlocks.isEmpty()) {
            Collection<TNTPrimed> newTNTEntities = new HashSet<>();
            Bukkit.getServer().getWorlds().forEach((world) -> {
                newTNTEntities.addAll(world.getEntitiesByClass(org.bukkit.entity.TNTPrimed.class));
            });
            newTNTEntities.removeAll(tntEntities);

            HashSet<TNTPrimed> ents1 = new HashSet<>();
            HashSet<Block> kill = new HashSet<>();
            ents1.addAll(newTNTEntities);
            for (TNTPrimed tnt : newTNTEntities) {
                Location loc = tnt.getLocation().add(new Vector(-0.5, -0.5, -0.5));
                for (Block block : fireworkTNTBlocks.keySet()) {
                    if (loc.distance(block.getLocation()) <= 0.6) {
                        fireworkTNTEntities.put(tnt.getEntityId(), fireworkTNTBlocks.get(block));
                        kill.add(block);
                    }
                }
            }
            for (Block block : fireworkTNTBlocks.keySet()) {
                if (block.getType() != TNT) {
                    kill.add(block);
                }
            }
            for (Block block : kill) {
                fireworkTNTBlocks.remove(block);
            }
            tntEntities.clear();
            tntEntities.addAll(ents1);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true) // Remote TNT Broken
    public boolean onBreak(BlockBreakEvent evt) {
        if (evt.getPlayer().getGameMode() != CREATIVE) {
            if (fireworkTNTBlocks.containsKey(evt.getBlock())) {
                if (evt.getBlock().getType() == TNT) {
                    evt.setCancelled(true); // Cancel regular block break
                    ItemStack stk = new ItemStack(TNT); // Drop Firework TNT item
                    evt.getBlock().setType(AIR);
                    ItemMeta meta = stk.getItemMeta();
                    meta.setLore(fireworkTNTBlocks.get(evt.getBlock()).params);
                    stk.setItemMeta(meta);
                    evt.getPlayer().getWorld().dropItemNaturally(Utilities.getCenter(evt.getBlock().getLocation()), stk);
                }
            }
        }
        return true;
    }

    @EventHandler(priority = EventPriority.LOWEST) // Remote TNT Placed
    public boolean onPlace(BlockPlaceEvent evt) {
        if (!evt.isCancelled()
                && evt.getPlayer().getInventory().getItemInMainHand().hasItemMeta()
                && evt.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasLore()) {
            if (evt.getBlockPlaced().getType() == TNT) {
                List<String> lore = evt.getItemInHand().getItemMeta().getLore();
                // TODO: Not all lore TNT is firework
                fireworkTNTBlocks.put(evt.getBlock(), new TNTExplosion(evt.getItemInHand().getItemMeta().getLore()));
            }
        }
        return true;
    }

    @EventHandler // Scans through exploding entities
    public boolean onExplode(EntityExplodeEvent evt) {
        if (fireworkTNTEntities.containsKey(evt.getEntity().getEntityId())) {
            evt.setCancelled(true);
            fireworkTNTEntities.get(evt.getEntity().getEntityId()).explode(evt.getEntity());
            fireworkTNTEntities.remove(evt.getEntity().getEntityId());
        }
        return true;
    }

}
