/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.pyro.features;

import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import static org.bukkit.Material.ARROW;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import zedly.pyro.FireworkEffectPlayer;

/**
 *
 * @author Dennis
 */
public class ColorArrows extends FeatureClass {

    public static final ColorArrows INSTANCE = new ColorArrows();
    public static final HashMap<Projectile, Color> colorArrows = new HashMap<>();

    private ColorArrows() {
    }
    
    @Override
    public int getTaskFrequency() {
        return 1;
    }

    @Override // Run every tick
    public void run() {
        colorArrows.entrySet().forEach((ent) -> {
            Color color = ent.getValue();
            Particle.DustOptions dustOptions = new Particle.DustOptions(color, 1);
            ent.getKey().getLocation().getWorld().spawnParticle(Particle.REDSTONE, ent.getKey().getLocation(), 5, 0, 0, 0, 0, dustOptions, true);
        });
    }

    @EventHandler // Player shoots color arrow
    public boolean onShoot(final EntityShootBowEvent evt) {
        if (!(evt.getEntity() instanceof Player)) {
            return true;
        }
        Player player = (Player) evt.getEntity();
        ItemStack arrowShot = null;
        for (ItemStack is : player.getInventory().getContents()) {
            if (is != null && is.getType() == ARROW) {
                arrowShot = is;
                break;
            }
        }
        if (arrowShot != null && arrowShot.getItemMeta().hasLore()) {
            if (arrowShot.getItemMeta().getLore().size() == 2) {
                String type = ChatColor.stripColor(arrowShot.getItemMeta().getLore().get(0));
                int color;
                if (type.equals("Color Arrow")) {
                    Arrow arrow = (Arrow) evt.getProjectile();
                    arrow.setCritical(false);
                    String info = arrowShot.getItemMeta().getLore().get(1);
                    info = ChatColor.stripColor(info);
                    info = info.replace("R: ", "").replace("G: ", "").replace("B: ", "").replace(" ", "");
                    String[] data = info.split(",");
                    int r, g, b;
                    if (data[0].equals("-") || data[1].equals("-") || data[2].equals("-")) {
                        return true;
                    }
                    try {
                        r = Integer.parseInt(data[0]);
                        g = Integer.parseInt(data[1]);
                        b = Integer.parseInt(data[2]);
                    } catch (NumberFormatException e) {
                        r = 255;
                        g = 255;
                        b = 255;
                    }
                    color = (r << 16) + (g << 8) + b;
                    colorArrows.put((Projectile) evt.getProjectile(), Color.fromRGB(color));
                }
            }
        }
        return true;
    }

    @EventHandler // Color arrow hits object
    public boolean onHit(final ProjectileHitEvent evt) {
        if (!colorArrows.containsKey(evt.getEntity())) {
            return true;
        }
        Color color = colorArrows.get(evt.getEntity());
        colorArrows.remove(evt.getEntity());
        FireworkEffect.Builder builder = FireworkEffect.builder();
        FireworkEffect effect = builder.flicker(false).trail(false).with(FireworkEffect.Type.BURST).withColor(color).build();
        FireworkEffectPlayer.playFirework(evt.getEntity().getLocation(), effect);
        FireworkEffectPlayer.playDustSwirlReverse(evt.getEntity().getLocation(), color);
        return true;
    }
}
