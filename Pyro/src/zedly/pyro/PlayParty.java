package zedly.pyro;

import org.bukkit.*;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;


public class PlayParty implements Runnable {

    @Override
    public void run() {
        for (final Player player : Storage.partyPlayers) {
            int a = Storage.rnd.nextInt(60) - 30;
            int b = Storage.rnd.nextInt(60) - 30;
            final int d = Storage.rnd.nextInt(30) - 15;
            final int e = Storage.rnd.nextInt(30) - 15;
            final int f = Storage.rnd.nextInt(25);
            int x = (int) player.getLocation().getX() + a;
            int y = player.getWorld().getHighestBlockYAt(player.getLocation().add(a, 0, b));
            int z = (int) player.getLocation().getZ() + b;
            final int p = Storage.rnd.nextInt(10);
            if (p == 2) {
                Firework fw = (Firework) player.getWorld().spawnEntity(new Location(player.getWorld(), x, y, z), EntityType.FIREWORK);
                FireworkMeta fm = fw.getFireworkMeta();
                fm.setPower(Storage.rnd.nextInt(2) + 1);
                Builder bu = FireworkEffect.builder();
                bu = bu.withColor(Color.fromRGB(Storage.rainbowcolors[Storage.rnd.nextInt(11)]));
                if (Storage.rnd.nextBoolean()) {
                    bu = bu.withFade(Color.fromRGB(Storage.rainbowcolors[Storage.rnd.nextInt(11)]));
                }
                bu = bu.flicker(Storage.rnd.nextBoolean());
                bu = bu.trail(Storage.rnd.nextBoolean());
                bu = bu.with(FireworkEffect.Type.values()[Storage.rnd.nextInt(5)]);
                fm.addEffect(bu.build());
                fw.setFireworkMeta(fm);
            }
            final int colors = Storage.rainbowcolors[Storage.rnd.nextInt(11)];
            for (int i = 1000; i > 0; i -= 10) {
                final float j = i;
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.pyro, new Runnable() {
                    @Override
                    public void run() {
                        Location loc = player.getLocation().add(new Vector(d, f, e));
                        float x, y, z;
                        float j1 = j;
                        for (int f = 0; f < 10; f++) {
                            loc.setY(loc.getY() + (Math.sin(Math.toDegrees(f * Math.PI) / 2) * (j1 / 800)));
                            if (p == 9) {
                                loc.setX(loc.getX() + Math.sin(Math.toRadians((j1 + f) * 100)) * j1 / 330);
                                loc.setZ(loc.getZ() + Math.cos(Math.toRadians((j1 + f) * 100)) * j1 / 330);
                                ParticleEffect.SPELL_MOB.display(null, loc, Color.fromRGB(colors), 32, 0, 0, 0, 1, 1);
                            } else if (p == 8) {
                                loc.setX(loc.getX() + Math.sin(Math.toRadians(j1 + f * 100)) * j1 / 330);
                                loc.setZ(loc.getZ() + Math.cos(Math.toRadians(j1 + f * 100)) * j1 / 330);
                                ParticleEffect.REDSTONE.display(null, loc, Color.fromRGB(colors), 32, 0, 0, 0, 1, 1);
                            }
                        }
                    }
                }, (int) ((1000 - i) / 100));
            }
            double gX = Storage.rnd.nextGaussian() * 10;
            double gY = Math.abs(Storage.rnd.nextGaussian()) * 10;
            double gZ = Storage.rnd.nextGaussian() * 10;
            if (Math.pow(gX, 2) + Math.pow(gY, 2) + Math.pow(gZ, 2) > 10) {
                ParticleEffect.NOTE.display(5, 5, 5, 1f, 10, new Location(player.getWorld(), player.getLocation().getX() + gX, player.getLocation().getY() + gY, player.getLocation().getZ() + gZ), 32);
            }
        }
    }
}
