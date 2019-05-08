package zedly.pyro;

import org.bukkit.*;
import org.bukkit.entity.*;
import zedly.pyro.annotations.EffectTask;
import zedly.pyro.enums.Frequency;

public class ColorArrows {

	@EffectTask(Frequency.HIGH)
	public static void taskColorArrows() {
		for (Projectile p : Storage.colorArrows.keySet()) {
			Integer[] colors = Storage.colorArrows.get(p);
			Color color =
				Color.fromRGB(Utilities.clamp(colors[0]), Utilities.clamp(colors[1]), Utilities.clamp(colors[2]));
			p.getLocation().getWorld().spawnParticle(Particle.REDSTONE, p.getLocation(), 1,
				new Particle.DustOptions(color, 0.5f));
		}
	}

}
