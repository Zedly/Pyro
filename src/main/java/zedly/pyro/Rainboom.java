package zedly.pyro;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import java.util.*;
import org.bukkit.*;

public class Rainboom implements Runnable {

    private static final int[] colors = {0xFF0000, 0xFF8800, 0xFFFF00, 0x88FF00, 0x00FF00, 0x00FF88, 0x00FFFF, 0x0088FF, 0x0000FF, 0x8800FF, 0xFF00FF, 0xFF0088};
    private static final HashMap<Player, Rainboom> rainbooms = new HashMap<>();

    private final Player player;
    private Location loc;
    private int id;
    private int tick;

    public static boolean isEnabledFor(Player player) {
        return rainbooms.containsKey(player);
    }

    public static void enableFor(Player player) {
        if (!rainbooms.containsKey(player)) {
            Rainboom pr = new Rainboom(player);
            int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Storage.pyro, pr, 0, 2);
            pr.identify(id);
            rainbooms.put(player, pr);
        }
    }

    public static void disableFor(Player player) {
        if (rainbooms.containsKey(player)) {
            rainbooms.get(player).stop();
            rainbooms.remove(player);
        }
    }

    private Rainboom(Player player) {
        this.player = player;
        this.loc = player.getLocation().clone();
    }
    
    @Override
    public void run() {
        if (!player.isOnline()) {
            Bukkit.getScheduler().cancelTask(id);
            return;
        }
        if (!player.isFlying() || player.getLocation().distance(loc) <= 0.25) {
        } else {
            FireworkEffect.Builder bu = FireworkEffect.builder();
            bu = bu.withColor(org.bukkit.Color.fromRGB(colors[tick++ % 12]));
            bu = bu.trail(true);
            bu = bu.with(FireworkEffect.Type.BALL);
            try {
                FireworkEffectPlayer.playFirework(player.getLocation().add(new Vector(0, 1, 0)), bu.build());
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("FireworkContrail error! Cancelling firework!");
                stop();
            }
            loc = player.getLocation().clone();
        }
    }
    
    private void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }

    private synchronized void identify(int id) {
        this.id = id;
    }
}
