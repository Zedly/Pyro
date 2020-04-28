package zedly.pyro.features;

import java.util.HashSet;
import java.util.function.BiFunction;
import org.bukkit.*;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;
import zedly.pyro.FireworkEffectPlayer;
import zedly.pyro.Storage;

public class TaskPartyFirework extends FeatureClass {

    public static final TaskPartyFirework INSTANCE = new TaskPartyFirework();
    public static boolean globalParty = false;
    public static final HashSet<Player> partyPlayers = new HashSet<>();

    private TaskPartyFirework() {
    }
    
    @Override
    public int getTaskFrequency() {
        return 3;
    }

    @Override
    public void run() {
        for (final Player player : globalParty ? Bukkit.getOnlinePlayers() : partyPlayers) {
            final Location origin = player.getLocation();
            final int randFireworkType = Storage.rnd.nextInt(10);

            // Launch some fireworks
            if (randFireworkType == 2) {
                Location fireworkSpawnLoc = randomSquareLocAround(origin, 30);
                Firework fw = (Firework) player.getWorld().spawnEntity(fireworkSpawnLoc, EntityType.FIREWORK);
                FireworkMeta fm = fw.getFireworkMeta();
                fm.setPower(Storage.rnd.nextInt(2) + 1);
                Builder bu = FireworkEffect.builder();
                bu.withColor(Color.fromRGB(Storage.RAINBOW_COLORS[Storage.rnd.nextInt(11)]))
                        .flicker(Storage.rnd.nextBoolean())
                        .trail(Storage.rnd.nextBoolean())
                        .with(FireworkEffect.Type.values()[Storage.rnd.nextInt(5)]);
                if (Storage.rnd.nextBoolean()) {
                    bu.withFade(Color.fromRGB(Storage.RAINBOW_COLORS[Storage.rnd.nextInt(11)]));
                }

                fm.addEffect(bu.build());
                fw.setFireworkMeta(fm);
            }

            // Make some potion and dust swirls
            if (randFireworkType < 2) {
                final int randomColor = Storage.RAINBOW_COLORS[Storage.rnd.nextInt(Storage.RAINBOW_COLORS.length)];
                final int yOffset = Storage.rnd.nextInt(25);
                Location swirlLoc = randomSquareLocAround(origin, 15).add(0, yOffset, 0);
                if (Storage.rnd.nextBoolean()) {
                    FireworkEffectPlayer.playDustSwirl(swirlLoc, Color.fromRGB(randomColor));
                } else {
                    FireworkEffectPlayer.playMobSwirl(swirlLoc, Color.fromRGB(randomColor));
                }
            }

            // Make some musical note particles
            int randomNoteColor = Storage.rnd.nextInt(24);
            Location particleLoc = randomGaussianLocationAround(origin, 10, 3);
            particleLoc.getWorld().spawnParticle(Particle.NOTE, particleLoc, 1, randomNoteColor / 24D, 0, 0, 1, null, true);
        }
    }

    private static Location randomGaussianLocationAround(Location origin, int minRadius, int maxRadius) {
        double gX = Storage.rnd.nextGaussian() * maxRadius;
        double gY = Math.abs(Storage.rnd.nextGaussian()) * maxRadius;
        double gZ = Storage.rnd.nextGaussian() * maxRadius;
        if (gX * gX + gY * gY + gZ * gZ > minRadius * minRadius) {
            return origin.add(new Vector(gX, gY, gZ));
        } else {
            return randomGaussianLocationAround(origin, minRadius, maxRadius);
        }
    }

    private static Location randomSquareLocAround(Location origin, int radius) {
        int xOffset = Storage.rnd.nextInt(radius * 2) - radius;
        int zOffset = Storage.rnd.nextInt(radius * 2) - radius;
        int x = origin.getBlockX() + xOffset;
        int z = origin.getBlockZ() + zOffset;
        int y = origin.getWorld().getHighestBlockYAt(x, z);
        return new Location(origin.getWorld(), x, y, z);
    }

}
