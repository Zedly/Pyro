package zedly.pyro;

import java.util.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import static org.bukkit.Material.*;
import org.bukkit.inventory.ItemStack;

public class Storage {

    //Variables
    public static Pyro pyro;
    public static final Random rnd = new Random();
    public static boolean globalparty = false;
    public static String colorString;

    //Pre-defined Variables
    public static final String logo = ChatColor.DARK_RED + "[" + ChatColor.GOLD + "Pyro" + ChatColor.DARK_RED + "]" + ChatColor.YELLOW + "";
    public static Material[] leather = new Material[]{LEATHER_BOOTS, LEATHER_CHESTPLATE, LEATHER_HELMET, LEATHER_LEGGINGS};

    public static Integer[] badIds = new Integer[]{0, 8, 9, 10, 11, 26, 34, 36, 43, 51, 55, 59, 63, 64, 68, 71, 74, 75, 83, 90, 92, 93, 94, 104, 105, 115, 117,
        118, 119, 124, 125, 127, 132, 140, 141, 142, 144, 149, 150, 176, 177, 178, 181, 193, 194, 195, 196, 197};

    public static final int[] rainbowcolors = {0xFF0000, 0xFF8800, 0xFFFF00, 0x88FF00, 0x00FF00, 0x00FF88, 0x00FFFF, 0x0088FF, 0x0000FF, 0x8800FF, 0xFF00FF, 0xFF0088};

    public static final Material[] rainbowGlass = {RED_STAINED_GLASS, ORANGE_STAINED_GLASS, YELLOW_STAINED_GLASS, GREEN_STAINED_GLASS,
        LIGHT_BLUE_STAINED_GLASS, BLUE_STAINED_GLASS, PURPLE_STAINED_GLASS};

    public static final Material[] EASTER_EGG_TYPES = {
        ELDER_GUARDIAN_SPAWN_EGG, WITHER_SKELETON_SPAWN_EGG, STRAY_SPAWN_EGG, HUSK_SPAWN_EGG,
        ZOMBIE_VILLAGER_SPAWN_EGG, SKELETON_HORSE_SPAWN_EGG, ZOMBIE_HORSE_SPAWN_EGG,
        DONKEY_SPAWN_EGG, MULE_SPAWN_EGG, EVOKER_SPAWN_EGG, VEX_SPAWN_EGG,
        VINDICATOR_SPAWN_EGG, CREEPER_SPAWN_EGG, SKELETON_SPAWN_EGG, SPIDER_SPAWN_EGG,
        ZOMBIE_SPAWN_EGG, SLIME_SPAWN_EGG, GHAST_SPAWN_EGG, ZOMBIE_PIGMAN_SPAWN_EGG,
        ENDERMAN_SPAWN_EGG, CAVE_SPIDER_SPAWN_EGG, SILVERFISH_SPAWN_EGG,
        BLAZE_SPAWN_EGG, MAGMA_CUBE_SPAWN_EGG, WITHER_SKELETON_SPAWN_EGG, BAT_SPAWN_EGG,
        WITCH_SPAWN_EGG, ENDERMITE_SPAWN_EGG, GUARDIAN_SPAWN_EGG, SHULKER_SPAWN_EGG,
        PIG_SPAWN_EGG, SHEEP_SPAWN_EGG, COW_SPAWN_EGG, CHICKEN_SPAWN_EGG,
        SQUID_SPAWN_EGG, WOLF_SPAWN_EGG, MOOSHROOM_SPAWN_EGG, OCELOT_SPAWN_EGG, HORSE_SPAWN_EGG,
        RABBIT_SPAWN_EGG, POLAR_BEAR_SPAWN_EGG, LLAMA_SPAWN_EGG, VILLAGER_SPAWN_EGG
    };

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

    //Collections
    public static final HashMap<String, Boolean> recipes = new HashMap<>();
    public static final HashSet<Snowball> bangBalls = new HashSet<>();
    public static final HashMap<Integer, TNTExplosion> explodingEntities = new HashMap<>();
    public static final HashMap<Block, TNTExplosion> explodingBlocks = new HashMap<>();
    public static final HashMap<String, ArrayList<Block>> remoteTnt = new HashMap<>();
    public static final HashSet<TNTPrimed> tntEntities = new HashSet<>();
    public static final HashSet<Block> poweredBlocks = new HashSet<>();
    public static final HashMap<Item, ItemStack> eastereggs = new HashMap<>();
    public static final HashSet<Entity> snowballs = new HashSet<>();
    public static final HashMap<Block, Long> rainbowBlocks = new HashMap<>();
    public static final HashMap<Location, String> dispenserProj = new HashMap<>();
    public static final HashMap<Projectile, Integer[]> colorArrows = new HashMap<>();
    public static final HashSet<Player> partyPlayers = new HashSet<>();
    public static final HashMap<Player, CraftedFirework> inventories = new HashMap<>();
    public static final HashSet<Block> blockedSigns = new HashSet<>();
}
