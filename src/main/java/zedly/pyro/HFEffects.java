package zedly.pyro;

import java.util.*;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.*;
import static org.bukkit.Material.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.Vector;
import zedly.particles.ParticleEffect;
import zedly.particles.ParticleEffectOld;

public class HFEffects implements Runnable {

    private final LinkedList<Player> tempPlayerList = new LinkedList<>();
    private final LinkedList<Block> tempBlockList = new LinkedList<>();
    
    @Override
    public void run() {
        //Firework TNT
        HashSet<Block> blox = new HashSet<>();
        for (Block block : Storage.explodingBlocks.keySet()) {
            if (block.getType() != TNT && block.getType() != NETHERRACK) {
                blox.add(block);
            }
        }
        if (!blox.isEmpty()) {
            Collection<TNTPrimed> ents = new HashSet<>();
            for (World world : Bukkit.getServer().getWorlds()) {
                ents.addAll(world.getEntitiesByClass(org.bukkit.entity.TNTPrimed.class));
            }
            ents.removeAll(Storage.tntEntities);
            HashSet<TNTPrimed> ents1 = new HashSet<>();
            HashSet<Block> kill = new HashSet<>();
            ents1.addAll(ents);
            for (TNTPrimed tnt : ents) {
                Location loc = tnt.getLocation().add(new Vector(-0.5, -0.5, -0.5));
                for (Block block : Storage.explodingBlocks.keySet()) {
                    if (loc.distance(block.getLocation()) <= 0.6) {
                        Storage.explodingEntities.put(tnt.getEntityId(), Storage.explodingBlocks.get(block));
                        kill.add(block);
                    }
                }
            }
            for (Block block : Storage.explodingBlocks.keySet()) {
                if (block.getType() != TNT) {
                    kill.add(block);
                }
            }
            for (Block block : kill) {
                Storage.explodingBlocks.remove(block);
            }
            Storage.tntEntities.clear();
            Storage.tntEntities.addAll(ents1);
        }
        //Chromatic Armor
        tempPlayerList.clear();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            boolean b = false;
            for (ItemStack stk : player.getInventory().getArmorContents()) {
                if (stk != null) {
                    if (ArrayUtils.contains(Storage.leather, stk.getType())) {
                        if (stk.getItemMeta().hasLore()) {
                            if (stk.getItemMeta().getLore().get(0).contains(ChatColor.GREEN + "Chromatic Armor") && !stk.getItemMeta().getLore().get(0).contains("Not Configured")) {
                                b = true;
                            }
                        }
                    }
                }
            }
            if (b) {
                if (!Storage.chromoPlayers.containsKey(player)) {
                    Storage.chromoPlayers.put(player, 0);
                } else {
                    Storage.chromoPlayers.put(player, Storage.chromoPlayers.get(player) + 1);
                }
            } else {
                tempPlayerList.add(player);
            }
            if (!player.isOnline()) {
                tempPlayerList.add(player);
            }
        }
        for (Player p : tempPlayerList) {
            Storage.chromoPlayers.remove(p);
        }
        for (Player player : Storage.chromoPlayers.keySet()) {
            int counter = Storage.chromoPlayers.get(player);
            for (ItemStack stk : player.getInventory().getArmorContents()) {
                if (stk != null && stk.hasItemMeta()) {
                    if (stk.getItemMeta().hasLore()) {
                        if (stk.getItemMeta().getLore().size() > 1) {
                            if (ArrayUtils.contains(Storage.leather, stk.getType())) {
                                List<String> lore = stk.getItemMeta().getLore();
                                if (lore.get(0).contains(ChatColor.GREEN + "Chromatic Armor") && !lore.get(0).contains("Not Configured")) {
                                    stk.setDurability((short) 0);
                                    LeatherArmorMeta meta = (LeatherArmorMeta) stk.getItemMeta();
                                    int[] color;
                                    try {
                                        double[] params = Utilities.parseParameters(ChatColor.stripColor(lore.get(1)));
                                        if (ArrayUtils.contains(Storage.colors, ChatColor.stripColor(lore.get(0).toLowerCase().split(": ")[1].replace(" ", "_")))) {
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
                                        if (stk.getType().equals(LEATHER_HELMET)) {
                                            player.getInventory().setHelmet(stk);
                                        } else if (stk.getType().equals(LEATHER_CHESTPLATE)) {
                                            player.getInventory().setChestplate(stk);
                                        } else if (stk.getType().equals(LEATHER_LEGGINGS)) {
                                            player.getInventory().setLeggings(stk);
                                        } else if (stk.getType().equals(LEATHER_BOOTS)) {
                                            player.getInventory().setBoots(stk);
                                        }
                                    } catch (Exception e) {
                                        lore.clear();
                                        lore.add(0, ChatColor.GREEN + "Chromatic Armor: " + ChatColor.GOLD + "Not Configured");
                                        lore.add(1, ChatColor.GRAY + "Not Configured");
                                        meta.setLore(lore);
                                        meta.setLore(lore);
                                        stk.setItemMeta(meta);
                                        return;
                                    }
                                    if ((player.isFlying() || player.isSprinting()) && Storage.rnd.nextBoolean() && !Storage.vanishedPlayers.contains(player)) {
                                        Location loc = player.getLocation().clone();
                                        loc.subtract(player.getLocation().getDirection());
                                        float i = 0;
                                        switch (stk.getType()) {
                                            case LEATHER_HELMET:
                                                i = 1.75f;
                                                break;
                                            case LEATHER_CHESTPLATE:
                                                i = 1.25f;
                                                break;
                                            case LEATHER_LEGGINGS:
                                                i = .83f;
                                                break;
                                            case LEATHER_BOOTS:
                                                i = .38f;
                                                break;
                                        }
                                        loc.setY(loc.getY() + i + (Storage.rnd.nextFloat() / 4) * (Storage.rnd.nextInt(2) * 2 - 1));
                                        loc.setX(loc.getX() + (Storage.rnd.nextFloat() / 4) * (Storage.rnd.nextInt(2) * 2 - 1));
                                        loc.setZ(loc.getZ() + (Storage.rnd.nextFloat() / 4) * (Storage.rnd.nextInt(2) * 2 - 1));
                                        Color col = Color.fromRGB(Utilities.clamp(color[0]), Utilities.clamp(color[1]), Utilities.clamp(color[2]));
                                        if (Bukkit.getVersion().contains("1.10")) {
                                            ParticleEffect.REDSTONE.display(null, loc, col, 32, 0, 0, 0, 1, 1);
                                        } else {
                                            ParticleEffectOld.REDSTONE.display(new ParticleEffectOld.OrdinaryColor(col), loc, 32);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //Color Arrows
        for (Projectile p : Storage.colorArrows.keySet()) {
            Integer[] colors = Storage.colorArrows.get(p);
            Color color = Color.fromRGB(Utilities.clamp(colors[0]), Utilities.clamp(colors[1]), Utilities.clamp(colors[2]));
            if (Bukkit.getVersion().contains("1.10")) {
                ParticleEffect.REDSTONE.display(null, p.getLocation(), color, 32, 0, 0, 0, 1, 1);
            } else {
                ParticleEffectOld.REDSTONE.display(new ParticleEffectOld.OrdinaryColor(color), p.getLocation(), 32);
            }
        }
        //Create Glass Rainbows
        HashSet<Entity> snowballsTemp = new HashSet<>();
        for (Entity ent : Storage.snowballs) {
            if (ent.isDead()) {
                snowballsTemp.add(ent);
            } else {
                Location temp = ent.getLocation().clone();
                float direction = ent.getLocation().getYaw();
                if (direction < 0) {
                    direction += 360;
                }
                direction %= 360;
                int i = (int) ((direction + 8) / 22.5);
                switch (i) {
                    case 2:
                        temp.setX(temp.getX() - 1);
                        temp.setZ(temp.getZ() - 1);
                        break;
                    case 3:
                    case 4:
                    case 5:
                        temp.setX(temp.getX() - 1);
                        break;
                    case 6:
                        temp.setX(temp.getX() - 1);
                        temp.setZ(temp.getZ() + 1);
                        break;
                    case 7:
                    case 8:
                    case 9:
                        temp.setZ(temp.getZ() + 1);
                        break;
                    case 10:
                        temp.setX(temp.getX() + 1);
                        temp.setZ(temp.getZ() + 1);
                        break;
                    case 11:
                    case 12:
                    case 13:
                        temp.setX(temp.getX() + 1);
                        break;
                    case 14:
                        temp.setX(temp.getX() + 1);
                        temp.setZ(temp.getZ() - 1);
                        break;
                    default:
                        temp.setZ(temp.getZ() - 1);
                        break;
                }
                byte[] bt = new byte[]{14, 1, 4, 5, 11, 2, 6};
                for (int c = 0; c < 7; c++) {
                    if (temp.getBlock().getRelative(0, -c, 0).getType() == AIR) {
                        temp.getBlock().getRelative(0, -c, 0).setType(STAINED_GLASS);
                        temp.getBlock().getRelative(0, -c, 0).setData(bt[c]);
                        Storage.rainbowBlocks.put(temp.getBlock().getRelative(0, -c, 0), System.currentTimeMillis());
                    }
                }
            }
        }
        Storage.snowballs.removeAll(snowballsTemp);
        //Remove Glass Rainbows
        tempBlockList.clear();
        for (Map.Entry<Block, Long> entry : Storage.rainbowBlocks.entrySet()) {
            if (System.currentTimeMillis() - entry.getValue() > 10000) {
                entry.getKey().setType(AIR);
                tempBlockList.add(entry.getKey());
            }
        }
        for (Block block : tempBlockList) {
            Storage.rainbowBlocks.remove(block);
        }
        //Remove Item Trails
        Iterator dropIt = Storage.dropItem.iterator();
        while (dropIt.hasNext()) {
            Item item = (Item) dropIt.next();
            if (item.getTicksLived() >= 100) {
                item.remove();
                dropIt.remove();
            }
        }
    }
}
