/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.pyro.features;

import java.util.HashSet;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import zedly.pyro.FireworkEffectPlayer;
import zedly.pyro.Storage;

/**
 *
 * @author Dennis
 */
public class Rainboom extends FeatureClass {

    public static final Rainboom INSTANCE = new Rainboom();
    public static final HashSet<Player> rainboomPlayers = new HashSet<>();
    private int tick = 0;

    private Rainboom() {
    }

    @Override
    public int getTaskFrequency() {
        return 2;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent evt) {
        if (!rainboomPlayers.contains(evt.getPlayer()) || !evt.getPlayer().isFlying() || evt.getFrom().distanceSquared(evt.getTo()) < 0.1) {
            return;
        }
        FireworkEffect.Builder bu = FireworkEffect.builder();
        bu = bu.withColor(org.bukkit.Color.fromRGB(Storage.RAINBOW_COLORS[tick++ % 12]));
        bu = bu.trail(true);
        bu = bu.with(FireworkEffect.Type.BALL);
        FireworkEffectPlayer.playFirework(evt.getFrom().add(new Vector(0, 1, 0)), bu.build());
    }
}
