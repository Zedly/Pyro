package zedly.pyro;

import java.util.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import static org.bukkit.Material.*;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.util.Vector;

public class CommandProcessor {

    public static void run(CommandSender sender, Command command, String commandlabel, String[] args) {
        if (!(sender instanceof Player)) {
            return;
        }
        switch (commandlabel.toLowerCase()) {
            case "color": {
                if (!sender.hasPermission("pyro.colorarrow")) {
                    sender.sendMessage(Storage.logo + " You do not have permission to do this!");
                    return;
                }
                if (args.length < 1) {
                    sender.sendMessage(Storage.logo + " /color <red [0..255]> <green [0..255]> <blue [0..255]>\n/color <color name>\n" + Storage.colorString);
                } else {
                    int[] color = new int[]{0, 0, 0};
                    try {
                        color[0] = Math.min(Math.abs(Integer.parseInt(args[0])), 255);
                        color[1] = Math.min(Math.abs(Integer.parseInt(args[1])), 255);
                        color[2] = Math.min(Math.abs(Integer.parseInt(args[2])), 255);
                    } catch (Exception e) {
                        if (ArrayUtils.contains(Storage.FW_COLOR_FRIENDLY_NAMES, args[0].toLowerCase())) {
                            color = Storage.FW_COLOR_RGB_RICH[ArrayUtils.indexOf(Storage.FW_COLOR_FRIENDLY_NAMES, args[0].toLowerCase())];
                        } else {
                            sender.sendMessage(Storage.logo + " /color <red [0..255]> <green [0..255]> <blue [0..255]>\n/color <color name>\n" + Storage.colorString);
                            return;
                        }
                    }
                    Player player = (Player) sender;
                    if (player.getInventory().getItemInMainHand().getType().equals(ARROW)) {
                        ItemStack stk = player.getInventory().getItemInMainHand();
                        ItemMeta meta = stk.getItemMeta();
                        List<String> lore = new ArrayList<>();
                        lore.add(ChatColor.GOLD + "Color Arrow");
                        lore.add(ChatColor.RED + "R: " + color[0] + ChatColor.WHITE + ", " + ChatColor.GREEN + "G: " + color[1] + ChatColor.WHITE + ", " + ChatColor.BLUE + "B: " + color[2]);
                        meta.setLore(lore);
                        stk.setItemMeta(meta);
                        player.sendMessage(Storage.logo + " Color arrow created!");
                    } else {
                        player.sendMessage(Storage.logo + " You need to be holding an arrow!");
                    }
                }
                break;
            }
            case "remote": {
                if (!sender.hasPermission("pyro.remote")) {
                    sender.sendMessage(Storage.logo + " You do not have permission to do this!");
                    return;
                }
                if (args.length < 2) {
                    sender.sendMessage(Storage.logo + " /remote <add/del> <channel name>");
                    return;
                }
                Player player = (Player) sender;
                ItemStack is = player.getInventory().getItemInMainHand();
                if (is.getType() != TNT && is.getType() != NAME_TAG && is.getType() != OAK_SIGN) {
                    player.sendMessage(Storage.logo + " You need to be holding TNT, a Name Tag, or a Sign!");
                    return;
                }
                ItemMeta meta = is.getItemMeta();
                List<String> lore;
                if (meta.hasLore()) {
                    if (meta.getLore().get(0).contains(ChatColor.GREEN + "Remote")) {
                        lore = meta.getLore();
                        lore.remove(0);
                    } else {
                        lore = new LinkedList<>();
                    }
                } else {
                    lore = new LinkedList<>();
                }
                if (args[0].equalsIgnoreCase("add") && !lore.contains(ChatColor.GOLD + args[1])) {
                    if (is.getType() == OAK_SIGN && lore.size() > 2) {
                        lore.remove(2);
                    }
                    lore.add(0, ChatColor.GOLD + args[1]);
                    player.sendMessage(Storage.logo + " Added remote channel!");
                } else if (args[0].equalsIgnoreCase("del") && lore.contains(ChatColor.GOLD + args[1])) {
                    lore.remove(ChatColor.GOLD + args[1]);
                    player.sendMessage(Storage.logo + " Removed remote channel!");
                }
                if (is.getType() == TNT) {
                    lore.add(0, ChatColor.GREEN + "Remote Controlled");
                } else if (is.getType().equals(NAME_TAG)) {
                    lore.add(0, ChatColor.GREEN + "Remote Detonator");
                } else if (is.getType() == OAK_SIGN) {
                    lore.add(0, ChatColor.GREEN + "Remote Detonate Sign");
                }
                if (lore.size() > 1) {
                    meta.setLore(lore);
                } else {
                    meta.setLore(new LinkedList<String>());
                }
                is.setItemMeta(meta);
                break;
            }
            case "py": {
                if (args.length > 0) {
                    switch (args[0]) {
                        case "egg":
                        case "xmas": {
                            if (!sender.hasPermission("pyro.egg")) {
                                sender.sendMessage(Storage.logo + " You do not have permission to do this!");
                                return;
                            }
                            Player player = (Player) sender;
                            ItemStack items = player.getInventory().getItemInMainHand();
                            if (items == null || items.getType() == Material.AIR) {
                                sender.sendMessage(Storage.logo + " You must have something in your hand!");
                                return;
                            }
                            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                            ItemStack disguise;
                            if (args[0].equals("egg")) {
                                disguise = new ItemStack(Storage.EASTER_EGG_TYPES[Storage.rnd.nextInt(Storage.EASTER_EGG_TYPES.length)]);
                                ItemMeta meta = disguise.getItemMeta();
                                meta.setDisplayName(ChatColor.MAGIC + "Transient");
                                disguise.setItemMeta(meta);
                            } else {
                                disguise = new ItemStack(Material.CHEST);
                                ItemMeta meta = disguise.getItemMeta();
                                meta.setDisplayName(ChatColor.MAGIC + "Transient");
                                disguise.setItemMeta(meta);
                            }
                            Item ent = (Item) player.getWorld().dropItem(player.getLocation().add(new Vector(0, 1, 0)), disguise);
                            ent.setVelocity(player.getLocation().getDirection().multiply(0.5));
                            Storage.eastereggs.put(ent, items);
                            ent.setPickupDelay(128);
                            break;
                        }
                        case "eggs": {
                            if (!sender.hasPermission("pyro.egg")) {
                                sender.sendMessage(Storage.logo + " You do not have permission to do this!");
                                return;
                            }
                            sender.sendMessage(Storage.logo + " " + Storage.eastereggs.size() + " egg(s) remaining!");
                            break;
                        }
                        case "retrieve-eggs": {
                            if (!(sender instanceof Player)) {
                                sender.sendMessage(Storage.logo + " Only players can retrieve eggs!");
                                return;
                            }
                            for (Item itemEnt : Storage.eastereggs.keySet()) {
                                itemEnt.teleport((Player) sender);
                            }
                            sender.sendMessage(Storage.logo + " " + Storage.eastereggs.size() + " egg(s) returned!");
                            break;
                        }
                        case "party": {
                            if (args.length == 2) {
                                if (args[1].equalsIgnoreCase("global")) {
                                    if (!sender.hasPermission("pyro.partyglobal")) {
                                        sender.sendMessage(Storage.logo + " You do not have permission to do this!");
                                        return;
                                    }
                                    if (Storage.globalparty) {
                                        Storage.globalparty = false;
                                        Storage.partyPlayers.removeAll(Bukkit.getOnlinePlayers());
                                        sender.sendMessage(Storage.logo + " Stopping the party, hiding the drugs.");
                                    } else {
                                        Storage.globalparty = true;
                                        Storage.partyPlayers.addAll(Bukkit.getOnlinePlayers());
                                        sender.sendMessage(Storage.logo + " Let's get this party started!");
                                    }
                                    return;
                                }
                            }
                            if (!sender.hasPermission("pyro.party")) {
                                sender.sendMessage(Storage.logo + " You do not have permission to do this!");
                                return;
                            }
                            if (Storage.partyPlayers.contains((Player) sender)) {
                                if (Storage.partyPlayers.size() == 1) {
                                    sender.sendMessage(Storage.logo + " Nope, one is not a party...");
                                } else {
                                    sender.sendMessage(Storage.logo + " \"I have a thing to go do..\"");
                                }
                                Storage.partyPlayers.remove((Player) sender);
                            } else {
                                if (Storage.partyPlayers.isEmpty()) {
                                    sender.sendMessage(Storage.logo + " One's a party! Right..?");
                                } else {
                                    sender.sendMessage(Storage.logo + " \"I was totally invited to this..\"");
                                }
                                Storage.partyPlayers.add((Player) sender);
                            }
                            break;
                        }

                        case "firework": {
                            if (!sender.hasPermission("pyro.firework")) {
                                sender.sendMessage(Storage.logo + " You do not have permission to do this!");
                                return;
                            }
                            Storage.inventories.put((Player) sender, new CraftedFirework());
                            CraftingGUI.updatePage(1, (Player) sender);
                            break;
                        }
                        case "help": {
                            sender.sendMessage(Storage.logo + "\n- /py egg: Drops the held item, disguised as a random mob egg.");
                            sender.sendMessage(ChatColor.YELLOW + "- /py eggs: Shows remaining eggs from /py egg.");
                            sender.sendMessage(ChatColor.YELLOW + "- /py firework: Opens a GUI for crafting firework items.");
                            sender.sendMessage(ChatColor.YELLOW + "- /py party <global?>: Starts a party of particles and fireworks.");
                            break;
                        }
                        default: {
                            sender.sendMessage(Storage.logo + " Unknown command. Try /py help");
                        }
                    }
                } else {
                    sender.sendMessage(Storage.logo + "\n- /py egg: Drops the held item, disguised as a random mob egg.");
                    sender.sendMessage(ChatColor.YELLOW + "- /py eggs: Shows remaining eggs from /py egg.");
                    sender.sendMessage(ChatColor.YELLOW + "- /py firework: Opens a GUI for crafting firework items.");
                    sender.sendMessage(ChatColor.YELLOW + "- /py party <global?>: Starts a party of particles and fireworks.");
                }
            }
            break;
        }
    }
}
