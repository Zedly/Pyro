package zedly.pyro;

import java.util.*;
import org.bukkit.*;
import static org.bukkit.Material.*;

public class Storage {

    //Variables
    public static Pyro pyro;
    public static final Random rnd = new Random();
    public static final String colorString;

    //Pre-defined Variables
    public static final String logo = ChatColor.DARK_RED + "[" + ChatColor.GOLD + "Pyro" + ChatColor.DARK_RED + "]" + ChatColor.YELLOW + "";
    public static final Material[] leather = new Material[]{LEATHER_BOOTS, LEATHER_CHESTPLATE, LEATHER_HELMET, LEATHER_LEGGINGS};

    public static final int[] RAINBOW_COLORS = {0xFF0000, 0xFF8800, 0xFFFF00, 0x88FF00, 0x00FF00, 0x00FF88, 0x00FFFF, 0x0088FF, 0x0000FF, 0x8800FF, 0xFF00FF, 0xFF0088};

    // TODO: replace this with an array of tuples
    public static final Material[] FW_COLOR_ICON_MATS = {RED_DYE, ORANGE_DYE, YELLOW_DYE, LIME_DYE,
        GREEN_DYE, LIGHT_BLUE_DYE, BLUE_DYE, LIGHT_GRAY_DYE, PINK_DYE, MAGENTA_DYE,
        PURPLE_DYE, CYAN_DYE, GRAY_DYE, BROWN_DYE, WHITE_DYE, BLACK_DYE};

    public static final String[] FW_COLOR_FRIENDLY_NAMES = {
        "red", "orange", "yellow", "light_green",
        "dark_green", "light_blue", "dark_blue", "light_gray",
        "pink", "magenta", "purple", "aqua",
        "dark_gray", "brown", "white", "black"};

    public static final int[][] FW_COLOR_RGB_RICH = {
        {255, 0, 0}, {255, 127, 0}, {255, 255, 0}, {0, 255, 0},
        {0, 127, 0}, {0, 255, 255}, {0, 0, 255}, {127, 127, 127},
        {255, 0, 127}, {255, 0, 255}, {0, 127, 127}, {0, 127, 127},
        {63, 63, 63}, {127, 63, 0}, {255, 255, 255}, {0, 0, 0}};

    public static final int[][] FW_COLOR_RGB_MOJANG = {
        {179, 49, 44}, {235, 136, 68}, {222, 207, 42}, {65, 205, 52},
        {59, 81, 26}, {102, 137, 211}, {37, 49, 146}, {171, 171, 171},
        {216, 129, 152}, {195, 84, 205}, {123, 47, 190}, {40, 118, 151},
        {67, 67, 67}, {81, 48, 26}, {240, 240, 240}, {30, 27, 27}};    

    static {
        String col = ChatColor.GOLD + "Colors: " + ChatColor.YELLOW + "";
        for (int i = 0; i < FW_COLOR_FRIENDLY_NAMES.length; i++) {
            col += FW_COLOR_FRIENDLY_NAMES[i];
            if (i != FW_COLOR_FRIENDLY_NAMES.length - 1) {
                col += ChatColor.GOLD + ", " + ChatColor.YELLOW + "";
            }
        }
        colorString = col;
    }
}
