package zedly.pyro;

import java.util.*;
import org.bukkit.*;
import static org.bukkit.Material.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
import zedly.particles.ParticleEffect;

public class HFEffects implements Runnable {

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

        //Color Arrows
        for (Projectile p : Storage.colorArrows.keySet()) {
            Integer[] colors = Storage.colorArrows.get(p);
            Color color = Color.fromRGB(Utilities.clamp(colors[0]), Utilities.clamp(colors[1]), Utilities.clamp(colors[2]));
            ParticleEffect.REDSTONE.display(null, p.getLocation(), color, 32, 0, 0, 0, 1, 1);
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
    }
}
