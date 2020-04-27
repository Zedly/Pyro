/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.pyro.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import zedly.pyro.Storage;

/**
 *
 * @author Dennis
 */
public class EEggs extends Command {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        sender.sendMessage(Storage.logo + " " + Storage.eastereggs.size() + " egg(s) remaining!");
        return true;
    }

    @Override
    public String getSyntax() {
        return "/eeggs";
    }

    @Override
    public String getDescription() {
        return "List how many easter eggs / christmas presents are left";
    }

}
