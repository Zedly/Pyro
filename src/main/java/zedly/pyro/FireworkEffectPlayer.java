package zedly.pyro;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.server.v1_15_R1.EntityFireworks;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityStatus;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class FireworkEffectPlayer {

    private static boolean nmsDetected = false;

    private static final BiConsumer<Location, Color> DUST_PARTICLE_METHOD = (l, c) -> {
        Particle.DustOptions dustOptions = new Particle.DustOptions(c, 1);
        l.getWorld().spawnParticle(Particle.REDSTONE, l, 3, 0, 0, 0, 0, dustOptions, true);
    };
    private static final BiConsumer<Location, Color> MOB_PARTICLE_METHOD = (l, c) -> {
        l.getWorld().spawnParticle(Particle.SPELL_MOB, l, 0, c.getRed() / 255.0, c.getGreen() / 255.0, c.getBlue() / 255.0, 1);
    };
    private static final Function<Integer, Integer> SCHEDULE_FORWARD = (i) -> {
        return (1000 - i) / 100;
    };
    private static final Function<Integer, Integer> SCHEDULE_REVERSE = (i) -> {
        return i / 100;
    };

    public static void playFirework(Location loc, org.bukkit.FireworkEffect.Type type, org.bukkit.Color color, org.bukkit.Color fade, boolean trail, boolean twinkle) {
        FireworkEffect.Builder bu = FireworkEffect.builder();
        bu.withColor(color);
        bu.withFade(fade);
        bu.with(type);
        if (trail) {
            bu.withTrail();
        }
        if (twinkle) {
            bu.withFlicker();
        }
        playFirework(loc, bu.build());
    }

    public static void playFirework(Location loc, org.bukkit.FireworkEffect.Type type, org.bukkit.Color color, boolean trail, boolean twinkle) {
        FireworkEffect.Builder bu = FireworkEffect.builder();
        bu.withColor(color);
        bu.with(type);
        if (trail) {
            bu.withTrail();
        }
        if (twinkle) {
            bu.withFlicker();
        }
        playFirework(loc, bu.build());
    }

    public static void playFirework(Location loc, org.bukkit.FireworkEffect.Type type, List<org.bukkit.Color> color, boolean trail, boolean twinkle) {
        FireworkEffect.Builder bu = FireworkEffect.builder();
        bu.withColor(color);
        bu.with(type);
        if (trail) {
            bu.withTrail();
        }
        if (twinkle) {
            bu.withFlicker();
        }
        playFirework(loc, bu.build());
    }

    public static void playFirework(Location loc, org.bukkit.FireworkEffect.Type type, List<org.bukkit.Color> color, List<org.bukkit.Color> fade, boolean trail, boolean twinkle) {
        FireworkEffect.Builder bu = FireworkEffect.builder();
        bu.withColor(color);
        bu.withFade(fade);
        bu.with(type);
        if (trail) {
            bu.withTrail();
        }
        if (twinkle) {
            bu.withFlicker();
        }

        playFirework(loc, bu.build());
    }

    public static void playFirework(Location location, FireworkEffect... effects) {
        if (!nmsDetected) {
            return;
        }
        ItemStack is = new ItemStack(Material.FIREWORK_ROCKET, 1);
        FireworkMeta meta = (FireworkMeta) is.getItemMeta();
        meta.addEffects(effects);
        is.setItemMeta(meta);
        net.minecraft.server.v1_15_R1.ItemStack nmsIs = CraftItemStack.asNMSCopy(is);
        CustomEntityFirework_1_15_R1 firework = new CustomEntityFirework_1_15_R1(location, nmsIs);
        firework.perform();
    }

    public static void playDustSwirl(Location loc, Color color) {
        playSwirl(loc, DUST_PARTICLE_METHOD, color, SCHEDULE_FORWARD);
    }

    public static void playDustSwirlReverse(Location loc, Color color) {
        playSwirl(loc, DUST_PARTICLE_METHOD, color, SCHEDULE_REVERSE);
    }

    public static void playMobSwirl(Location loc, Color color) {
        playSwirl(loc, MOB_PARTICLE_METHOD, color, SCHEDULE_FORWARD);
    }

    public static void playPotionSwirlReverse(Location loc, Color color) {
        playSwirl(loc, MOB_PARTICLE_METHOD, color, SCHEDULE_REVERSE);
    }

    public static void playSwirl(Location loc, BiConsumer<Location, Color> spawnParticleMethod, Color color, Function<Integer, Integer> scheduleFunction) {
        for (int i = 1000; i > 0; i -= 10) {
            final float swirlTime = i;
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.pyro, () -> {
                for (int f = 0; f < 5; f++) {
                    Location l = loc.clone().add(Math.sin(Math.toRadians(swirlTime + f * 36)) * swirlTime / 500,
                            (swirlTime / 1000),
                            Math.cos(Math.toRadians(swirlTime + f * 36)) * swirlTime / 500);
                    spawnParticleMethod.accept(l, color);
                }
            }, scheduleFunction.apply(i));
        }
    }

    //CustomEntityFirework class by recon88: https://github.com/recon88/Instant-Fireworks/blob/master/src/CustomEntityFirework.java
    private static class CustomEntityFirework_1_15_R1 extends EntityFireworks {

        private final Player[] players = new Player[]{};
        private final Location location;
        private boolean gone = false;

        protected CustomEntityFirework_1_15_R1(Location loc, net.minecraft.server.v1_15_R1.ItemStack metaContainer) {
            super(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ(), metaContainer);
            Bukkit.getOnlinePlayers().toArray(players);
            this.a(0.25F, 0.25F);
            this.location = loc;
        }

        public boolean perform() {
            try {
                if ((((CraftWorld) location.getWorld()).getHandle()).addEntity(this)) {
                    setInvisible(true);
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void tick() {
            if (gone) {
                return;
            }
            gone = true;
            if (players != null && players.length > 0) {
                for (Player player : players) {
                    (((CraftPlayer) player).getHandle()).playerConnection.sendPacket(new PacketPlayOutEntityStatus(this, (byte) 17));
                }
            } else {
                world.broadcastEntityEffect(this, (byte) 17);
            }
            die();
        }
    }

    static {
        try {
            Class.forName("net.minecraft.server.v1_15_R1.EntityFireworks");
            nmsDetected = true;
            System.out.println(Storage.logo + ": Compatible NMS version detected");
        } catch (Exception e) {
            System.out.println(Storage.logo + ": NMS version not compatible!");
        }
    }
}
