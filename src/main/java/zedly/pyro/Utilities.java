package zedly.pyro;

import java.lang.reflect.Field;
import java.util.*;
import net.minecraft.server.v1_15_R1.DataWatcher;
import net.minecraft.server.v1_15_R1.DataWatcherObject;
import net.minecraft.server.v1_15_R1.DataWatcherRegistry;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityMetadata;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

public class Utilities {

    private static final char[] BASE64_CHARS
            = {'0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
                'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
                'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
                'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
                'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                'u', 'v', 'w', 'x', 'y', 'z', '-', '+'};

    //All related to Chromatic Armor and transitioning colors
    private static final char[] HEX_CHARS
            = {0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37,
                0x38, 0x39, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66};

    private static boolean nmsDetected = false;
    private static FakeEntitySender fakeEntitySender;

    static {
        try {
            Class.forName("net.minecraft.server.v1_15_R1.EntityPlayer");
            nmsDetected = true;
            fakeEntitySender = new FakeEntitySender();
            System.out.println("Compatible NMS version detected");
        } catch (Exception e) {
            System.out.println("NMS version not compatible!");
            fakeEntitySender = null;
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

    // Related to Firework Generation
    public static ItemStack generateFireworkItem(CraftedFirework cf, Material mat) {
        ItemStack is = new ItemStack(mat, 1);
        if (mat == Material.FIREWORK_ROCKET || mat == Material.FIREWORK_STAR) {
            ArrayList<Color> primaryColors = new ArrayList<>();
            for (int c : cf.primary) {
                primaryColors.add(Color.fromRGB(Storage.FW_COLOR_RGB_RICH[c][0], Storage.FW_COLOR_RGB_RICH[c][1], Storage.FW_COLOR_RGB_RICH[c][2]));
            }
            ArrayList<Color> fadeColors = new ArrayList<>();
            for (int c : cf.fade) {
                fadeColors.add(Color.fromRGB(Storage.FW_COLOR_RGB_RICH[c][0], Storage.FW_COLOR_RGB_RICH[c][1], Storage.FW_COLOR_RGB_RICH[c][2]));
            }
            FireworkEffect fe = FireworkEffect.builder().withColor(primaryColors).withFade(fadeColors).trail(cf.trail).flicker(cf.flicker).with(getFireworkType(cf.type)).build();
            if (mat == Material.FIREWORK_ROCKET) {
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
    }

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
        int firstQuintuplet = (int) parseLong64(s, 0, 5);
        int secondQuintuplet = (int) parseLong64(s, 5, 10);
        int thirdQuintuplet = (int) parseLong64(s, 10, 15);

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
            bu = bu.withColor(Color.fromRGB(Storage.FW_COLOR_RGB_RICH[primaries[i]][0], Storage.FW_COLOR_RGB_RICH[primaries[i]][1], Storage.FW_COLOR_RGB_RICH[primaries[i]][2]));
        }
        for (int i = 0; i < fadeLen; i++) {
            bu = bu.withFade(Color.fromRGB(Storage.FW_COLOR_RGB_RICH[fades[i]][0], Storage.FW_COLOR_RGB_RICH[fades[i]][1], Storage.FW_COLOR_RGB_RICH[fades[i]][2]));
        }
        bu = bu.flicker(flicker);
        bu = bu.trail(trail);
        bu = bu.with(FireworkEffect.Type.values()[typeIndex]);
        Location loc = l.clone().add(new org.bukkit.util.Vector(0.5, 0.5 + launchHeight, 0.5));
        if (power == 0) {
            try {
                FireworkEffectPlayer.playFirework(loc, bu.build());
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

    /**
     * Checks if the given Block is any of the materials representing a type of
     * sign in 1.14.4.
     *
     * @param block
     * @return true if the block is a sign
     */
    public static boolean isSign(Block block) {
        return block != null && isMaterialSign(block.getType());
    }

    public static boolean isMaterialSign(final Material material) {
        switch (material) {
            case ACACIA_SIGN:
            case ACACIA_WALL_SIGN:
            case BIRCH_SIGN:
            case BIRCH_WALL_SIGN:
            case DARK_OAK_SIGN:
            case DARK_OAK_WALL_SIGN:
            case JUNGLE_SIGN:
            case JUNGLE_WALL_SIGN:
            case OAK_SIGN:
            case OAK_WALL_SIGN:
            case SPRUCE_SIGN:
            case SPRUCE_WALL_SIGN:
                return true;
            default:
                return false;
        }
    }

    public static boolean isMaterialDye(final Material material) {
        return ArrayUtils.contains(Storage.FW_COLOR_ICON_MATS, material);
    }

    public static String toBase64(int i) {
        return "" + BASE64_CHARS[((i >> 24) & 0x3F)] + BASE64_CHARS[((i >> 18) & 0x3F)] + BASE64_CHARS[((i >> 12) & 0x3F)] + BASE64_CHARS[((i >> 6) & 0x3F)] + BASE64_CHARS[(i & 0x3F)];
    }

    /**
     * Parses substrings of the Tesseract base64 encoding scheme.
     *
     * @param s_source
     * @param start
     * @param end
     * @return the parsed bits as a long integer
     */
    public static long parseLong64(String s_source, int start, int end) {
        char[] source = s_source.toCharArray();
        long result = 0;
        for (int i = start; i < end; i++) {
            result |= ((long) (ArrayUtils.indexOf(BASE64_CHARS, source[i])) << (6 * (end - i - 1)));
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

    public static String hexString(byte[] binary) {
        char[] hex = new char[binary.length * 2];
        for (int i = 0; i < binary.length; i++) {
            hex[2 * i] = HEX_CHARS[(binary[i] >> 4) & 0xF];
            hex[2 * i + 1] = HEX_CHARS[binary[i] & 0xF];
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
                block.breakNaturally();
            }
            return true;
        }
        return false;
    }

    public static boolean setEntityGlowing(Entity entity, Player viewer, boolean glowing) {
        if (!nmsDetected) {
            return false;
        }
        return fakeEntitySender.setEntityGlowing(entity, viewer, glowing);
    }

    private static class FakeEntitySender {

        private FakeEntitySender() {
        }

        public boolean setEntityGlowing(Entity entity, Player player, boolean glowing) {
            if (!nmsDetected) {
                return false;
            }

            int entityId = entity.getEntityId();

            DataWatcherObject<Byte> dwo0 = new DataWatcherObject<>(0, DataWatcherRegistry.a); // Indicating a metadata value of type Byte at index 0
            DataWatcher.Item<Byte> dwi0 = new DataWatcher.Item<>(dwo0, (byte) 0x60); // A Metadata item of type Byte with value 0x60

            List<DataWatcher.Item> dwiList = new ArrayList<>();
            dwiList.add(dwi0);

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
    }
    
    /**
     * Match an item stack against a number of criteria. Eliminates huge chains
     * of null checks
     *
     * @param is the item stack to match
     * @param mat the material to look for. null if irrelevant
     * @param durability the damage value to look for. -1 if irrelevant
     * @param name the name the item must have. null if irrelevant
     * @param lore a line of lore that must be contained in the item stack. null
     * if irrelevant
     * @return true if the item stack matches all specified criteria
     */
    public static boolean matchItemStack(ItemStack is, Material mat, String name, String lore) {
        if (is == null) {
            return false;
        }
        if (mat != null && is.getType() != mat) {
            return false;
        }
        if ((name != null || lore != null) && !is.hasItemMeta()) {
            return false;
        }
        if (name != null && !(is.getItemMeta().hasDisplayName() || !is.getItemMeta().getDisplayName().equals(name))) {
            return false;
        }
        if (lore != null) {
            if (is.getItemMeta().hasLore()) {
                for (String loreLine : is.getItemMeta().getLore()) {
                    if (loreLine.equals(lore)) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }

}
