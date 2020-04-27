package zedly.pyro.projectiles;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.SmallFireball;
import org.bukkit.util.Vector;
import zedly.pyro.FireworkEffectPlayer;
import zedly.pyro.Storage;

public class ProjectileRepulsor extends AdvancedProjectile {

    public static final FireworkEffect.Builder bu;

    public ProjectileRepulsor(SmallFireball sf) {
        super(sf);
    }

    public void trail() {
        FireworkEffectPlayer.playFirework(sf.getLocation(), bu.build());
    }

    public void impact() {
        Location epicenter = sf.getLocation().add(new Vector(0, -2.5, 0));
        for (Entity ent : sf.getNearbyEntities(10, 5, 10)) {
            Vector dv = ent.getLocation().subtract(epicenter).toVector();
            dv.multiply(10 / (dv.length() * dv.length() + 0.5));
            ent.setVelocity(dv);
        }
        sf.getWorld().createExplosion(sf.getLocation(), 0);
        sf.remove();
    }

    static {
        bu = FireworkEffect.builder().withColor(org.bukkit.Color.fromRGB(Storage.rainbowcolors[2])).trail(true).with(FireworkEffect.Type.BALL);
    }
}
