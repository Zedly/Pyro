package zedly.pyro;

import java.util.*;
import org.bukkit.*;
import static org.bukkit.GameMode.*;
import static org.bukkit.Material.*;
import org.bukkit.block.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

public class SignMechanics implements Listener {

    public static final SignMechanics INSTANCE = new SignMechanics();
    private static final HashMap<Block, Integer> SIGN_POWER_CACHE = new HashMap<>();
    public static final HashSet<Block> blockedSigns = new HashSet<>();

    private SignMechanics() {
    }
    
    @EventHandler // Firework sign created
    public boolean onSignChange(SignChangeEvent evt) {
        if (blockedSigns.contains(evt.getBlock())) {
            evt.setCancelled(true);
            blockedSigns.remove(evt.getBlock());
            return true;
        }
        if (evt.getLine(0).equalsIgnoreCase("[firework]") && evt.getPlayer().hasPermission("pyro.firework")) {
            evt.setLine(0, ChatColor.DARK_BLUE + "[Firework]");
        }
        return true;
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
        }
    }

    @EventHandler(priority = EventPriority.MONITOR) // Firework Sign Broken
    public boolean onBreak(BlockBreakEvent evt) {
        if (!evt.isCancelled()) {
            if (evt.getPlayer().getGameMode() != CREATIVE) {
                if (Utilities.isSign(evt.getBlock())) {
                    ItemStack stk = new ItemStack(OAK_SIGN);
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
        }
        return true;
    }

    @EventHandler(priority = EventPriority.LOWEST) // Firework Sign Placed
    public boolean onPlace(BlockPlaceEvent evt) {
        ItemStack is = evt.getItemInHand();
        if (!evt.isCancelled() && is != null
                && is.hasItemMeta()
                && is.getItemMeta().hasLore()) {
            if (Utilities.isMaterialSign(is.getType())
                    && evt.getPlayer().hasPermission("pyro.item.fireworksign")) {
                Block block = evt.getBlockPlaced();
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
                    evt.getPlayer().closeInventory();
                    blockedSigns.add(block);
                }
            }
        }
        return true;
    }
}
