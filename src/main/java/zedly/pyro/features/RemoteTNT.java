/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.pyro.features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import static org.bukkit.GameMode.CREATIVE;
import static org.bukkit.Material.AIR;
import static org.bukkit.Material.NETHERRACK;
import static org.bukkit.Material.TNT;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import zedly.pyro.Storage;
import zedly.pyro.Utilities;

/**
 *
 * @author Dennis
 */
public class RemoteTNT extends FeatureClass {

    public static final RemoteTNT INSTANCE = new RemoteTNT();

    private static final HashMap<Block, Integer> SIGN_POWER_CACHE = new HashMap<>();
    public static final HashMap<String, ArrayList<Block>> remoteTnt = new HashMap<>();

    private RemoteTNT() {
    }

    @EventHandler
    public void onRedstone(BlockPhysicsEvent evt) {
        if ((!Utilities.isSign(evt.getBlock())) || evt.isCancelled()) {
            return;
        }
        Block signBlock = evt.getBlock();
        int power = signBlock.getBlockPower();
        Integer oldPower = SIGN_POWER_CACHE.put(signBlock, power);
        if ((oldPower == null || oldPower == 0) && power > 0) { // Positive redstone edge on a sign
            redstoneEdgeOnSign((Sign) signBlock.getState());
        }
    }

    public void redstoneEdgeOnSign(Sign sign) {
        if (sign.getLine(0).equals(ChatColor.DARK_BLUE + "[Detonate]")) {
            String channel = sign.getLine(1);
            if (remoteTnt.containsKey(channel)) {
                ArrayList<Block> blocks = remoteTnt.get(channel);
                for (Block block : blocks) {
                    if (block.getType() == TNT) {
                        block.setType(AIR);
                        TNTPrimed ent = (TNTPrimed) block.getWorld().spawnEntity(block.getLocation(), EntityType.PRIMED_TNT);
                        ent.setYield(4);
                        ent.setFuseTicks(0);
                        if (FireworkTNT.fireworkTNTBlocks.containsKey(block)) {
                            FireworkTNT.fireworkTNTBlocks.get(block).explode(ent);
                        }
                    }
                }
                remoteTnt.remove(channel);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true) // Remote TNT Broken
    public boolean onBreak(BlockBreakEvent evt) {
        if (evt.getPlayer().getGameMode() != CREATIVE) {
            if (Utilities.isSign(evt.getBlock())) {
                evt.setCancelled(true);
                Sign sign = (Sign) evt.getBlock().getState();
                ItemStack stk = new ItemStack(evt.getBlock().getType());
                ItemMeta meta = stk.getItemMeta();
                List<String> lore = new ArrayList<>();
                if (sign.getLine(0).contains(ChatColor.DARK_BLUE + "[Detonate]")) {
                    lore.add(ChatColor.GOLD + "Remote Detonator");
                    for (int i = 1; i < 4; i++) {
                        if (!"".equals(sign.getLine(i))) {
                            lore.add(ChatColor.GREEN + sign.getLine(i));
                        }
                    }
                    evt.getBlock().setType(AIR);
                }
                meta.setLore(lore);
                stk.setItemMeta(meta);
                evt.getPlayer().getWorld().dropItemNaturally(Utilities.getCenter(evt.getBlock().getLocation()), stk);
            }
        }
        Iterator<Map.Entry<String, ArrayList<Block>>> it = remoteTnt.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ArrayList<Block>> pair = it.next();
            ArrayList<Block> list = (ArrayList<Block>) pair.getValue();
            if (list.contains(evt.getBlock())) {
                list.remove(evt.getBlock());
            }
            if (list.isEmpty()) {
                it.remove();
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
                int ind = lore.indexOf(ChatColor.GREEN + "Remote Controlled");
                if (ind != -1 && lore.size() >= ind + 2) {
                    for (int i = ind + 1; i < lore.size(); i++) {
                        String channel = lore.get(i);
                        if (remoteTnt.containsKey(ChatColor.stripColor(channel))) {
                            ArrayList<Block> blocks = remoteTnt.get(ChatColor.stripColor(channel));
                            blocks.add(evt.getBlockPlaced());
                        } else {
                            ArrayList<Block> blocks = new ArrayList<>();
                            blocks.add(evt.getBlockPlaced());
                            remoteTnt.put(ChatColor.stripColor(channel), blocks);
                        }
                    }
                }
            }
        }
        return true;
    }

    @EventHandler // Scans through exploding entities
    public boolean onExplode(EntityExplodeEvent evt) {
        List<Block> blocks = evt.blockList();
        for (List<Block> savedBlocks : remoteTnt.values()) {
            blocks.removeAll(savedBlocks);
            // Shouldn't they be lit instead
            // Nah makes more sense to protect them for coordinat6ed explosions
        }
        return true;
    }

    @EventHandler // Remote tnt detonated
    public boolean onDetonate(final PlayerInteractEvent evt) throws Exception {
        Player player = evt.getPlayer();
        if ((evt.getAction() == Action.RIGHT_CLICK_BLOCK || evt.getAction() == Action.RIGHT_CLICK_AIR) && player.getInventory().getItemInMainHand() != null) {
            if (player.getInventory().getItemInMainHand().hasItemMeta() && player.getInventory().getItemInMainHand().getItemMeta().hasLore()) {
                List<String> lore = player.getInventory().getItemInMainHand().getItemMeta().getLore();
                int ind = lore.indexOf(ChatColor.GREEN + "Remote Detonator");
                if (ind != -1 && lore.size() > 1) {
                    Block blk = evt.getClickedBlock();
                    if (blk != null) {
                        if (blk.getType().equals(TNT) && player.isSneaking()) {
                            if (Utilities.tryBreak(player, blk, false)) {
                                if (remoteTnt.containsKey(ChatColor.stripColor(lore.get(1)))) {
                                    remoteTnt.get(ChatColor.stripColor(lore.get(1))).add(blk);
                                } else {
                                    remoteTnt.put(ChatColor.stripColor(lore.get(1)), new ArrayList<Block>());
                                    remoteTnt.get(ChatColor.stripColor(lore.get(1))).add(blk);
                                }
                                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                                blk.setType(NETHERRACK);
                                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.pyro, new Runnable() {
                                    @Override
                                    public void run() {
                                        evt.getClickedBlock().setType(TNT);
                                    }
                                }, 5);
                            }
                        }
                        if (blk.getType().equals(TNT) || player.isSneaking()) {
                            return true;
                        }
                    }
                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
                    LinkedList<Block> blocks = new LinkedList<>();
                    lore.remove(0);
                    for (String channel : lore) {
                        if (remoteTnt.containsKey(ChatColor.stripColor(channel))) {
                            blocks.addAll(remoteTnt.get(ChatColor.stripColor(channel)));
                            remoteTnt.remove(ChatColor.stripColor(channel));
                        }
                    }
                    for (Block block : blocks) {
                        if (block.getType() == TNT) {

                            // BlockIgniteEvent
                            block.setType(AIR);
                            TNTPrimed ent = (TNTPrimed) block.getWorld().spawnEntity(block.getLocation(), EntityType.PRIMED_TNT);
                            ent.setYield(4);
                            ent.setFuseTicks(0);
                        }
                    }
                    player.sendMessage(Storage.logo + " Detonated " + blocks.size() + " block(s)!");
                } else if (ind != -1 && lore.size() == 1) {
                    lore.add(ChatColor.GOLD + "" + RandomStringUtils.randomAlphabetic(8));
                    ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
                    meta.setLore(lore);
                    player.getInventory().getItemInMainHand().setItemMeta(meta);
                }
            }
        }
        return true;
    }
}
