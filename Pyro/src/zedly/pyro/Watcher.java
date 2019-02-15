package zedly.pyro;

import java.util.*;
import java.util.Map.Entry;
import org.apache.commons.lang.*;
import org.bukkit.*;
import org.bukkit.FireworkEffect;
import static org.bukkit.GameMode.*;
import static org.bukkit.Material.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import static org.bukkit.event.inventory.InventoryType.SlotType.RESULT;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.util.Vector;

public class Watcher implements Listener {

    private static final HashMap<Block, Integer> SIGN_POWER_CACHE = new HashMap<>();

    @EventHandler // Vanish activated
    public boolean onCommand(PlayerCommandPreprocessEvent evt) {
        if (evt.getMessage().equals("/vanish") && evt.getPlayer().hasPermission("vanish.vanish")) {
            if (Storage.vanishedPlayers.contains(evt.getPlayer())) {
                Storage.vanishedPlayers.remove(evt.getPlayer());
            } else {
                Storage.vanishedPlayers.add(evt.getPlayer());
            }
        }
        return true;
    }

    @EventHandler // Firework sign created
    public boolean onSignChange(SignChangeEvent evt) {
        if (Storage.blockedSigns.contains(evt.getBlock())) {
            evt.setCancelled(true);
            Storage.blockedSigns.remove(evt.getBlock());
            return true;
        }
        if (evt.getLine(0).equalsIgnoreCase("[firework]") && evt.getPlayer().hasPermission("pyro.firework")) {
            evt.setLine(0, ChatColor.DARK_BLUE + "[Firework]");
        }
        return true;
    }

    @EventHandler // Bang Ball is dispensed
    public boolean onDispense(BlockDispenseEvent evt) {
        if (evt.getBlock().getType() == DISPENSER) {
            if (evt.getItem() != null) {
                ItemStack is = evt.getItem();
                if (evt.getItem().getType() == SNOWBALL) {
                    if (is.getItemMeta().hasLore()) {
                        if (is.getItemMeta().getLore().contains(ChatColor.GOLD + "Bang")) {
                            Storage.dispenserProj.put(evt.getBlock().getLocation(), "Bang");
                        }
                    }
                }
            }
        }
        return true;
    }

    @EventHandler // Bang Ball is shot
    public boolean onProjectileLaunch(ProjectileLaunchEvent evt) {
        if (evt.getEntity() != null) {
            Set<Location> toDie = new HashSet<>();
            for (Location l : Storage.dispenserProj.keySet()) {
                if (l.distance(evt.getEntity().getLocation()) < 2) {
                    if (Storage.dispenserProj.get(l).equalsIgnoreCase("Bang")) {
                        Storage.bangBalls.add((Snowball) evt.getEntity());
                        toDie.add(l);
                    }
                }
            }
            for (Location l : toDie) {
                Storage.dispenserProj.remove(l);
            }
        }
        return true;
    }

    @EventHandler
    public void onRedstone(BlockPhysicsEvent evt) {
        if ((evt.getBlock().getType() != SIGN
                && evt.getBlock().getType() != WALL_SIGN) || evt.isCancelled()) {
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
        if (sign.getLine(0).equals(ChatColor.DARK_BLUE + "[Firework]")) {
            if (!sign.getLine(1).equals("")) {
                Utilities.explodeFromString(sign.getLocation(), sign.getLine(1));
            }
            if (!sign.getLine(2).equals("")) {
                Utilities.explodeFromString(sign.getLocation(), sign.getLine(2));
            }
            if (!sign.getLine(3).equals("")) {
                Utilities.explodeFromString(sign.getLocation(), sign.getLine(3));
            }
        } else if (sign.getLine(0).equals(ChatColor.DARK_BLUE + "[Detonate]")) {
            String channel = sign.getLine(1);
            if (Storage.remoteTnt.containsKey(channel)) {
                ArrayList<Block> blocks = Storage.remoteTnt.get(channel);
                for (Block block : blocks) {
                    if (block.getType() == TNT) {
                        block.setType(AIR);
                        TNTPrimed ent = (TNTPrimed) block.getWorld().spawnEntity(block.getLocation(), EntityType.PRIMED_TNT);
                        ent.setYield(4);
                        ent.setFuseTicks(0);
                        if (Storage.explodingBlocks.containsKey(block)) {
                            Storage.explodingBlocks.get(block).explode(ent);
                        }
                    }
                }
                Storage.remoteTnt.remove(channel);
            }
        }
    }

    @EventHandler // Removed Vanished Players
    public boolean onQuit(PlayerQuitEvent evt) {
        Storage.vanishedPlayers.remove(evt.getPlayer());
        return true;
    }

    @EventHandler(priority = EventPriority.MONITOR) // Remote TNT Broken
    public boolean onBreak(BlockBreakEvent evt) {
        if (!evt.isCancelled()) {
            if (evt.getPlayer().getGameMode() != CREATIVE) {
                if (Storage.explodingBlocks.containsKey(evt.getBlock())) {
                    if (evt.getBlock().getType() == TNT) {
                        ItemStack stk = new ItemStack(TNT);
                        evt.setCancelled(true);
                        evt.getBlock().setType(AIR);
                        ItemMeta meta = stk.getItemMeta();
                        meta.setLore(Storage.explodingBlocks.get(evt.getBlock()).params);
                        stk.setItemMeta(meta);
                        evt.getPlayer().getWorld().dropItemNaturally(Utilities.getCenter(evt.getBlock().getLocation()), stk);
                    }
                }
                if (evt.getBlock().getType() == WALL_SIGN || evt.getBlock().getType() == SIGN) {
                    ItemStack stk = new ItemStack(SIGN);
                    Sign sign = (Sign) evt.getBlock().getState();
                    ItemMeta meta = stk.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    evt.setCancelled(true);
                    evt.getBlock().setType(AIR);
                    if (sign.getLine(0).contains(ChatColor.DARK_BLUE + "[Firework]")) {
                        evt.getBlock().setType(AIR);
                        lore.add(ChatColor.GOLD + "Firework Sign");
                        for (int i = 1; i < 4; i++) {
                            if (!"".equals(sign.getLine(i))) {
                                lore.add(ChatColor.GREEN + sign.getLine(i));
                            }
                        }
                    }
                    meta.setLore(lore);
                    stk.setItemMeta(meta);
                    evt.getPlayer().getWorld().dropItemNaturally(Utilities.getCenter(evt.getBlock().getLocation()), stk);
                }
            }
            for (Entry<String, ArrayList<Block>> pair : Storage.remoteTnt.entrySet()) {
                ArrayList<Block> list = pair.getValue();
                if (list.contains(evt.getBlock())) {
                    list.remove(evt.getBlock());
                }
                if (list.isEmpty()) {
                    Storage.remoteTnt.remove(pair.getKey());
                }
            }
        }
        return true;
    }

    @EventHandler(priority = EventPriority.MONITOR) // Remote TNT Placed
    public boolean onPlace(BlockPlaceEvent evt) {
        if (!evt.isCancelled() && evt.getPlayer().getItemInHand().hasItemMeta() && evt.getPlayer().getItemInHand().getItemMeta().hasLore()) {
            if (evt.getBlockPlaced().getType() == TNT) {
                List<String> lore = evt.getPlayer().getItemInHand().getItemMeta().getLore();
                Storage.explodingBlocks.put(evt.getBlock(), new TNTExplosion(evt.getPlayer().getItemInHand().getItemMeta().getLore()));
                int ind = lore.indexOf(ChatColor.GREEN + "Remote Controlled");
                if (ind != -1 && lore.size() >= ind + 2) {
                    lore.remove(0);
                    for (String channel : lore) {
                        if (Storage.remoteTnt.containsKey(ChatColor.stripColor(channel))) {
                            ArrayList<Block> blocks = Storage.remoteTnt.get(ChatColor.stripColor(channel));
                            blocks.add(evt.getBlockPlaced());
                        } else {
                            ArrayList<Block> blocks = new ArrayList<>();
                            blocks.add(evt.getBlockPlaced());
                            Storage.remoteTnt.put(ChatColor.stripColor(channel), blocks);
                        }
                    }
                }
            } else if (evt.getPlayer().getInventory().getItemInMainHand() != null && evt.getPlayer().getItemInHand().getType() == Material.SIGN && evt.getPlayer().hasPermission("pyro.fireworksign")) {
                ItemStack is = evt.getPlayer().getInventory().getItemInMainHand();
                Block block = evt.getBlockPlaced();
                if (block.getType() != Material.SIGN && block.getType() != Material.WALL_SIGN) {
                    return true;
                }
                if (is.hasItemMeta() && is.getItemMeta().hasLore() && is.getItemMeta().getLore().get(0).equals(ChatColor.GOLD + "Firework Sign")) {
                    Sign sign = (Sign) block.getState();
                    List<String> lines = is.getItemMeta().getLore();
                    sign.setLine(0, ChatColor.DARK_BLUE + "[Firework]");
                    if (lines.size() >= 2) {
                        sign.setLine(1, ChatColor.stripColor(lines.get(1)));
                    }
                    if (lines.size() >= 3) {
                        sign.setLine(2, ChatColor.stripColor(lines.get(2)));
                    }
                    if (lines.size() >= 4) {
                        sign.setLine(3, ChatColor.stripColor(lines.get(3)));
                    }
                    sign.update();
                    Storage.blockedSigns.add(block);
                }
            }
        }
        return true;
    }

    @EventHandler // Scans through exploding entities
    public boolean onExplode(EntityExplodeEvent evt) {
        if (evt.getEntity() == null) {
            return true;
        }
        if (Storage.explodingEntities.containsKey(evt.getEntity().getEntityId())) {
            evt.setCancelled(true);
            Storage.explodingEntities.get(evt.getEntity().getEntityId()).explode(evt.getEntity());
            Storage.explodingEntities.remove(evt.getEntity().getEntityId());
        }
        List<Block> blocks = evt.blockList();
        for (List<Block> savedBlocks : Storage.remoteTnt.values()) {
            blocks.removeAll(savedBlocks);
        }
        return true;
    }

    @EventHandler // Player teleports, linked to tpboom
    public boolean onTeleport(PlayerTeleportEvent evt) {
        if (evt.getTo().getWorld() == evt.getFrom().getWorld() && evt.getTo().distance(evt.getFrom()) < 10) {
            return true;
        }
        if (Storage.vanishedPlayers.contains(evt.getPlayer())) {
            return true;
        }
        Location loc = evt.getFrom().clone();
        Utilities.explodeFromArmor(evt.getPlayer().getInventory().getBoots(), loc.add(new Vector(0, 0.38, 0)).clone());
        Utilities.explodeFromArmor(evt.getPlayer().getInventory().getLeggings(), loc.add(new Vector(0, 0.45, 0)).clone());
        Utilities.explodeFromArmor(evt.getPlayer().getInventory().getChestplate(), loc.add(new Vector(0, 0.42, 0)).clone());
        Utilities.explodeFromArmor(evt.getPlayer().getInventory().getHelmet(), loc.add(new Vector(0, 0.5, 0)).clone());
        return true;
    }

    @EventHandler // Bang Ball hits object
    public boolean onProjectileHit(ProjectileHitEvent evt) {
        if (evt.getEntity() instanceof Snowball && Storage.bangBalls.contains((Snowball) evt.getEntity())) {
            FireworkEffect.Builder bu = FireworkEffect.builder();
            bu = bu.withColor(org.bukkit.Color.fromRGB(Storage.rainbowcolors[Storage.rnd.nextInt(12)]));
            bu = bu.trail(true);
            bu = bu.with(FireworkEffect.Type.BALL);
            FireworkEffectPlayer.playFirework(evt.getEntity().getLocation(), bu.build());
            Storage.bangBalls.remove((Snowball) evt.getEntity());
        }
        return true;
    }

    @EventHandler // Rainbow snowball is launched
    public void onRainbowSnowball(ProjectileLaunchEvent evt) {
        if (evt.getEntity() != null) {
            if (evt.getEntity().getType() == EntityType.SNOWBALL) {
                if (evt.getEntity().getShooter() != null) {
                    if (evt.getEntity().getShooter() instanceof HumanEntity) {
                        Player player = (Player) evt.getEntity().getShooter();
                        if (player.getItemInHand().getType().equals(SNOWBALL) && player.getItemInHand().getItemMeta().hasLore()) {
                            if (player.getItemInHand().getItemMeta().getLore().contains(ChatColor.GOLD + "Bang")) {
                                Storage.bangBalls.add((Snowball) evt.getEntity());
                            }
                        }
                        if (player.getItemInHand() != null) {
                            if (player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().hasLore()) {
                                if (player.getItemInHand().getItemMeta().getLore().contains(ChatColor.GREEN + "Rainbow")) {
                                    Storage.snowballs.add(evt.getEntity());
                                }
                            }
                        }
                    }
                    if ((evt.getEntity().getShooter() instanceof Snowman) && ((Snowman) evt.getEntity().getShooter()).getCustomName() != null && ((Snowman) evt.getEntity().getShooter()).getCustomName().equals("brainiac94")) {
                        Storage.bangBalls.add((Snowball) evt.getEntity());
                    }
                }
            }
        }
    }

    @EventHandler // Remote tnt detonated
    public boolean onDetonate(final PlayerInteractEvent evt) throws Exception {
        Player player = evt.getPlayer();
        if ((evt.getAction() == Action.RIGHT_CLICK_BLOCK || evt.getAction() == Action.RIGHT_CLICK_AIR) && player.getItemInHand() != null) {
            if (player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().hasLore()) {
                List<String> lore = player.getItemInHand().getItemMeta().getLore();
                int ind = lore.indexOf(ChatColor.GREEN + "Remote Detonator");
                if (ind != -1 && lore.size() > 1) {
                    Block blk = evt.getClickedBlock();
                    if (blk != null) {
                        if (blk.getType().equals(TNT) && player.isSneaking()) {
                            blk.setType(NETHERRACK);
                            blk.setType(TNT);
                            if (Utilities.tryBreak(player, blk, false)) {
                                if (Storage.remoteTnt.containsKey(ChatColor.stripColor(lore.get(1)))) {
                                    Storage.remoteTnt.get(ChatColor.stripColor(lore.get(1))).add(blk);
                                } else {
                                    Storage.remoteTnt.put(ChatColor.stripColor(lore.get(1)), new ArrayList<Block>());
                                    Storage.remoteTnt.get(ChatColor.stripColor(lore.get(1))).add(blk);
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
                        if (Storage.remoteTnt.containsKey(ChatColor.stripColor(channel))) {
                            blocks.addAll(Storage.remoteTnt.get(ChatColor.stripColor(channel)));
                            Storage.remoteTnt.remove(ChatColor.stripColor(channel));
                        }
                    }
                    for (Block block : blocks) {
                        if (block.getType() == TNT) {
                            block.setType(AIR);
                            TNTPrimed ent = (TNTPrimed) block.getWorld().spawnEntity(block.getLocation(), EntityType.PRIMED_TNT);
                            ent.setYield(4);
                            ent.setFuseTicks(0);
                            if (Storage.explodingBlocks.containsKey(block)) {
                                if (Storage.explodingBlocks.get(block).params.get(0).contains("Firework TNT")) {
                                    Storage.explodingBlocks.get(block).explode(ent);
                                    ent.remove();
                                }
                            }
                        }
                    }
                    player.sendMessage(Storage.logo + " Detonated " + blocks.size() + " block(s)!");
                } else if (ind != -1 && lore.size() == 1) {
                    lore.add(ChatColor.GOLD + "" + RandomStringUtils.randomAlphabetic(8));
                    ItemMeta meta = player.getItemInHand().getItemMeta();
                    meta.setLore(lore);
                    player.getItemInHand().setItemMeta(meta);
                }
            }
        }
        return true;
    }

    @EventHandler // Stops Easter eggs from being pickef up
    public boolean onItemPickup(PlayerPickupItemEvent evt) {
        Item item = evt.getItem();
        if (Storage.eastereggs.containsKey(item)) {
            evt.setCancelled(true);
            ItemStack newitem = Storage.eastereggs.get(item);
            evt.getItem().setItemStack(newitem);
            Storage.eastereggs.remove(item);
        } else if (item.getItemStack().hasItemMeta() && item.getItemStack().getItemMeta().hasDisplayName()
                && item.getItemStack().getItemMeta().getDisplayName().equals(ChatColor.MAGIC + "Transient")) {
            // Kill items left after a crash
            evt.setCancelled(true);
            item.remove();
        }
        return true;
    }
    
    @EventHandler // Stops Easter eggs from being pickef up
    public boolean onItemPickup(InventoryPickupItemEvent evt) {
        Item item = evt.getItem();
        if(TaskItemTrails.trailItems.contains(evt.getItem())) {
            TaskItemTrails.trailItems.remove(evt.getItem());
            evt.getItem().remove();
            evt.setCancelled(true);
        }
        if (Storage.eastereggs.containsKey(item)) {
            evt.setCancelled(true);
            ItemStack newitem = Storage.eastereggs.get(item);
            evt.getItem().setItemStack(newitem);
            Storage.eastereggs.remove(item);
        } else if (item.getItemStack().hasItemMeta() && item.getItemStack().getItemMeta().hasDisplayName()
                && item.getItemStack().getItemMeta().getDisplayName().equals(ChatColor.MAGIC + "Transient")) {
            // Kill items left after a crash
            evt.setCancelled(true);
            item.remove();
        }
        return true;
    }

    @EventHandler // Player shoots color arrow
    public boolean onShoot(final EntityShootBowEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            ItemStack arrowShot = null;
            for (ItemStack is : player.getInventory().getContents()) {
                if (is != null && is.getType() == ARROW) {
                    arrowShot = is;
                    break;
                }
            }
            if (arrowShot != null && arrowShot.getItemMeta().hasLore()) {
                if (arrowShot.getItemMeta().getLore().size() == 2) {
                    String type = ChatColor.stripColor(arrowShot.getItemMeta().getLore().get(0));
                    Integer[] color;
                    if (type.equals("Color Arrow")) {
                        Arrow arrow = (Arrow) evt.getProjectile();
                        arrow.setCritical(false);
                        String info = arrowShot.getItemMeta().getLore().get(1);
                        info = ChatColor.stripColor(info);
                        info = info.replace("R: ", "").replace("G: ", "").replace("B: ", "").replace(" ", "");
                        String[] data = info.split(",");
                        int r, g, b;
                        try {
                            r = Integer.parseInt(data[0]);
                            g = Integer.parseInt(data[1]);
                            b = Integer.parseInt(data[2]);
                        } catch (NumberFormatException e) {
                            if (data[0].equals("-") || data[1].equals("-") || data[2].equals("-")) {
                                return true;
                            }
                            r = 0;
                            g = 0;
                            b = 0;
                        }
                        color = new Integer[]{r, g, b};
                        Storage.colorArrows.put((Projectile) evt.getProjectile(), color);
                    }
                }
            }
        }
        return true;
    }

    @EventHandler // Color arrow hits object
    public boolean onHit(final ProjectileHitEvent evt) {
        if (Storage.colorArrows.containsKey(evt.getEntity())) {
            boolean close = false;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getWorld().equals(evt.getEntity().getWorld())) {
                    if (p.getLocation().distance(evt.getEntity().getLocation()) < 28) {
                        close = true;
                    }
                }
            }
            final Integer[] colors = Storage.colorArrows.get(evt.getEntity());
            Storage.colorArrows.remove(evt.getEntity());
            if (!close) {
                FireworkEffect.Builder builder = FireworkEffect.builder();
                FireworkEffect effect = builder.flicker(false).trail(false).with(FireworkEffect.Type.BURST).withColor(Color.fromRGB(colors[0], colors[1], colors[2])).build();
                FireworkEffectPlayer.playFirework(evt.getEntity().getLocation(), effect);
            } else {
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.pyro, new Runnable() {
                    @Override
                    public void run() {
                        FireworkEffect.Builder builder = FireworkEffect.builder();
                        FireworkEffect effect = builder.flicker(false).trail(false).with(FireworkEffect.Type.BURST).withColor(Color.fromRGB(colors[0], colors[1], colors[2])).build();
                        FireworkEffectPlayer.playFirework(evt.getEntity().getLocation(), effect);
                    }
                }, 8);
                for (int i = 1000; i > 0; i -= 10) {
                    final float j = i;
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.pyro, new Runnable() {
                        @Override
                        public void run() {
                            float x, y, z;
                            float j1 = j;
                            for (int f = 0; f < 10; f++) {
                                Location loc = evt.getEntity().getLocation();
                                loc.setY(loc.getY() + (Math.sin(Math.toDegrees(f * Math.PI) / 2) * (j1 / 800)));
                                loc.setX(loc.getX() + Math.sin(Math.toRadians(j1 + f * 100)) * j1 / 330);
                                loc.setZ(loc.getZ() + Math.cos(Math.toRadians(j1 + f * 100)) * j1 / 330);
                                Color col = Color.fromRGB(Utilities.clamp(colors[0]), Utilities.clamp(colors[1]), Utilities.clamp(colors[2]));
                                loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, new Particle.DustOptions(col, 0.5f));
                            }
                        }
                    }, (int) ((1000 - i) / 100));
                }
            }
        }
        return true;
    }

    @EventHandler // Links to lore crafting (craftArrow/craftChromo)
    public void onCraft(InventoryDragEvent evt) {
        if (!evt.getInventory().getType().equals(InventoryType.WORKBENCH) || evt.isCancelled()) {
            return;
        }
        CraftingGUI.craftArrow(evt.getView(), evt.getInventorySlots(), (Player) evt.getWhoClicked(), false, false);
        CraftingGUI.craftChromo(evt.getView(), evt.getInventorySlots(), (Player) evt.getWhoClicked(), false);
        CraftingGUI.craftFireworkObject(evt.getView(), evt.getInventorySlots(), (Player) evt.getWhoClicked(), false);
    }

    @EventHandler // Links to lore crafting (craftArrow/craftChromo)
    public void onCraft(final InventoryClickEvent evt) {
        if (!evt.getInventory().getType().equals(InventoryType.WORKBENCH) || evt.isCancelled()) {
            return;
        }
        boolean canCraft = false;
        if (evt.getSlotType().equals(RESULT)) {
            canCraft = true;
        }
        Set<Integer> slot = new HashSet<>();
        slot.add(evt.getSlot());

        CraftingGUI.craftArrow(evt.getView(), slot, (Player) evt.getWhoClicked(), canCraft, evt.isShiftClick());
        CraftingGUI.craftChromo(evt.getView(), slot, (Player) evt.getWhoClicked(), canCraft);
        CraftingGUI.craftFireworkObject(evt.getView(), slot, (Player) evt.getWhoClicked(), canCraft);
    }

    @EventHandler // Firework Inventory
    public boolean onFireworkClick(InventoryClickEvent evt) {
        if (evt.getInventory().getName().contains(ChatColor.DARK_RED + "Firework")) {
            try {
                int page = Integer.parseInt(evt.getInventory().getName().split("/4")[0].split(" : ")[2]);
                int oldPage = page;
                if (evt.getSlot() == 53) {
                    page++;
                } else if (evt.getSlot() == 45) {
                    page--;
                }
                if (page < 1) {
                    page = 4;
                } else if (page > 4) {
                    page = 1;
                }
                if (oldPage != page) {
                    CraftingGUI.updatePage(page, (Player) evt.getWhoClicked());
                }
                if (evt.getInventory().getName().contains(ChatColor.DARK_RED + "Firework") && evt.getRawSlot() < 54) {
                    evt.setCancelled(true);
                    CraftingGUI.recieveClick(evt.getSlot(), page, (Player) evt.getWhoClicked(), evt.getInventory(), evt.isShiftClick());
                }
            } catch (Exception e) {
            }
        }
        return true;
    }

}
