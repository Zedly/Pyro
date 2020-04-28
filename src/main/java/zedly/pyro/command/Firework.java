/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.pyro.command;

import org.bukkit.entity.Player;
import zedly.pyro.CraftedFirework;
import zedly.pyro.features.CraftingGUI;
import zedly.pyro.Storage;

/**
 *
 * @author Dennis
 */
public class Firework extends PlayerCommand {

    @Override
    boolean onCommand(Player player, String[] args) {
        CraftingGUI.inventories.put((Player) player, new CraftedFirework());
        CraftingGUI.updatePage(1, (Player) player);
        return true;
    }

    @Override
    public String getSyntax() {
        return "/firework";
    }

    @Override
    public String getDescription() {
        return "A graphical interface for crfeating firework items";
    }

}
