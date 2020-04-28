package zedly.pyro.projectiles;

import org.bukkit.FireworkEffect;
import org.bukkit.entity.SmallFireball;
import zedly.pyro.FireworkEffectPlayer;
import zedly.pyro.Storage;

public class ProjectileSparkle extends AdvancedProjectile {

    public static final FireworkEffect.Builder bu;
    private final FireworkEffect.Builder bu1;

    public ProjectileSparkle(SmallFireball sf, int color) {
        super(sf);
        tick = 2 + Storage.rnd.nextInt(3);
        bu1 = FireworkEffect.builder()
                .withColor(org.bukkit.Color.fromRGB(Storage.RAINBOW_COLORS[color]))
                .with(FireworkEffect.Type.BALL_LARGE);
        sf.setVelocity(sf.getVelocity().multiply(1.3));
    }

    @Override
    public void trail() {
        FireworkEffectPlayer.playFirework(sf.getLocation(), bu.build());
        tick--;
        if (tick == 0) {
            impact();
        }
    }

    @Override
    public void impact() {
        FireworkEffectPlayer.playFirework(sf.getLocation(), bu1.build());
        sf.remove();
    }

    static {
        bu = FireworkEffect.builder().withColor(org.bukkit.Color.fromRGB(Storage.RAINBOW_COLORS[2])).trail(true).with(FireworkEffect.Type.BALL);
    }
}
