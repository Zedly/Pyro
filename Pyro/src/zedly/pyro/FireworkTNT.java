package zedly.pyro;

import java.util.*;
import org.bukkit.*;
import static org.bukkit.Material.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
import zedly.pyro.Core.Storage;
import zedly.pyro.annotations.EffectTask;
import zedly.pyro.enums.Frequency;

public class FireworkTNT {

	@EffectTask(Frequency.HIGH)
	public static void taskFireworkTNT() {
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
			Set<TNTPrimed> ents1 = new HashSet<>(ents);
			Set<Block> kill = new HashSet<>();
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
	}

}
