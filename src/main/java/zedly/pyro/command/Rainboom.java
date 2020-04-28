/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.pyro.command;

import org.bukkit.entity.Player;
import zedly.pyro.Storage;

/**
 *
 * @author Dennis
 */
public class Rainboom extends PlayerCommand {

    @Override
    boolean onCommand(Player player, String[] args) {
        if (isEnabledFor(player)) {
            disableFor(player);
            player.sendMessage(Storage.logo + " Rainboom disabled!");
        } else {
            enableFor(player);
            player.sendMessage(Storage.logo + " Rainboom enabled!");
        }
        return true;
    }

    @Override
    public String getSyntax() {
        return "/rainboom";
    }

    @Override
    public String getDescription() {
        return "Produces a colorful firework trail when you fly";
    }
    
    public static boolean isEnabledFor(Player player) {
        return zedly.pyro.features.Rainboom.rainboomPlayers.contains(player);
    }

    public static void enableFor(Player player) {
        zedly.pyro.features.Rainboom.rainboomPlayers.add(player);
    }

    public static void disableFor(Player player) {
        zedly.pyro.features.Rainboom.rainboomPlayers.remove(player);
    }
}
