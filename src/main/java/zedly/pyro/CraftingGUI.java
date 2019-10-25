package zedly.pyro;

import java.util.*;
import org.apache.commons.lang.*;
import org.bukkit.*;
import static org.bukkit.Material.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

public class CraftingGUI {

    private static final Material[] accepted = new Material[]{GUNPOWDER, SUGAR, REDSTONE, GLOWSTONE_DUST, RED_DYE, ORANGE_DYE, YELLOW_DYE, LIME_DYE,
        GREEN_DYE, LIGHT_BLUE_DYE, BLUE_DYE, LIGHT_GRAY_DYE, PINK_DYE, MAGENTA_DYE,
        PURPLE_DYE, CYAN_DYE, GRAY_DYE, BROWN_DYE, WHITE_DYE, BLACK_DYE, PRISMARINE_SHARD, FLINT, QUARTZ, WATER_BUCKET, SPONGE};
    
    // Color Arrow Crafting
    public static void craftArrow(final InventoryView view, final Set<Integer> slots, final Player p,
            final boolean canCraft, final boolean isShift) {
        // Check if Color Arrow Recipes are enabled
        if (!Storage.recipes.get("Color Arrow")) {
            return;
        }

        // Record what the crafting view was like before the task
        final List<ItemStack> stacks = new ArrayList<>();
        for (int i1 = 1; i1 < 10; i1++) {
            stacks.add(view.getItem(i1).clone());
        }

        // Record how many times we will craft the arrow
        int maxValue = Integer.MAX_VALUE;
        for (int i = 1; i < 10; i++) {
            if (view.getItem(i) != null && view.getItem(i).getType() != AIR) {
                maxValue = (Math.min(view.getItem(i).getAmount(), maxValue));
            }
        }

        final int max = maxValue;                         // Max number of times arrow will be crafted
        final int cursor = view.getCursor().getAmount();  // How many items are in the cursor stack
        final ArrayList<int[]> ints = new ArrayList<>();  // Color codes for present Color Arrows
        final ArrayList<int[]> dye = new ArrayList<>();   // Color codes for present dye
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.pyro, new Runnable() {
            public void run() {

                // Choose which inventory (pre or current) to use
                final List<ItemStack> realInv = new ArrayList<>();
                if (canCraft) {
                    realInv.addAll(stacks);
                } else {
                    for (int i1 = 1; i1 < 10; i1++) {
                        realInv.add(view.getItem(i1));
                    }
                }

                // Sort out what the final color of the result will be
                boolean hasColor = false;
                boolean hasArrow = false;
                int arrowCount = 0;
                boolean enter = true;
                for (int i = 1; i < 10; i++) {
                    if (realInv.get(i - 1).getType() == ARROW) {
                        hasArrow = true;
                        if (realInv.get(i - 1).getItemMeta().hasLore()) {
                            arrowCount++;
                            if (realInv.get(i - 1).getItemMeta().getLore().get(0).equals(ChatColor.GOLD + "Color Arrow")) {
                                String info = realInv.get(i - 1).getItemMeta().getLore().get(1);
                                info = ChatColor.stripColor(info);
                                info = info.replace("R: ", "").replace("G: ", "").replace("B: ", "").replace(" ", "");
                                String[] data = info.split(",");
                                int r, g, b;
                                try {
                                    r = Integer.parseInt(data[0]);
                                    g = Integer.parseInt(data[1]);
                                    b = Integer.parseInt(data[2]);
                                } catch (NumberFormatException e) {
                                    continue;
                                }
                                ints.add(new int[]{r, g, b});
                                hasColor = true;
                            } else {
                                enter = false;
                            }
                        } else {
                            enter = false;
                        }
                    } else if (ArrayUtils.contains(Storage.FW_COLOR_ICON_MATS, realInv.get(i - 1).getType())) {
                        int id = ArrayUtils.indexOf(Storage.FW_COLOR_ICON_MATS, realInv.get(i - 1).getType());
                        dye.add(Storage.FW_COLOR_RGB_RICH[id]);
                        hasColor = true;
                    } else if (realInv.get(i - 1).getType() != AIR) {
                        enter = false;
                    }
                }
                int[] color;
                if (hasColor && hasArrow && enter) {
                    int[] average = new int[]{0, 0, 0};
                    if (!ints.isEmpty()) {
                        for (int[] i : ints) {
                            average[0] += i[0];
                            average[1] += i[1];
                            average[2] += i[2];
                        }
                        average[0] /= ints.size();
                        average[1] /= ints.size();
                        average[2] /= ints.size();
                    }
                    int[] average2 = new int[]{0, 0, 0};
                    if (!dye.isEmpty()) {
                        for (int[] i : dye) {
                            average2[0] += i[0];
                            average2[1] += i[1];
                            average2[2] += i[2];
                        }
                        average2[0] /= dye.size();
                        average2[1] /= dye.size();
                        average2[2] /= dye.size();
                    }
                    if (dye.isEmpty()) {
                        color = average;
                    } else if (ints.isEmpty()) {
                        color = average2;
                    } else {
                        color = new int[]{(average[0] + (average2[0] * dye.size())) / (dye.size() + 1), (average[1] + (average2[1] * dye.size())) / (dye.size() + 1), (average[2] + (average2[2] * dye.size())) / (dye.size() + 1)};
                    }

                    // Make the final result arrow stack
                    final ItemStack s = new ItemStack(ARROW, arrowCount);
                    ItemMeta meta = s.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GOLD + "Color Arrow");
                    lore.add(ChatColor.RED + "R: " + color[0] + ChatColor.WHITE + ", " + ChatColor.GREEN + "G: " + color[1] + ChatColor.WHITE + ", " + ChatColor.BLUE + "B: " + color[2]);
                    meta.setLore(lore);
                    s.setItemMeta(meta);
                    view.setItem(0, s);

                    // 
                    boolean reject = false;
                    if (!isShift) {
                        if (view.getCursor() != null && view.getCursor().isSimilar(s)) {
                            if (64 - cursor < arrowCount) {
                                reject = true;
                            }
                        } else {
                            reject = true;
                        }
                    }

                    // 
                    if (slots.size() == 1 && slots.contains(0) && canCraft) {
                        view.setItem(0, new ItemStack(AIR));
                        if (isShift) {
                            removeItem(p, s, s.getAmount());
                            for (int i = 0; i < max; i++) {
                                Map<Integer, ItemStack> idk = p.getInventory().addItem(s);
                                remove(view, realInv, 1);
                                if (!idk.isEmpty()) {
                                    int amount = s.getAmount() - idk.get(0).getAmount();
                                    remove(view, realInv, -1);
                                    break;
                                }
                            }
                            for (int i = 0; i < 9; i++) {
                                ItemStack stk = realInv.get(i);
                                if (stk.getAmount() < 2) {
                                    stk = new ItemStack(AIR, 0);
                                }
                                view.setItem(i + 1, stk);
                                realInv.set(i, stk);
                            }
                        } else if (!reject) {
                            remove(view, realInv, 1);
                        }
                        slots.remove(0);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.pyro, new Runnable() {
                            public void run() {
                                craftArrow(view, slots, p, false, false);
                            }
                        }, 1);
                    }
                    p.updateInventory();
                }
            }
        }, 0);
    }// Full Inventory Loss

    // Firework Sign/TNT Crafting
    public static void craftFireworkObject(final InventoryView view, final Set<Integer> slots, final Player p, final boolean canCraft) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.pyro, new Runnable() {
            public void run() {
                FireworkMeta fwm = null;
                int counter = 0;
                boolean tnt = view.getTopInventory().contains(TNT);
                boolean sign = view.getTopInventory().contains(OAK_SIGN);
                for (int i = 1; i < 10; i++) {
                    if (view.getItem(i).getType().equals(FIREWORK_ROCKET)) {
                        fwm = (FireworkMeta) view.getItem(i).getItemMeta();
                    }
                    if (!view.getItem(i).getType().equals(AIR)) {
                        counter++;
                    }
                }
                if (counter != 2) {
                    return;
                }
                if (!Storage.recipes.get("Firework TNT") && tnt) {
                    return;
                }
                if (!Storage.recipes.get("Firework Sign") && sign) {
                    return;
                }
                CraftedFirework f = new CraftedFirework();
                if (fwm != null) {
                    ArrayList<Integer> primary = new ArrayList<>();
                    ArrayList<Integer> fade = new ArrayList<>();
                    for (Color c : fwm.getEffects().get(0).getColors()) {
                        int[] rgb = new int[]{c.getRed(), c.getGreen(), c.getBlue()};
                        for (int[] i : Storage.FW_COLOR_RGB_MOJANG) {
                            if (ArrayUtils.isEquals(rgb, i)) {
                                primary.add(ArrayUtils.indexOf(Storage.FW_COLOR_RGB_MOJANG, i));
                            }
                        }
                    }
                    for (Color c : fwm.getEffects().get(0).getFadeColors()) {
                        int[] rgb = new int[]{c.getRed(), c.getGreen(), c.getBlue()};
                        for (int[] i : Storage.FW_COLOR_RGB_MOJANG) {
                            if (ArrayUtils.isEquals(rgb, i)) {
                                fade.add(ArrayUtils.indexOf(Storage.FW_COLOR_RGB_MOJANG, i));
                            }
                        }
                    }
                    f.flicker = fwm.getEffects().get(0).hasFlicker();
                    f.trail = fwm.getEffects().get(0).hasTrail();
                    f.power = fwm.getPower();
                    f.type = fwm.getEffects().get(0).getType().toString();
                    f.primary = primary;
                    f.fade = fade;
                }
                if (!((sign ^ tnt) && fwm != null)) {
                    return;
                }
                Material mat = TNT;
                if (sign) {
                    mat = OAK_SIGN;
                }
                view.setItem(0, Utilities.generateFireworkItem(f, mat));
                if (slots.size() == 1 && slots.contains(0) && canCraft) {
                    for (int i1 = 1; i1 < 10; i1++) {
                        ItemStack stk = view.getItem(i1);
                        stk.setAmount(stk.getAmount() - 1);
                        if (stk.getAmount() < 1) {
                            stk = null;
                        }
                        view.setItem(i1, stk);
                    }
                }
                p.updateInventory();
            }
        }, 1);

    }

    public static void remove(InventoryView view, List<ItemStack> realInv, int amount) {
        for (int i = 0; i < 9; i++) {
            ItemStack stk = realInv.get(i).clone();
            stk.setAmount(realInv.get(i).getAmount() - amount);
            if (stk.getAmount() < 1) {
                stk = new ItemStack(AIR, 0);
            }
            view.setItem(i + 1, stk);
            realInv.set(i, stk);
        }
    }

    public static void removeItem(Player player, ItemStack stk, int amount) {
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) != null && inv.getItem(i).getType() == stk.getType()) {
                if (inv.getItem(i).getItemMeta().equals(stk.getItemMeta())) {
                    if (inv.getItem(i).getAmount() > amount) {
                        int res = inv.getItem(i).getAmount() - amount;
                        ItemStack rest = inv.getItem(i);
                        rest.setAmount(res);
                        inv.setItem(i, rest);
                        return;
                    } else {
                        amount -= inv.getItem(i).getAmount();
                        inv.setItem(i, null);
                    }
                }
            }
        }
    }

    // Opens a firework inventory and updates pages
    public static void updatePage(int page, Player player) {
        CraftedFirework fwk = Storage.inventories.get(player);
        String id = "";
        switch (page) {
            case 1:
                id = "Primary";
                break;
            case 2:
                id = "Fade";
                break;
            case 3:
                id = "Properties";
                break;
            case 4:
                id = "Result";
                break;
        }
        final Inventory inv = Bukkit.createInventory(player, 54, ChatColor.DARK_RED + "Firework : " + id + " : " + page + "/4");
        int[] whiteBorder = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 46, 47, 48, 49, 50, 51, 52};
        int[] grayFill1 = new int[]{29, 30, 31, 32, 33, 37, 38, 42, 43};
        int[] grayFill2 = new int[]{10, 12, 14, 16, 19, 20, 21, 22, 23, 24, 25, 31, 37, 39, 40, 41, 43};
        int[] grayFill3 = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 21, 22, 23, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
        for (int i : whiteBorder) {
            setSlot(i, inv, ChatColor.BLACK + "", null, WHITE_STAINED_GLASS_PANE, 1);
        }
        setSlot(53, inv, ChatColor.WHITE + "Next Page", null, ENCHANTED_BOOK, 1);
        setSlot(45, inv, ChatColor.WHITE + "Previous Page", null, ENCHANTED_BOOK, 1);
        switch (page) {
            case 1:
            case 2:
                for (int i : grayFill1) {
                    setSlot(i, inv, ChatColor.BLACK + "", null, GRAY_STAINED_GLASS_PANE, 1);
                }
                final ArrayList<Integer> colors;
                int numberRandom;
                if (page == 1) {
                    colors = fwk.primary;
                    numberRandom = fwk.numberRandomPrimary;
                } else {
                    colors = fwk.fade;
                    numberRandom = fwk.numberRandomFade;
                }

                setSlot(39, inv, ChatColor.WHITE + "Less Random Colors", null, GHAST_TEAR, 1);
                if (numberRandom == 0) {
                    setSlot(40, inv, ChatColor.WHITE + "Random: " + ChatColor.RED + "Disabled", null, ENDER_PEARL, 1);
                } else {
                    setSlot(40, inv, ChatColor.WHITE + "Random: " + ChatColor.GREEN + "Enabled", null, ENDER_EYE, numberRandom + 1);
                }
                setSlot(41, inv, ChatColor.WHITE + "More Random Colors", null, SNOWBALL, 1);
                resetColors(inv, colors);
                break;
            case 3:
                for (int i : grayFill2) {
                    setSlot(i, inv, ChatColor.BLACK + "", null, GRAY_STAINED_GLASS_PANE, 1);
                }
                String flicker = (fwk.randomFlicker) ? ChatColor.GOLD + "Random"
                        : (fwk.flicker) ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled";
                String trail = (fwk.randomTrail) ? ChatColor.GOLD + "Random"
                        : (fwk.trail) ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled";
                setSlot(11, inv, "Flicker: " + flicker, null, GLOWSTONE_DUST, 1);
                setTypeIcon(fwk, 13, inv);
                setSlot(15, inv, "Trail: " + trail, null, DIAMOND, 1);
                setSlot(28, inv, "Decrease Power", null, GHAST_TEAR, 1);
                setSlot(29, inv, "Power: " + ChatColor.GREEN + fwk.power, null, GUNPOWDER, fwk.power + 1);
                setSlot(30, inv, "Increase Power", null, SNOWBALL, 1);
                setSlot(32, inv, "Decrease Launch Height", null, GHAST_TEAR, 1);
                setSlot(33, inv, "Launch Height: " + ChatColor.GREEN + fwk.launchHeight, null, SUGAR, fwk.launchHeight + 1);
                setSlot(34, inv, "Increase Launch Height", null, SNOWBALL, 1);
                if (fwk.randomPower) {
                    setSlot(38, inv, "Random: " + ChatColor.GREEN + "Enabled", null, ENDER_EYE, 1);
                } else {
                    setSlot(38, inv, "Random: " + ChatColor.RED + "Disabled", null, ENDER_PEARL, 1);
                }
                if (fwk.randomLaunchHeight) {
                    setSlot(42, inv, "Random: " + ChatColor.GREEN + "Enabled", null, ENDER_EYE, 1);
                } else {
                    setSlot(42, inv, "Random: " + ChatColor.RED + "Disabled", null, ENDER_PEARL, 1);
                }
                break;
            case 4:
                for (int i : grayFill3) {
                    setSlot(i, inv, ChatColor.BLACK + "", null, GRAY_STAINED_GLASS_PANE, 1);
                }
                setSlot(20, inv, ChatColor.AQUA + "Create Firework TNT", ChatColor.GREEN + "Shift-Click for a Stack", TNT, 1);
                setSlot(24, inv, ChatColor.AQUA + "Create Firework Sign", ChatColor.GREEN + "Shift-Click for a Stack", OAK_SIGN, 1);
                if (fwk.numberRandomFade != 0 || fwk.randomFlicker || fwk.randomLaunchHeight || fwk.randomPower
                        || fwk.numberRandomPrimary != 0 || fwk.randomTrail || fwk.type.equals("Random")) {
                    setSlot(30, inv, ChatColor.BLACK + "", null, GRAY_STAINED_GLASS_PANE, 1);
                    setSlot(32, inv, ChatColor.BLACK + "", null, GRAY_STAINED_GLASS_PANE, 1);
                } else {
                    setSlot(30, inv, ChatColor.AQUA + "Create Firework Rocket", ChatColor.GREEN + "Shift-Click for a Stack", FIREWORK_ROCKET, 1);
                    setSlot(32, inv, ChatColor.AQUA + "Create Firework Star", ChatColor.GREEN + "Shift-Click for a Stack", FIRE_CHARGE, 1);
                }
                break;
        }
        player.openInventory(inv);
    }

    private static void setTypeIcon(CraftedFirework fwk, int slot, Inventory inv) {
        Material fireworkType = null;
        switch (fwk.type) {
            case "Ball":
                fireworkType = PAPER;
                break;
            case "Large Ball":
                fireworkType = FIRE_CHARGE;
                break;
            case "Burst":
                fireworkType = FEATHER;
                break;
            case "Star":
                fireworkType = GOLD_NUGGET;
                break;
            case "Creeper":
                fireworkType = CREEPER_HEAD;
                break;
            case "Random":
                fireworkType = ENDER_EYE;
                break;
        }
        setSlot(slot, inv, "Type: " + ChatColor.GOLD + fwk.type, null, fireworkType, 1);
    }

    // Sets inventory slot to specified item stack information
    private static void setSlot(int slot, Inventory inv, String name, String lore, Material mat, int amount) {
        ItemStack stack = new ItemStack(mat, amount);
        ItemMeta meta = stack.getItemMeta();
        if (lore != null) {
            List<String> str = new ArrayList<>();
            str.add(lore);
            meta.setLore(str);
        }
        meta.setDisplayName(ChatColor.WHITE + name);
        stack.setItemMeta(meta);
        inv.setItem(slot, stack);
    }

    // Sets the dye data in the firework GUI
    private static void resetColors(Inventory inventory, ArrayList<Integer> colors) {
        int counter = 0;
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null
                    || inventory.getItem(i).getType() == Material.AIR
                    || ArrayUtils.contains(Storage.FW_COLOR_ICON_MATS, inventory.getItem(i).getType())) {
                short displayAmount = 1;
                if (colors.contains(counter)) {
                    displayAmount = 64;
                }
                ItemStack stk = new ItemStack(Storage.FW_COLOR_ICON_MATS[counter], displayAmount);
                ItemMeta m = stk.getItemMeta();
                m.setDisplayName(ChatColor.WHITE + StringUtils.capitalize(Storage.FW_COLOR_FRIENDLY_NAMES[counter].replace("_", " ")));
                stk.setItemMeta(m);
                inventory.setItem(i, stk);
                counter++;
            }
        }
    }

    // Update Player view and update firework status
    public static void recieveClick(int slot, int page, Player player, Inventory inventory, boolean shift) {
        CraftedFirework fwk = Storage.inventories.get(player);
        switch (page) {
            case 1:
            case 2:
                ArrayList<Integer> colors;
                int numberRandom;
                if (page == 1) {
                    colors = fwk.primary;
                    numberRandom = fwk.numberRandomPrimary;
                } else {
                    colors = fwk.fade;
                    numberRandom = fwk.numberRandomFade;
                }
                if (inventory.getItem(slot) != null && ArrayUtils.contains(Storage.FW_COLOR_ICON_MATS, inventory.getItem(slot).getType())) {
                    numberRandom = 0;
                    int color = ArrayUtils.indexOf(Storage.FW_COLOR_ICON_MATS, inventory.getItem(slot).getType());
                    if (!colors.contains(color)) {
                        if (colors.size() < 8) {
                            colors.add(color);
                        }
                    } else {
                        colors.remove((Integer) color);
                    }
                }
                if (slot == 39) {
                    numberRandom = Math.max(--numberRandom, 0);
                } else if (slot == 40) {
                    if (numberRandom != 0) {
                        numberRandom = 0;
                    } else {
                        colors.clear();
                        numberRandom = 1;
                    }
                } else if (slot == 41) {
                    colors.clear();
                    numberRandom = Math.min(numberRandom + 1, 16);
                }
                if (page == 1) {
                    fwk.numberRandomPrimary = numberRandom;
                } else {
                    fwk.numberRandomFade = numberRandom;
                }
                resetColors(inventory, colors);
                break;
            case 3:
                switch (slot) {
                    case 11:
                        if (fwk.randomFlicker) {
                            fwk.flicker = true;
                            fwk.randomFlicker = false;
                        } else if (fwk.flicker) {
                            fwk.flicker = false;
                        } else {
                            fwk.randomFlicker = true;
                        }
                        break;
                    case 13:
                        String[] types = {"Ball", "Large Ball", "Burst", "Star", "Creeper", "Random"};
                        int index = ArrayUtils.indexOf(types, fwk.type) + 1;
                        if (index >= types.length) {
                            index = 0;
                        }
                        fwk.type = types[index];
                        break;
                    case 15:
                        if (fwk.randomTrail) {
                            fwk.trail = true;
                            fwk.randomTrail = false;
                        } else if (fwk.trail) {
                            fwk.trail = false;
                        } else {
                            fwk.randomTrail = true;
                        }
                        break;
                    case 28:
                        fwk.randomPower = false;
                        fwk.power = --fwk.power;
                        if (fwk.power < 0) {
                            fwk.power = 0;
                        }
                        break;
                    case 30:
                        fwk.randomPower = false;
                        fwk.power = ++fwk.power;
                        if (fwk.power > 3) {
                            fwk.power = 3;
                        }
                        break;
                    case 32:
                        fwk.randomLaunchHeight = false;
                        fwk.launchHeight = --fwk.launchHeight;
                        if (fwk.launchHeight < 0) {
                            fwk.launchHeight = 0;
                        }
                        break;
                    case 34:
                        fwk.randomLaunchHeight = false;
                        fwk.launchHeight = ++fwk.launchHeight;
                        if (fwk.launchHeight > 8) {
                            fwk.launchHeight = 8;
                        }
                        break;
                    case 38:
                        fwk.power = 1;
                        fwk.randomPower = !fwk.randomPower;
                        break;
                    case 42:
                        fwk.launchHeight = 0;
                        fwk.randomLaunchHeight = !fwk.randomLaunchHeight;
                        break;
                }
                break;
            case 4:
                switch (slot) {
                    case 20:
                    case 24:
                    case 30:
                    case 32:
                        boolean output = true;
                        if (slot == 30 || slot == 32) {
                            if (fwk.numberRandomFade != 0 || fwk.randomFlicker || fwk.randomLaunchHeight || fwk.randomPower
                                    || fwk.numberRandomPrimary != 0 || fwk.randomTrail || fwk.type.equals("Random")) {
                                output = false;
                            }
                        }
                        if (output) {
                            ItemStack stk = Utilities.generateFireworkItem(fwk, inventory.getItem(slot).getType());
                            if (shift) {
                                stk.setAmount(64);
                            }
                            player.getInventory().addItem(stk);
                        }
                        break;
                }
                break;
        }
        updatePage(page, player);
    }

}
