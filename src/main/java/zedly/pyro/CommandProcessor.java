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
import org.bukkit.util.Vector;

public class CommandProcessor {

    public static String getConfig(Player player, String[] args) {
        String str = null;
        switch (args[0].toLowerCase()) {
            case "custom":
                if (args.length == 11) {
                    str = (ChatColor.GRAY + args[1] + ":" + args[2] + ":" + args[3] + ":" + args[4] + ":" + args[5] + ":" + args[6] + ":" + args[7] + ":" + args[8] + ":" + args[9] + ":" + args[10]);
                } else {
                    player.sendMessage(Storage.logo + " <Red Frequency[-180..180]> <Green Frequency [-180..180]> <Blue Frequency [-180..180]> <Red Delay [-180..180]> <Green Delay [-180..180]> <Blue Delay [-180..180]> <Brightness [0..127> <Offset [0..]> <Speed [1..]> <Gamma [-1.0..]>");
                    return str;
                }
                break;
            case "grayscale":
                if (args.length == 4) {
                    str = (ChatColor.GRAY + "7:7:7:0:0:0:" + args[1] + ":" + args[2] + ":" + args[3] + ":1");
                } else {
                    player.sendMessage(Storage.logo + " <Brigtness [0..127]> <Offset [0..]> <Speed [1..]>");
                    return str;
                }
                break;
            case "normal":
                if (args.length == 4) {
                    str = (ChatColor.GRAY + "7:7:7:0:120:240:" + args[1] + ":" + args[2] + ":" + args[3] + ":1");
                } else {
                    player.sendMessage(Storage.logo + " <Brigtness [0..127]> <Offset [0..]> <Speed [1..]>");
                    return str;
                }
                break;
            case "pastel":
                if (args.length == 3) {
                    str = (ChatColor.GRAY + "7:7:7:0:120:240:35:" + args[1] + ":" + args[2] + ":1");
                } else {
                    player.sendMessage(Storage.logo + " <Offset [0..]> <Speed [1..]>");
                    return str;
                }
                break;
            case "bright":
                if (args.length == 3) {
                    str = (ChatColor.GRAY + "7:7:7:0:120:240:127:" + args[1] + ":" + args[2] + ":.001");
                } else {
                    player.sendMessage(Storage.logo + " <Offset [0..]> <Speed [1..]>");
                    return str;
                }
                break;
            case "color":
                if (args.length == 4) {
                    int offset = Integer.parseInt(args[2]);
                    int speed = Integer.parseInt(args[3]);
                    if (ArrayUtils.contains(Storage.colors, args[1].toLowerCase())) {
                        args[0] = args[1];
                        String[] color = new String[]{":2:3:180:0.05:0.05:62:0.05:0.95:", ":0:3:7:0:0.1:30:1:0.9:", ":0:3:10:0:0.15:300:1:0.85:",
                            ":5:3:10:0.3:0.15:200:0.7:0.85:", ":0:3:7:0:0.15:62:1:0.85:", ":0:3:25:0.1:0.15:110:0.9:0.85:", ":0:3:10:0:0.1:327:1:0.9:",
                            ":0:3:15:0:0.1:25:0:0.45:", ":0:3:15:0:0.15:25:0:0.75:", ":0:3:15:0.05:0.05:180:0.95:0.75:", ":0:3:10:0:0.20:280:1:0.80:",
                            ":0:3:15:0.05:0.25:240:0.95:0.75:", ":0:3:10:0.05:0.1:25:0.55:0.45:", ":0:3:15:0.05:0.1:96:0.93:0.4:", ":0:3:15:0:0.2:0:1:0.8:",
                            ":0:3:180:0.05:0.05:180:0.15:0.15:"};
                        str = (ChatColor.GRAY + "" + speed + color[ArrayUtils.indexOf(Storage.colors, args[1].toLowerCase())] + offset + ":" + offset + ":" + offset);
                    }
                } else {
                    player.sendMessage(Storage.logo + " <Color> <Offset [0..]> <Speed [1..]>");
                    player.sendMessage(ChatColor.YELLOW + Storage.colorString);
                }
                break;
        }
        return str;
    }

    private static void trl(Player player, String[] args) {
        String[] trlList = new String[]{"custom", "mineral", "color", "flower"};
        ArrayList<String> trails = new ArrayList<>();
        List<String> lore = player.getItemInHand().getItemMeta().getLore();
        HashSet<String> oldItems = new HashSet<>();
        HashSet<String> newItems = new HashSet<>();
        if (lore != null) {
            for (String s : lore) {
                if (ArrayUtils.contains(trlList, ChatColor.stripColor(s.toLowerCase().replace(" trail", "")))) {
                    trails.add(ChatColor.stripColor(s.toLowerCase().replace(" trail", "")));
                }
            }
        }
        if (lore != null) {
            for (String s : lore) {
                Material m = Material.matchMaterial(ChatColor.stripColor(s.split(":")[0].toUpperCase().replace(" ", "_")));//
                if (m != AIR && m != null && !ArrayUtils.contains(Storage.badIds, m.getId())) {
                    if (s.contains(":")) {
                        int i = Integer.parseInt(ChatColor.stripColor(s.split(":")[1]));
                        oldItems.add(m + ":" + i);
                    }
                }
            }
        }
        for (String s : args) {
            int i = 0;
            if (s.contains(":")) {
                try {
                    i = Integer.parseInt(s.split(":")[1]);
                } catch (NumberFormatException e) {
                    i = 0;
                }
            }
            Material m = Material.matchMaterial(s.split(":")[0].toUpperCase().replace(" ", "_"));
            if (m == AIR || m == null) {
                int id = 0;
                try {
                    id = Integer.parseInt(s.split(":")[0]);
                } catch (NumberFormatException e) {
                }
                m = Material.getMaterial(id);
            }
            if (m != AIR && m != null && !ArrayUtils.contains(Storage.badIds, m.getId())) {
                newItems.add(m + ":" + i);
            }
        }
        if (args[0].equalsIgnoreCase("add")) {
            if (ArrayUtils.contains(trlList, args[1].toLowerCase()) && !trails.contains(args[1].toLowerCase())) {
                trails.add(args[1].toLowerCase());
            }
            oldItems.addAll(newItems);
        } else if (args[0].equalsIgnoreCase("del")) {
            if (ArrayUtils.contains(trlList, args[1].toLowerCase())) {
                trails.remove(args[1].toLowerCase());
            }
            if (args.length == 2 && args[1].equalsIgnoreCase("custom")) {
                oldItems.clear();
            } else {
                oldItems.removeAll(newItems);
            }
        }
        if (oldItems.isEmpty()) {
            trails.remove("custom");
        } else if (!trails.contains("custom")) {
            trails.add("custom");
        }
        List<String> newLore = new ArrayList<>();
        for (String trl : trails) {
            newLore.add(ChatColor.GOLD + WordUtils.capitalize(trl) + " Trail");
        }
        for (String s : oldItems) {
            newLore.add(ChatColor.GREEN + WordUtils.capitalize(s.toLowerCase().replace("_", " ")));
        }
        player.sendMessage(Storage.logo + " Trail effect changed.");
        ItemMeta meta = player.getItemInHand().getItemMeta();
        meta.setLore(newLore);
        player.getItemInHand().setItemMeta(meta);
    }

    public static void run(CommandSender sender, Command command, String commandlabel, String[] args) {
        if (!(sender instanceof Player)) {
            return;
        }
        switch (commandlabel.toLowerCase()) {
            case "cannon": {
                if (!sender.hasPermission("pyro.cannon") || !(sender instanceof Player)) {
                    sender.sendMessage(Storage.logo + " You do not have permission to do this!");
                    return;
                }
                if (args.length == 1) {
                    Material m;
                    short data;
                    try {
                        data = Short.parseShort(args[0].split(":")[1]);
                    } catch (Exception e) {
                        data = 0;
                    }
                    try {
                        m = Material.getMaterial(Integer.parseInt(args[0].split(":")[0]));
                    } catch (Exception e) {
                        m = Material.getMaterial(args[0].split(":")[0].toUpperCase());
                    }
                    if (m != null && !ArrayUtils.contains(Storage.badIds, m.getId())) {
                        Player player = (Player) sender;
                        player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_DEATH, 10, 100f);
                        final Item item = player.getWorld().dropItem(player.getLocation().add(new Vector(0, 1.62, 0)), new ItemStack(m, 0, data));
                        item.setVelocity(player.getLocation().getDirection().multiply(1.5));
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.pyro, new Runnable() {
                            @Override
                            public void run() {
                                item.getWorld().createExplosion(item.getLocation(), 0);
                                item.remove();
                            }
                        }, 40);
                    } else {
                        sender.sendMessage(Storage.logo + " " + args[0] + " is not an item!");
                    }
                } else {
                    sender.sendMessage(Storage.logo + " /cannon <Item ID/Item Name>:<MaterialData?>");
                }
                break;
            }
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
                        if (ArrayUtils.contains(Storage.colors, args[0].toLowerCase())) {
                            color = Storage.color[ArrayUtils.indexOf(Storage.colors, args[0].toLowerCase())];
                        } else {
                            sender.sendMessage(Storage.logo + " /color <red [0..255]> <green [0..255]> <blue [0..255]>\n/color <color name>\n" + Storage.colorString);
                            return;
                        }
                    }
                    Player player = (Player) sender;
                    if (player.getItemInHand().getType().equals(ARROW)) {
                        ItemStack stk = player.getItemInHand();
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

            case "chromo": {
                Player player = (Player) sender;
                if (!player.hasPermission("pyro.chromo")) {
                    player.sendMessage(Storage.logo + " You do not have permission to do this!");
                    return;
                }
                if (ArrayUtils.contains(Storage.leather, player.getItemInHand().getType())) {
                    if (args.length == 0) {
                        sender.sendMessage(Storage.logo + " Chromatic Armor Types: \nCustom\nGrayscale\nNormal\nPastel\nBright\nColor");
                        return;
                    }
                    ItemMeta meta = player.getItemInHand().getItemMeta();
                    List<String> lore = new ArrayList<>();
                    boolean b = false;
                    for (int i = 1; i < args.length; i++) {
                        try {
                            Double.parseDouble(args[i]);
                        } catch (NumberFormatException e) {
                            if (!ArrayUtils.contains(Storage.colors, args[i].toLowerCase())) {
                                b = true;
                            }
                        }
                    }
                    if (b) {
                        player.sendMessage(Storage.logo + " One of those values is not a number!");
                        return;
                    }
                    String config = getConfig(player, args);
                    if (config != null) {
                        lore.add(ChatColor.GREEN + "Chromatic Armor: " + ChatColor.GOLD + WordUtils.capitalize(args[0].replace("_", " ")));
                        lore.add(config);
                        if (player.getItemInHand().getItemMeta().hasLore()) {
                            List<String> oldLore = player.getItemInHand().getItemMeta().getLore();
                            if (oldLore.get(0).contains(ChatColor.GREEN + "Chromatic Armor")) {
                                if (oldLore.size() > 2) {
                                    for (int i = 2; i < oldLore.size(); i++) {
                                        lore.add(oldLore.get(i));
                                    }
                                }
                            } else {
                                lore.addAll(oldLore);
                            }
                        }
                        meta.setLore(lore);
                        player.getItemInHand().setItemMeta(meta);
                        player.updateInventory();
                        player.sendMessage(Storage.logo + " Chromatic Armor created!");
                    }
                } else {
                    player.sendMessage(Storage.logo + " You need to be holding a leather armor piece!");
                }
                break;
            }
            case "trl": {
                if (!sender.hasPermission("pyro.trail")) {
                    sender.sendMessage(Storage.logo + " You do not have permission to do this!");
                    return;
                }
                Player player = (Player) sender;
                Material mat = player.getItemInHand().getType();
                if (args.length == 0 || "help".equalsIgnoreCase(args[0])) {
                    player.sendMessage(Storage.logo + " For Normal Trails: /trl add <trail>");
                    player.sendMessage(Storage.logo + " For Custom Trails: /trl add <Item ID:Data> ...");
                    break;
                }
                if (mat == DIAMOND_CHESTPLATE || mat == IRON_CHESTPLATE || mat == CHAINMAIL_CHESTPLATE
                        || mat == GOLD_CHESTPLATE || mat == LEATHER_CHESTPLATE) {
                    ItemMeta meta = player.getItemInHand().getItemMeta();
                    if ("add".equalsIgnoreCase(args[0]) || "del".equalsIgnoreCase(args[0]) && args.length > 1) {
                        trl(player, args);
                    } else if ("list".equalsIgnoreCase(args[0])) {
                        player.sendMessage(Storage.logo + " Trails:\nFlower\nColor\nMineral\nCustom");
                    }
                } else {
                    player.sendMessage(Storage.logo + " You need to be holding a chestplate!");
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
                ItemStack is = player.getItemInHand();
                if (is.getType() != TNT && is.getType() != NAME_TAG && is.getType() != SIGN) {
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
                    if (is.getType() == SIGN && lore.size() > 2) {
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
                } else if (is.getType() == SIGN) {
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
                        case "egg": {
                            if (!sender.hasPermission("pyro.egg")) {
                                sender.sendMessage(Storage.logo + " You do not have permission to do this!");
                                return;
                            }
                            Player player = (Player) sender;
                            ItemStack items = player.getItemInHand().clone();
                            if (items.getTypeId() == 0) {
                                sender.sendMessage(Storage.logo + " You must have something in your hand!");
                                return;
                            }
                            player.setItemInHand(new ItemStack(0, 0));
                            Item ent = (Item) player.getWorld().dropItem(player.getLocation().add(new Vector(0, 1, 0)), new ItemStack(383, 1, (short) Storage.eastereggids[Storage.rnd.nextInt(23)]));
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
