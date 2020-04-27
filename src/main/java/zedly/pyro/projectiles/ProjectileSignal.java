package zedly.pyro.projectiles;

import org.bukkit.FireworkEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.SmallFireball;
import zedly.pyro.FireworkEffectPlayer;
import zedly.pyro.Storage;

public class ProjectileSignal extends AdvancedProjectile {

    public static FireworkEffect.Builder bu;

    public ProjectileSignal(SmallFireball sf) {
        super(sf);
        sf.setVelocity(sf.getVelocity().multiply(3));
    }

    @Override
    public void trail() {
        if (tick == 8) {
            impact();
        }
    }

    @Override
    public void impact() {
        FireworkEffectPlayer.playFirework(sf.getLocation(), bu.build());
        sf.remove();
    }

    static {
        bu = FireworkEffect.builder().withColor(org.bukkit.Color.fromRGB(Storage.rainbowcolors[0])).trail(true).with(FireworkEffect.Type.BALL_LARGE);
    }
}
