package zedly.pyro.projectiles;

import org.bukkit.FireworkEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.SmallFireball;
import org.bukkit.util.Vector;
import zedly.pyro.FireworkEffectPlayer;
import zedly.pyro.Storage;

public class ProjectileSpark extends AdvancedProjectile {

    public static final FireworkEffect.Builder bu;
    private int s_tick;

    public ProjectileSpark(SmallFireball sf) {
        super(sf);
        s_tick = Storage.rnd.nextInt(6) + 12;
    }

    @Override
    public void trail() {
        FireworkEffectPlayer.playFirework(sf.getLocation(), bu.build());
        
        s_tick--;
        if (s_tick == 0) {
            impact();
            sf.remove();
        }
    }

    @Override
    public void impact() {
        sf.getLocation().getWorld().createExplosion(sf.getLocation().getX(), sf.getLocation().getY(), sf.getLocation().getZ(), 0, false, false);
        int sparks = Storage.rnd.nextInt(7) + 5;
        int color = Storage.rnd.nextInt(12);
        for (int i = 0; i < sparks; i++) {
            SmallFireball sf1 = (SmallFireball) sf.getWorld().spawnEntity(sf.getLocation(), EntityType.SMALL_FIREBALL);
            sf1.setIsIncendiary(false);
            sf1.setVelocity(new Vector(Storage.rnd.nextGaussian(), Storage.rnd.nextGaussian(), Storage.rnd.nextGaussian()));
            Storage.advancedProjectiles.put(sf1, new ProjectileSparkle(sf1, color));
        }
        sf.remove();
    }

    static {
        bu = FireworkEffect.builder().withColor(org.bukkit.Color.fromRGB(Storage.rainbowcolors[1])).with(FireworkEffect.Type.BALL);
    }
}
