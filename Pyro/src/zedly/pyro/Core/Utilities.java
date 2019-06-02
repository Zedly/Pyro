package zedly.pyro.Core;


import org.bukkit.*;
import org.bukkit.block.Block;

import org.bukkit.entity.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.Damageable;

import java.util.LinkedHashMap;
import java.util.Map;

public class Utilities {

    private static boolean nmsDetected = false;
    //private static FakeEntitySender fakeEntitySender;

    static {
        try {
            Class.forName("net.minecraft.server.v1_12_R1.EntityPlayer");
            nmsDetected = true;
            //fakeEntitySender = new FakeEntitySender();
            System.out.println("Compatible NMS version detected");
        } catch (Exception e) {
            System.out.println("NMS version not compatible!");
            //fakeEntitySender = null;
        }
    }

    // Retuns the center of the block at the specified location
    public static Location getCenter(Location loc) {
        double x = loc.getX();
        double z = loc.getZ();
        if (x >= 0) {
            x += .5;
        } else {
            x += .5;
        }
        if (z >= 0) {
            z += .5;
        } else {
            z += .5;
        }
        Location lo = loc.clone();
        lo.setX(x);
        lo.setZ(z);
        return lo;
    }

    /*
    // Related to Firework Generation
    public static ItemStack generateFireworkItem(CraftedFirework cf, Material mat) {
        ItemStack is = new ItemStack(mat, 1);
        if (mat == Material.FIREWORK || mat == Material.FIREWORK_CHARGE) {
            ArrayList<Color> primaryColors = new ArrayList<>();
            for (int c : cf.primary) {
                primaryColors.add(Color.fromRGB(Storage.color[c][0], Storage.color[c][1], Storage.color[c][2]));
            }
            ArrayList<Color> fadeColors = new ArrayList<>();
            for (int c : cf.fade) {
                fadeColors.add(Color.fromRGB(Storage.color[c][0], Storage.color[c][1], Storage.color[c][2]));
            }
            FireworkEffect fe = FireworkEffect.builder().withColor(primaryColors).withFade(fadeColors).trail(cf.trail).flicker(cf.flicker).with(getFireworkType(cf.type)).build();
            if (mat == Material.FIREWORK) {
                FireworkMeta fm = (FireworkMeta) is.getItemMeta();
                fm.addEffect(fe);
                is.setItemMeta(fm);
            } else {
                FireworkEffectMeta fm = (FireworkEffectMeta) is.getItemMeta();
                fm.setEffect(fe);
                is.setItemMeta(fm);
            }
        } else {
            int firstQuintuplet, secondQuintuplet, thirdQuintuplet;
            firstQuintuplet = (cf.power << 28)
                    | ((cf.launchHeight & 0xF) << 24)
                    | ((cf.randomPower) ? 1 << 23 : 0)
                    | ((cf.randomLaunchHeight) ? 1 << 22 : 0)
                    | ((cf.type.toUpperCase().equals("RANDOM")) ? 1 << 21 : 0)
                    | ((cf.randomTrail) ? 1 << 20 : 0)
                    | ((cf.randomFlicker) ? 1 << 19 : 0)
                    | ((9 * cf.primary.size() + cf.fade.size()) << 9)
                    | (effectIndex(getFireworkType(cf.type)) << 6)
                    | (cf.trail ? 1 << 5 : 0)
                    | (cf.flicker ? 1 << 4 : 0)
                    | ((cf.primary.size() >= 1) ? cf.primary.get(0) : 0);
            if (cf.numberRandomPrimary != 0) {
                firstQuintuplet |= 1 << 18;
                secondQuintuplet = cf.numberRandomPrimary;
            } else {
                secondQuintuplet = ((cf.primary.size() >= 2) ? (cf.primary.get(1) << 26) : 0)
                        | ((cf.primary.size() >= 3) ? (cf.primary.get(2) << 22) : 0)
                        | ((cf.primary.size() >= 4) ? (cf.primary.get(3) << 18) : 0)
                        | ((cf.primary.size() >= 5) ? (cf.primary.get(4) << 14) : 0)
                        | ((cf.primary.size() >= 6) ? (cf.primary.get(5) << 10) : 0)
                        | ((cf.primary.size() >= 7) ? (cf.primary.get(6) << 6) : 0)
                        | ((cf.primary.size() >= 8) ? (cf.primary.get(7) << 2) : 0)
                        | ((cf.fade.size() >= 1) ? (cf.fade.get(0) >> 2) : 0);
            }
            if (cf.numberRandomFade != 0) {
                thirdQuintuplet = cf.numberRandomFade;
                secondQuintuplet = cf.numberRandomPrimary;
            } else {
                thirdQuintuplet = ((cf.fade.size() >= 1) ? ((cf.fade.get(0) & 0x3) << 28) : 0)
                        | ((cf.fade.size() >= 2) ? (cf.fade.get(1) << 24) : 0)
                        | ((cf.fade.size() >= 3) ? (cf.fade.get(2) << 20) : 0)
                        | ((cf.fade.size() >= 4) ? (cf.fade.get(3) << 16) : 0)
                        | ((cf.fade.size() >= 5) ? (cf.fade.get(4) << 12) : 0)
                        | ((cf.fade.size() >= 6) ? (cf.fade.get(5) << 8) : 0)
                        | ((cf.fade.size() >= 7) ? (cf.fade.get(6) << 4) : 0)
                        | ((cf.fade.size() >= 8) ? (cf.fade.get(7)) : 0);
            }
            String lore = toBase64(firstQuintuplet) + toBase64(secondQuintuplet) + toBase64(thirdQuintuplet);
            ItemMeta im = is.getItemMeta();
            ArrayList<String> il = new ArrayList<>();
            if (mat == Material.TNT) {
                il.add(ChatColor.GOLD + "Firework TNT");
            } else {
                il.add(ChatColor.GOLD + "Firework Sign");
            }
            il.add(ChatColor.GREEN + lore);
            im.setLore(il);
            is.setItemMeta(im);
        }
        return is;
    }*/

    // Returns the correct FireworkType Enum based on the String
    private static FireworkEffect.Type getFireworkType(String type) {
        switch (type.toUpperCase()) {
            case "LARGE BALL":
                return FireworkEffect.Type.BALL_LARGE;
            case "RANDOM":
                return null;
            default:
                return FireworkEffect.Type.valueOf(type.toUpperCase());
        }
    }

    // Creates firework when firework signs are activated
    public static void explodeFromString(Location l, String s) {
        if (s.length() < 15) {
            return;
        }
        int[] primaries = new int[8];
        int[] fades = new int[8];

        // PPHHHH rrrrrr rriiii iiiTTT tfAAAA BBBBCC CCDDDD EEEEFF FFGGGG HHHHaa aabbbb ccccdd ddeeee ffffgg gghhhh
        int firstQuintuplet = Utilities.fromBase64(s.substring(0, 5));
        int secondQuintuplet = Utilities.fromBase64(s.substring(5, 10));
        int thirdQuintuplet = Utilities.fromBase64(s.substring(10, 15));

        int randomFlags = (firstQuintuplet >> 16) & 0xFF;
        int power = (randomFlags & 0x80) != 0 ? Storage.rnd.nextInt(4) : firstQuintuplet >> 28;
        int launchHeight = (randomFlags & 0x40) != 0 ? Storage.rnd.nextInt(16) : (firstQuintuplet >> 24) & 0xF;
        int primaryLen = ((firstQuintuplet >> 9) & 0x7F) / 9;
        int fadeLen = ((firstQuintuplet >> 9) & 0x7F) % 9;
        int typeIndex = (randomFlags & 0x20) != 0 ? Storage.rnd.nextInt(5) : (firstQuintuplet >> 6) & 0x7;
        boolean trail = (randomFlags & 0x10) != 0 ? Storage.rnd.nextBoolean() : (firstQuintuplet & (1 << 5)) != 0;
        boolean flicker = (randomFlags & 0x08) != 0 ? Storage.rnd.nextBoolean() : (firstQuintuplet & (1 << 4)) != 0;

        if (primaryLen == 0) {
            primaryLen = secondQuintuplet;
            primaries = new int[primaryLen];
            for (int i = 0; i < primaryLen; i++) {
                primaries[i] = Storage.rnd.nextInt(16);
            }
        } else {
            primaries[0] = firstQuintuplet & 0xF;
            primaries[1] = (secondQuintuplet >> 26) & 0xF;
            primaries[2] = (secondQuintuplet >> 22) & 0xF;
            primaries[3] = (secondQuintuplet >> 18) & 0xF;
            primaries[4] = (secondQuintuplet >> 24) & 0xF;
            primaries[5] = (secondQuintuplet >> 10) & 0xF;
            primaries[6] = (secondQuintuplet >> 6) & 0xF;
            primaries[7] = (secondQuintuplet >> 2) & 0xF;
        }

        if (fadeLen == 0) {
            fadeLen = thirdQuintuplet;
            fades = new int[fadeLen];
            for (int i = 0; i < fadeLen; i++) {
                fades[i] = Storage.rnd.nextInt(16);
            }
        } else {
            fades[0] = ((thirdQuintuplet >> 28) & 0x3) | (secondQuintuplet >> 2) & 0xC;
            fades[1] = (thirdQuintuplet >> 24) & 0xF;
            fades[2] = (thirdQuintuplet >> 20) & 0xF;
            fades[3] = (thirdQuintuplet >> 16) & 0xF;
            fades[4] = (thirdQuintuplet >> 12) & 0xF;
            fades[5] = (thirdQuintuplet >> 8) & 0xF;
            fades[6] = (thirdQuintuplet >> 4) & 0xF;
            fades[7] = thirdQuintuplet & 0xF;
        }

        FireworkEffect.Builder bu = FireworkEffect.builder();
        for (int i = 0; i < primaryLen; i++) {
            bu = bu.withColor(Color.fromRGB(Storage.color[primaries[i]][0], Storage.color[primaries[i]][1], Storage.color[primaries[i]][2]));
        }
        for (int i = 0; i < fadeLen; i++) {
            bu = bu.withFade(Color.fromRGB(Storage.color[fades[i]][0], Storage.color[fades[i]][1], Storage.color[fades[i]][2]));
        }
        bu = bu.flicker(flicker);
        bu = bu.trail(trail);
        bu = bu.with(FireworkEffect.Type.values()[typeIndex]);
        Location loc = l.clone().add(new org.bukkit.util.Vector(0.5, 0.5 + launchHeight, 0.5));
        if (power == 0) {
            try {
                //FireworkEffectPlayer.playFirework(loc, bu.build());
            } catch (Exception ex) {
            }
        } else {
            Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            FireworkMeta fm = fw.getFireworkMeta();
            fm.addEffect(bu.build());
            fm.setPower(power);
            fw.setFireworkMeta(fm);
        }
    }

    // Creates firework on teleport with Chromatic Armor
    public static void explodeFromArmor(ItemStack is, Location loc) {
        if (is != null && Storage.COMPATIBILITY_ADAPTER.LeatherArmors().contains(is.getType()) && is.getItemMeta().hasLore()) {
            if (is.getItemMeta().hasLore() && is.getItemMeta().getLore().get(0).startsWith(ChatColor.GREEN + "Chromatic Armor")) {
                LeatherArmorMeta lm = (LeatherArmorMeta) is.getItemMeta();
                Color rgb = lm.getColor();
                try {
                    //FireworkEffectPlayer.playFirework(loc, FireworkEffect.Type.BALL, rgb, false, true);
                } catch (Exception ex) {
                }
            }
        }
    }

    public static String toBase64(int i) {
        String[] chars = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "-", "+"};
        return chars[((i >> 24) & 0x3F)] + chars[((i >> 18) & 0x3F)] + chars[((i >> 12) & 0x3F)] + chars[((i >> 6) & 0x3F)] + chars[(i & 0x3F)];
    }

    public static int fromBase64(String s) {
        char[] c = s.toCharArray();
        int result = 0;
        for (int i = 0; i < 5; i++) {
            int l = c[4 - i] & 0x0F;
            switch (c[4 - i] & 0xF0) {
                case 0x30: {
                    if (l > 9) {
                        return 0;
                    } else {
                        result |= (l << (6 * i));
                    }
                    break;
                }
                case 0x40: {
                    if (l == 0) {
                        return 0;
                    } else {
                        result |= ((9 + l) << (6 * i));
                    }
                    break;
                }
                case 0x50: {
                    if (l > 10) {
                        return 0;
                    } else {
                        result |= ((25 + l) << (6 * i));
                    }
                    break;
                }
                case 0x60: {
                    if (l == 0) {
                        return 0;
                    } else {
                        result |= ((35 + l) << (6 * i));
                    }
                    break;
                }
                case 0x70: {
                    if (l > 10) {
                        return 0;
                    } else {
                        result |= ((51 + l) << (6 * i));
                    }
                    break;
                }
                default: {
                    if (c[4 - i] == 45) {
                        result |= 62 << (6 * i);
                    } else if (c[4 - i] == 43) {
                        result |= 63 << (6 * i);
                    } else {
                        return 0;
                    }
                }
            }
        }
        return result;
    }

    private static int effectIndex(FireworkEffect.Type type) {
        for (int i = 0; i < FireworkEffect.Type.values().length; i++) {
            if (FireworkEffect.Type.values()[i] == type) {
                return i;
            }
        }
        return 0;
    }

    //All related to Chromatic Armor and transitioning colors
    private static final char[] hexValues = {0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66};

    public static String hexString(byte[] binary) {
        char[] hex = new char[binary.length * 2];
        for (int i = 0; i < binary.length; i++) {
            hex[2 * i] = hexValues[(binary[i] >> 4) & 0xF];
            hex[2 * i + 1] = hexValues[binary[i] & 0xF];
        }
        return new String(hex);
    }

    public static int[] getColor(double[] params, int counter) {
        if (params.length != 10) {
            throw new IllegalArgumentException();
        }
        counter += params[7];
        double redFreq = (params[0] / 180.0 * (Math.PI)) * (params[8] / 100.0);
        double greenFreq = (params[1] / 180.0 * (Math.PI)) * (params[8] / 100.0);
        double blueFreq = (params[2] / 180.0 * (Math.PI)) * (params[8] / 100.0);
        int red = (int) ((Math.pow(Math.sin(redFreq * counter + (params[3] * Math.PI / 180.0)), params[9]) * params[6]) + 255 - params[6]);
        int green = (int) ((Math.pow(Math.sin(greenFreq * counter + (params[4] * Math.PI / 180.0)), params[9]) * params[6]) + 255 - params[6]);
        int blue = (int) ((Math.pow(Math.sin(blueFreq * counter + (params[5] * Math.PI / 180.0)), params[9]) * params[6]) + 255 - params[6]);
        return new int[]{red, green, blue};
    }

    public static int[] getThemedColor(double[] params, int counter) throws IllegalArgumentException {
        if (params.length != 12) {
            throw new IllegalArgumentException();
        }
        double h = params[6] + params[3] * Math.sin(params[0] * ((counter + params[9]) * Math.PI / 180.0));
        double s = params[7] + params[4] * Math.sin(params[1] * ((counter + params[10]) * Math.PI / 180.0));
        double v = params[8] + params[5] * Math.sin(params[2] * ((counter + params[11]) * Math.PI / 180.0));
        return HsvToRgb(h, s, v);
    }

    public static int[] HsvToRgb(double h, double S, double V) {
        double H = h;
        while (H < 0) {
            H += 360;
        }
        while (H >= 360) {
            H -= 360;
        }
        double R = 0, G = 0, B = 0;
        if (V <= 0) {
            R = G = B = 0;
        } else if (S <= 0) {
            R = G = B = V;
        } else {
            double hf = H / 60.0;
            int i = (int) Math.floor(hf);
            double f = hf - i;
            double pv = V * (1 - S);
            double qv = V * (1 - S * f);
            double tv = V * (1 - S * (1 - f));
            switch (i) {
                case 0:
                    R = V;
                    G = tv;
                    B = pv;
                    break;
                case 1:
                    R = qv;
                    G = V;
                    B = pv;
                    break;
                case 2:
                    R = pv;
                    G = V;
                    B = tv;
                    break;
                case 3:
                    R = pv;
                    G = qv;
                    B = V;
                    break;
                case 4:
                    R = tv;
                    G = pv;
                    B = V;
                    break;
                case 5:
                    R = V;
                    G = pv;
                    B = qv;
                    break;
                case 6:
                    R = V;
                    G = tv;
                    B = pv;
                    break;
                case -1:
                    R = V;
                    G = pv;
                    B = qv;
                    break;
            }
        }
        int r = clamp((int) (R * 255.0));
        int g = clamp((int) (G * 255.0));
        int b = clamp((int) (B * 255.0));
        return new int[]{r, g, b};
    }

    public static int clamp(int i) {
        if (i < 0) {
            return 0;
        }
        if (i > 255) {
            return 255;
        }
        return i;
    }

    public static double[] parseParameters(String parameterSet) {
        String[] segments = parameterSet.split(":");
        double[] params = new double[segments.length];
        for (int i = 0; i < segments.length; i++) {
            params[i] = Double.parseDouble(segments[i]);
        }
        return params;
    }

    public static boolean tryBreak(Player player, Block block, boolean doBreak) {
        BlockBreakEvent event = new BlockBreakEvent(block, player);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            if (doBreak) {
                block.setType(Material.AIR);
            }
            return true;
        }
        return false;
    }

    public static boolean setEntityGlowing(Entity entity, Player viewer, boolean glowing) {
        if (!nmsDetected) {
            return false;
        }
        return false;
        //return fakeEntitySender.setEntityGlowing(entity, viewer, glowing);
    }

    public static void setDamage(ItemStack is, int damage) {
        if (is.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable) {
            org.bukkit.inventory.meta.Damageable dm = ((Damageable) is.getItemMeta());
            dm.setDamage(damage);
            is.setItemMeta((ItemMeta) dm);
        }
    }

    public static int getDamage(ItemStack is) {
        if (is.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable) {
            org.bukkit.inventory.meta.Damageable dm = ((Damageable) is.getItemMeta());
            return dm.getDamage();
        }
        return 0;
    }

    // Encodes a given string to be invisible to players surrounded by the escape sequence "\< \>"
    public static String toInvisibleString(String str) {
        str = "\\<" + str + "\\>" + ChatColor.COLOR_CHAR + 'F';
        StringBuilder builder = new StringBuilder();
        for (char c : str.toCharArray()) {
            builder.append(ChatColor.COLOR_CHAR);
            builder.append(c);
        }
        return builder.toString();
    }

    // Returns a map of strings to booleans, where the boolean represents visibility
    public static Map<String, Boolean> fromInvisibleString(String str) {
        Map<String, Boolean> strs = new LinkedHashMap<>();

        int state = 0; // 0 = close, 1 = waiting for next to open, 2 = open, 3 = waiting for next to close
        StringBuilder builder = new StringBuilder();
        for (char c : str.toCharArray()) {
            switch (state) {
                case 0: // Visible, waiting for '§'
                    if (c == ChatColor.COLOR_CHAR) {
                        state = 1;
                    } else {
                        builder.append(c);
                    }
                    break;
                case 1: // Got a '§', waiting for '\'
                    if (c == '\\') {
                        state = 2;
                    } else if (c == ChatColor.COLOR_CHAR) {
                        builder.append(ChatColor.COLOR_CHAR);
                    } else {
                        builder.append(ChatColor.COLOR_CHAR);
                        builder.append(c);
                        state = 0;
                    }
                    break;
                case 2: // Got a '\', waiting for '§'
                    if (c == ChatColor.COLOR_CHAR) {
                        state = 3;
                    } else {
                        builder.append(ChatColor.COLOR_CHAR);
                        builder.append('\\');
                        builder.append(c);
                        state = 0;
                    }
                    break;
                case 3: // Got a '§', waiting for '<'
                    if (c == '<') {
                        state = 4;
                        if (builder.length() != 0) {
                            strs.put(builder.toString(), true);
                            builder = new StringBuilder();
                        }
                    } else if (c == ChatColor.COLOR_CHAR) {
                        builder.append(ChatColor.COLOR_CHAR);
                        builder.append('\\');
                        state = 1;
                    } else {
                        builder.append(ChatColor.COLOR_CHAR);
                        builder.append('\\');
                        builder.append(ChatColor.COLOR_CHAR);
                        builder.append(c);
                        state = 0;
                    }
                    break;
                case 4: // Invisible, ignore '§'
                    state = 5;
                    break;
                case 5: // Invisible, waiting for '\'
                    if (c == '\\') {
                        state = 6;
                    } else {
                        builder.append(c);
                        state = 4;
                    }
                    break;
                case 6: // Got '\', waiting for '§'
                    if (c == ChatColor.COLOR_CHAR) {
                        state = 7;
                    } else {
                        builder.append('\\');
                        state = 5;
                    }
                    break;
                case 7: // Got '§', waiting for '>'
                    if (c == '>') {
                        state = 0;
                        if (builder.length() != 0) {
                            strs.put(builder.toString(), false);
                            builder = new StringBuilder();
                        }
                    } else {
                        builder.append('\\');
                        builder.append(c);
                        state = 4;
                    }
                    break;
            }
        }
        if (builder.length() != 0) {
            strs.put(builder.toString(), true);
        }
        return strs;
    }



    /*
    private static class FakeEntitySender {

        private FakeEntitySender() {
        }

        public boolean setEntityGlowing(Entity entity, Player player, boolean glowing) {
            if (!nmsDetected) {
                return false;
            }

            int entityId = entity.getEntityId();

            DataWatcherObject<Byte> dwo0 = new DataWatcherObject<>(0, DataWatcherRegistry.a); // Indicating a metadata value of type Byte at index 0
            DataWatcherObject<Boolean> dwo5 = new DataWatcherObject<>(5, DataWatcherRegistry.h); // Indicating a metadata value of type Boolean at index 5
            DataWatcher.Item<Byte> dwi0 = new DataWatcher.Item<>(dwo0, (byte) 0x60); // A Metadata item of type Byte with value 0x60
            DataWatcher.Item<Boolean> dwi5 = new DataWatcher.Item<>(dwo5, glowing); // A Metadata item of type Boolean with value true

            List<DataWatcher.Item> dwiList = new ArrayList<>();
            dwiList.add(dwi0);
            dwiList.add(dwi5);

            PacketPlayOutEntityMetadata ppoem = new PacketPlayOutEntityMetadata();
            Class clazz = ppoem.getClass();

            try {
                // Entity ID and DataWatcher items
                Field f = clazz.getDeclaredField("a");
                f.setAccessible(true);
                f.setInt(ppoem, entityId);
                f = clazz.getDeclaredField("b");
                f.setAccessible(true);
                f.set(ppoem, dwiList);
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                ex.printStackTrace();
                return false;
            }

            EntityPlayer ep = ((CraftPlayer) player).getHandle();
            ep.playerConnection.networkManager.sendPacket(ppoem);
            return true;
        }
    }*/

}
