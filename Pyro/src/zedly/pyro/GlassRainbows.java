package zedly.pyro;

import java.util.*;
import org.bukkit.*;
import static org.bukkit.Material.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import zedly.pyro.annotations.EffectTask;
import zedly.pyro.enums.Frequency;

public class GlassRainbows {

	private static final LinkedList<Block> tempBlockList = new LinkedList<>();

	@EffectTask(Frequency.HIGH)
	public static void taskGlassRainbows() {
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
				Material[] materials = new Material[]{RED_STAINED_GLASS, ORANGE_STAINED_GLASS, YELLOW_STAINED_GLASS,
					LIME_STAINED_GLASS, BLUE_STAINED_GLASS, MAGENTA_STAINED_GLASS, PINK_STAINED_GLASS};
				for (int c = 0; c < 7; c++) {
					if (temp.getBlock().getRelative(0, -c, 0).getType() == AIR) {
						temp.getBlock().getRelative(0, -c, 0).setType(materials[c]);
						Storage.rainbowBlocks.put(temp.getBlock().getRelative(0, -c, 0), System.currentTimeMillis());
					}
				}
			}
		}
		Storage.snowballs.removeAll(snowballsTemp);

		// Remove Glass Rainbows
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
