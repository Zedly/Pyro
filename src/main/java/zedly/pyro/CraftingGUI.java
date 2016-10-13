package zedly.pyro;

import java.util.*;
import org.apache.commons.lang.*;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import static org.bukkit.Material.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

public class CraftingGUI {

    private static boolean chromaCraftingEnabled = false;

    // Chomatic Armor Crafting *DISABLED*
    public static void craftChromo(final InventoryView view, final Set<Integer> slots, final Player p,
            final boolean canCraft) {
        if (!isChromaCraftingEnabled()) {
            return;
        }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.pyro, new Runnable() {
            public void run() {
                int offset = 0;
                int speed = 0;
                String type = "not configured";
                int color = 0;
                boolean newArmor = false;
                boolean hasArmor = false;
                ItemStack armor = new ItemStack(AIR);
                int sponge = 1;
                int spongeAmount = 0;
                int bucket = 2;
                boolean washed = false;
                Material[] accepted = new Material[]{SULPHUR, SUGAR, REDSTONE, GLOWSTONE_DUST, INK_SACK, PRISMARINE_SHARD, FLINT, QUARTZ, WATER_BUCKET, SPONGE};
                ArrayList<Material> mats = new ArrayList<>();
                ArrayList<Integer> infos = new ArrayList<>();
                String[] args = new String[]{};
                for (int i = 1; i < 10; i++) {
                    if (!view.getItem(i).getType().equals(AIR)) {
                        if (ArrayUtils.contains(Storage.leather, view.getItem(i).getType())) {
                            if (view.getItem(i).getItemMeta().hasLore()) {
                                if (view.getItem(i).getItemMeta().getLore().get(0).contains(ChatColor.GREEN + "Chromatic Armor")) {
                                    if (view.getItem(i).getItemMeta().getLore().size() > 1) {
                                        if (hasArmor == true) {
                                            return;
                                        }
                                        hasArmor = true;
                                        args = ChatColor.stripColor(view.getItem(i).getItemMeta().getLore().get(1)).split(":");
                                        type = ChatColor.stripColor(view.getItem(i).getItemMeta().getLore().get(0)).split(": ")[1].toLowerCase();
                                        armor = view.getItem(i);
                                        if (view.getItem(i).getItemMeta().getLore().get(0).contains("Not Configured")) {
                                            newArmor = true;
                                        }
                                    }
                                }
                            }
                        } else if (ArrayUtils.contains(accepted, view.getItem(i).getType())) {
                            if (view.getItem(i).getType().equals(WATER_BUCKET)) {
                                bucket = i;
                            } else if (view.getItem(i).getType().equals(SPONGE)) {
                                sponge = i;
                                spongeAmount = view.getItem(i).getAmount();
                            }
                            mats.add(view.getItem(i).getType());
                            infos.add((int) view.getItem(i).getData().getData());
                        } else {
                            return;
                        }
                    }
                }
                if (hasArmor && newArmor) {
                    speed += 10;
                    if (mats.size() == 4) {
                        int notInk = 0;
                        boolean sameI = true;
                        boolean sameD = true;
                        for (int i = 0; i < 4; i++) {
                            if (!(mats.get(i).equals(mats.get(0)) && mats.get(i).equals(INK_SACK))) {
                                sameI = false;
                            }
                            if (!(infos.get(i).equals(infos.get(0)) && mats.get(i).equals(INK_SACK))) {
                                sameD = false;
                            }
                            if (!mats.get(i).equals(INK_SACK)) {
                                notInk++;
                            }
                        }
                        if (sameI && sameD) {
                            type = "color";
                            color = 15 - infos.get(0);
                        } else if (notInk == 1) {
                            if (mats.contains(PRISMARINE_SHARD)) {
                                infos.remove(mats.lastIndexOf(PRISMARINE_SHARD));
                                mats.remove(mats.lastIndexOf(PRISMARINE_SHARD));
                                if (infos.contains(1) && infos.contains(2) && infos.contains(4)) {
                                    type = "normal";
                                }
                            } else if (mats.contains(GLOWSTONE_DUST)) {
                                infos.remove(mats.lastIndexOf(GLOWSTONE_DUST));
                                mats.remove(mats.lastIndexOf(GLOWSTONE_DUST));
                                if (infos.contains(1) && infos.contains(2) && infos.contains(4)) {
                                    type = "bright";
                                }
                            } else if (mats.contains(QUARTZ)) {
                                infos.remove(mats.lastIndexOf(QUARTZ));
                                mats.remove(mats.lastIndexOf(QUARTZ));
                                if (infos.contains(1) && infos.contains(2) && infos.contains(4)) {
                                    type = "pastel";
                                }
                            } else if (mats.contains(FLINT)) {
                                infos.remove(mats.lastIndexOf(FLINT));
                                mats.remove(mats.lastIndexOf(FLINT));
                                if (infos.contains(8) && infos.contains(7) && infos.contains(15)) {
                                    type = "grayscale";
                                }
                            }
                        }
                    }
                } else if (hasArmor) {
                    if (mats.size() == 2 && mats.contains(WATER_BUCKET) && mats.contains(SPONGE)) {
                        washed = true;
                        type = "not configured";
                    } else if (!type.equals("not configured")) {
                        try {
                            if (ArrayUtils.contains(Storage.colors, type.replace(" ", "_"))) {
                                color = ArrayUtils.indexOf(Storage.colors, type.replace(" ", "_"));
                                speed = Integer.parseInt(args[0]);
                                offset = Integer.parseInt(args[9]);
                                type = "color";
                            } else {
                                speed = Integer.parseInt(args[8]);
                                offset = Integer.parseInt(args[7]);
                            }
                        } catch (NumberFormatException e) {
                        }
                        for (Material mat : mats) {
                            switch (mat) {
                                case SULPHUR:
                                    speed--;
                                    break;
                                case SUGAR:
                                    speed++;
                                    break;
                                case REDSTONE:
                                    offset -= 5;
                                    break;
                                case GLOWSTONE_DUST:
                                    offset += 5;
                                    break;
                            }
                        }
                    }
                }
                if (hasArmor) {
                    String config;
                    if (!type.equals("not configured")) {
                        switch (type) {
                            case "color":
                                args = new String[]{type, Storage.colors[color], offset + "", speed + ""};
                                type = Storage.colors[color];
                                break;
                            case "normal":
                            case "grayscale":
                                args = new String[]{type, 127 + "", offset + "", speed + ""};
                                break;
                            default:
                                args = new String[]{type, offset + "", speed + ""};
                                break;
                        }
                        config = CommandProcessor.getConfig(p, args);
                    } else {
                        config = ChatColor.GRAY + "Not Configured";
                    }
                    ItemStack finalArmor = new ItemStack(armor.getType());
                    List<String> lore = new ArrayList<>();
                    ItemMeta meta = finalArmor.getItemMeta();
                    lore.add(ChatColor.GREEN + "Chromatic Armor: " + ChatColor.GOLD + WordUtils.capitalize(type.replace("_", " ")));
                    lore.add(config);
                    if (armor.getItemMeta().hasLore()) {
                        List<String> oldLore = armor.getItemMeta().getLore();
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
                    finalArmor.setItemMeta(meta);

                    view.setItem(0, finalArmor);
                    if (slots.size() == 1 && slots.contains(0) && canCraft) {
                        for (int i1 = 1; i1 < 10; i1++) {
                            ItemStack stk = view.getItem(i1);
                            stk.setAmount(stk.getAmount() / 2);
                            if (stk.getAmount() < 1) {
                                stk = null;
                            }
                            view.setItem(i1, stk);
                        }
                        if (washed) {
                            view.setItem(sponge, new ItemStack(SPONGE, 1, (short) 1));
                            view.setItem(bucket, new ItemStack(BUCKET));
                        }
                    }
                }
                p.updateInventory();
            }
        }, 0);
    }// Sponge loss // Full Inventory Loss

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
                    } else if (realInv.get(i - 1).getType() == INK_SACK) {
                        int id = realInv.get(i - 1).getData().getData();
                        dye.add(Storage.color[15 - id]);
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
            if (inv.getItem(i) != null && inv.getItem(i).getType() == stk.getType() && inv.getItem(i).getDurability() == stk.getData().getData()) {
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

    // Firework Sign/TNT Crafting
    public static void craftFireworkObject(final InventoryView view, final Set<Integer> slots, final Player p, final boolean canCraft) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.pyro, new Runnable() {
            public void run() {
                FireworkMeta fwm = null;
                int counter = 0;
                boolean tnt = view.getTopInventory().contains(TNT);
                boolean sign = view.getTopInventory().contains(SIGN);
                for (int i = 1; i < 10; i++) {
                    if (view.getItem(i).getType().equals(FIREWORK)) {
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
                        for (int[] i : Storage.mojangColors) {
                            if (ArrayUtils.isEquals(rgb, i)) {
                                primary.add(ArrayUtils.indexOf(Storage.mojangColors, i));
                            }
                        }
                    }
                    for (Color c : fwm.getEffects().get(0).getFadeColors()) {
                        int[] rgb = new int[]{c.getRed(), c.getGreen(), c.getBlue()};
                        for (int[] i : Storage.mojangColors) {
                            if (ArrayUtils.isEquals(rgb, i)) {
                                fade.add(ArrayUtils.indexOf(Storage.mojangColors, i));
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
                    mat = SIGN;
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
            setSlot(i, inv, ChatColor.BLACK + "", null, STAINED_GLASS_PANE, 0, 1);
        }
        setSlot(53, inv, ChatColor.WHITE + "Next Page", null, ENCHANTED_BOOK, 0, 1);
        setSlot(45, inv, ChatColor.WHITE + "Previous Page", null, ENCHANTED_BOOK, 0, 1);
        switch (page) {
            case 1:
            case 2:
                for (int i : grayFill1) {
                    setSlot(i, inv, ChatColor.BLACK + "", null, STAINED_GLASS_PANE, 7, 1);
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
                setSlot(39, inv, ChatColor.WHITE + "Less Random Colors", null, GHAST_TEAR, 0, 1);
                if (numberRandom == 0) {
                    setSlot(40, inv, ChatColor.WHITE + "Random: " + ChatColor.RED + "Disabled", null, ENDER_PEARL, 0, 1);
                } else {
                    setSlot(40, inv, ChatColor.WHITE + "Random: " + ChatColor.GREEN + "Enabled", null, EYE_OF_ENDER, 0, numberRandom);
                }
                setSlot(41, inv, ChatColor.WHITE + "More Random Colors", null, SNOW_BALL, 0, 1);
                resetColors(inv, colors);
                break;
            case 3:
                for (int i : grayFill2) {
                    setSlot(i, inv, ChatColor.BLACK + "", null, STAINED_GLASS_PANE, 7, 1);
                }
                String flicker = (fwk.randomFlicker) ? ChatColor.GOLD + "Random"
                        : (fwk.flicker) ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled";
                String trail = (fwk.randomTrail) ? ChatColor.GOLD + "Random"
                        : (fwk.trail) ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled";
                setSlot(11, inv, "Flicker: " + flicker, null, GLOWSTONE_DUST, 0, 1);
                setTypeIcon(fwk, 13, inv);
                setSlot(15, inv, "Trail: " + trail, null, DIAMOND, 0, 1);
                setSlot(28, inv, "Decrease Power", null, GHAST_TEAR, 0, 1);
                setSlot(29, inv, "Power: " + ChatColor.GREEN + fwk.power, null, SULPHUR, 0, fwk.power);
                setSlot(30, inv, "Increase Power", null, SNOW_BALL, 0, 1);
                setSlot(32, inv, "Decrease Launch Height", null, GHAST_TEAR, 0, 1);
                setSlot(33, inv, "Launch Height: " + ChatColor.GREEN + fwk.launchHeight, null, SUGAR, 0, fwk.launchHeight);
                setSlot(34, inv, "Increase Launch Height", null, SNOW_BALL, 0, 1);
                if (fwk.randomPower) {
                    setSlot(38, inv, "Random: " + ChatColor.GREEN + "Enabled", null, EYE_OF_ENDER, 0, 1);
                } else {
                    setSlot(38, inv, "Random: " + ChatColor.RED + "Disabled", null, ENDER_PEARL, 0, 1);
                }
                if (fwk.randomLaunchHeight) {
                    setSlot(42, inv, "Random: " + ChatColor.GREEN + "Enabled", null, EYE_OF_ENDER, 0, 1);
                } else {
                    setSlot(42, inv, "Random: " + ChatColor.RED + "Disabled", null, ENDER_PEARL, 0, 1);
                }
                break;
            case 4:
                for (int i : grayFill3) {
                    setSlot(i, inv, ChatColor.BLACK + "", null, STAINED_GLASS_PANE, 7, 1);
                }
                setSlot(20, inv, ChatColor.AQUA + "Create Firework TNT", ChatColor.GREEN + "Shift-Click for a Stack", TNT, 0, 1);
                setSlot(24, inv, ChatColor.AQUA + "Create Firework Sign", ChatColor.GREEN + "Shift-Click for a Stack", SIGN, 0, 1);
                if (fwk.numberRandomFade != 0 || fwk.randomFlicker || fwk.randomLaunchHeight || fwk.randomPower
                        || fwk.numberRandomPrimary != 0 || fwk.randomTrail || fwk.type.equals("Random")) {
                    setSlot(30, inv, ChatColor.BLACK + "", null, STAINED_GLASS_PANE, 7, 1);
                    setSlot(32, inv, ChatColor.BLACK + "", null, STAINED_GLASS_PANE, 7, 1);
                } else {
                    setSlot(30, inv, ChatColor.AQUA + "Create Firework Rocket", ChatColor.GREEN + "Shift-Click for a Stack", FIREWORK, 0, 1);
                    setSlot(32, inv, ChatColor.AQUA + "Create Firework Star", ChatColor.GREEN + "Shift-Click for a Stack", FIREWORK_CHARGE, 0, 1);
                }
                break;
        }
        player.openInventory(inv);
    }

    private static void setTypeIcon(CraftedFirework fwk, int slot, Inventory inv) {
        Material fireworkType = null;
        short data = 0;
        switch (fwk.type) {
            case "Ball":
                fireworkType = PAPER;
                break;
            case "Large Ball":
                fireworkType = FIREBALL;
                break;
            case "Burst":
                fireworkType = FEATHER;
                break;
            case "Star":
                fireworkType = GOLD_NUGGET;
                break;
            case "Creeper":
                fireworkType = SKULL_ITEM;
                data = 4;
                break;
            case "Random":
                fireworkType = EYE_OF_ENDER;
                break;
        }
        setSlot(slot, inv, "Type: " + ChatColor.GOLD + fwk.type, null, fireworkType, data, 1);
    }

    // Sets inventory slot to specified item stack information
    private static void setSlot(int slot, Inventory inv, String name, String lore, Material mat, int data, int amount) {
        ItemStack stack = new ItemStack(mat, amount, (short) data);
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
        int[] dyeColors = new int[]{1, 14, 11, 10, 2, 12, 4, 7, 9, 13, 5, 6, 8, 3, 15, 0};
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType().equals(INK_SACK)) {
                short data = 0;
                if (colors.contains(15 - dyeColors[counter])) {
                    data = 1;
                }
                ItemStack stk = new ItemStack(INK_SACK, data, (short) dyeColors[counter]);
                ItemMeta m = stk.getItemMeta();
                m.setDisplayName(ChatColor.WHITE + StringUtils.capitalize(Storage.colors[15 - dyeColors[counter]].replace("_", " ")));
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
                if (inventory.getItem(slot) != null && inventory.getItem(slot).getType().equals(INK_SACK)) {
                    numberRandom = 0;
                    int color = 15 - inventory.getItem(slot).getData().getData();
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

    /**
     * @return the chromaCraftingEnabled
     */
    public static boolean isChromaCraftingEnabled() {
        return chromaCraftingEnabled;
    }

    /**
     * @param aChromaCraftingEnabled the chromaCraftingEnabled to set
     */
    public static void setChromaCraftingEnabled(boolean aChromaCraftingEnabled) {
        chromaCraftingEnabled = aChromaCraftingEnabled;
    }

}
