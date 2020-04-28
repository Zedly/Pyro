package zedly.pyro.projectiles;

import org.bukkit.FireworkEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.SmallFireball;
import zedly.pyro.FireworkEffectPlayer;
import zedly.pyro.Storage;

public class ProjectileMissile extends AdvancedProjectile {

    public static final FireworkEffect.Builder bu;

    public ProjectileMissile(SmallFireball sf) {
        super(sf);
    }

    @Override
    public void trail() {
        FireworkEffectPlayer.playFirework(sf.getLocation(), bu.build());
    }

    @Override
    public void impact() {
        sf.getLocation().getWorld().createExplosion(sf.getLocation().getX(), sf.getLocation().getY(), sf.getLocation().getZ(), 5, false, false);
        sf.remove();
    }

    static {
        bu = FireworkEffect.builder().withColor(org.bukkit.Color.fromRGB(Storage.RAINBOW_COLORS[1])).trail(true).with(FireworkEffect.Type.BALL);
    }
}
