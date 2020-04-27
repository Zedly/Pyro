/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.pyro.command;

import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import zedly.pyro.Storage;

/**
 *
 * @author Dennis
 */
public class CommandDelegator {

    private static final HashMap<String, Command> DELEGATIONS = new HashMap<>();

    public static boolean onCommand(CommandSender sender, String label, String[] args) {
        Command cmd = DELEGATIONS.get(label);
        if (cmd == null) {
            sender.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "Hey! " + ChatColor.GRAY + "Brainiac is a dumbass and forgot to register this command :(");
            return true;
        }
        boolean ret = cmd.onCommand(sender, args);
        return ret;
    }

    static {
        DELEGATIONS.put("colorarrow", new ColorArrow());
        DELEGATIONS.put("eegg", new EEgg());
        DELEGATIONS.put("eeggs", new EEggs());
        DELEGATIONS.put("firework", new Firework());
        DELEGATIONS.put("party", new Party());
        DELEGATIONS.put("rtnt", new RTNT());
        DELEGATIONS.put("rainboom", new Rainboom());
        DELEGATIONS.put("retrieveeggs", new RetrieveEggs());
    }
}
