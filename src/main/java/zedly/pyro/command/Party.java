/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.pyro.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import zedly.pyro.Storage;
import zedly.pyro.features.TaskPartyFirework;

/**
 *
 * @author Dennis
 */
public class Party extends PlayerCommand {

    @Override
    boolean onCommand(Player player, String[] args) {
        if (args.length == 1) {
            if (!player.hasPermission("pyro.partyglobal")) {
                player.sendMessage(Storage.logo + " You do not have permission to do this!");
                return true;
            }
            if (TaskPartyFirework.globalParty) {
                TaskPartyFirework.globalParty = false;
                TaskPartyFirework.partyPlayers.removeAll(Bukkit.getOnlinePlayers());
                player.sendMessage(Storage.logo + " Stopping the party, hiding the drugs.");
            } else {
                TaskPartyFirework.globalParty = true;
                TaskPartyFirework.partyPlayers.addAll(Bukkit.getOnlinePlayers());
                player.sendMessage(Storage.logo + " Let's get this party started!");
            }
            return true;
        }
        if (TaskPartyFirework.partyPlayers.contains(player)) {
            if (TaskPartyFirework.partyPlayers.size() == 1) {
                player.sendMessage(Storage.logo + " Nope, one is not a party...");
            } else {
                player.sendMessage(Storage.logo + " \"I have a thing to go do..\"");
            }
            TaskPartyFirework.partyPlayers.remove(player);
        } else {
            if (TaskPartyFirework.partyPlayers.isEmpty()) {
                player.sendMessage(Storage.logo + " One's a party! Right..?");
            } else {
                player.sendMessage(Storage.logo + " \"I was totally invited to this..\"");
            }
            TaskPartyFirework.partyPlayers.add(player);
        }
        return true;
    }

    @Override
    public String getSyntax() {
        return "/party (all)";
    }

    @Override
    public String getDescription() {
        return "Continuously launches some fireworks around you or everyone";
    }

}
