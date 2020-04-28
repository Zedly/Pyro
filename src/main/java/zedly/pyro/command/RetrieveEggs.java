/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.pyro.command;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import zedly.pyro.features.EasterEggs;
import zedly.pyro.Storage;

/**
 *
 * @author Dennis
 */
public class RetrieveEggs extends PlayerCommand {

    @Override
    boolean onCommand(Player player, String[] args) {
        for (Item itemEnt : EasterEggs.eastereggs.keySet()) {
            itemEnt.teleport(player);
        }
        player.sendMessage(Storage.logo + " " + EasterEggs.eastereggs.size() + " egg(s) returned!");
        return true;
    }

    @Override
    public String getSyntax() {
        return "/retrieveeggs";
    }

    @Override
    public String getDescription() {
        return "Brings back all undiscovered easter eggs";
    }

}
