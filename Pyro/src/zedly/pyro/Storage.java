package zedly.pyro;

import java.util.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import zedly.pyro.compatibility.CompatibilityAdapter;
import zedly.pyro.compatibility.NMS_1_13_R1;
import zedly.pyro.compatibility.NMS_1_13_R2;

import static org.bukkit.entity.EntityType.*;

public class Storage {

    //Variables
    public static Pyro pyro;

	// Absolute path to the plugin jar
	public static String pluginPath;

    public static final Random rnd = new Random();
    public static boolean globalparty = false;
    public static String colorString;

    //Pre-defined Variables
    public static final String logo = ChatColor.DARK_RED + "[" + ChatColor.GOLD + "Pyro" + ChatColor.DARK_RED + "]" + ChatColor.YELLOW + "";
    public static Integer[] badIds = new Integer[]{0, 8, 9, 10, 11, 26, 34, 36, 43, 51, 55, 59, 63, 64, 68, 71, 74, 75, 83, 90, 92, 93, 94, 104, 105, 115, 117, 118, 119, 124, 125, 127, 132, 140, 141, 142, 144, 149, 150, 176, 177, 178, 181, 193, 194, 195, 196, 197};
    public static final int[] rainbowcolors = {0xFF0000, 0xFF8800, 0xFFFF00, 0x88FF00, 0x00FF00, 0x00FF88, 0x00FFFF, 0x0088FF, 0x0000FF, 0x8800FF, 0xFF00FF, 0xFF0088};

    public static final EntityType[] EASTER_EGG_TYPES = {
        ELDER_GUARDIAN, WITHER_SKELETON, STRAY, HUSK, ZOMBIE_VILLAGER, SKELETON_HORSE, ZOMBIE_HORSE, DONKEY, MULE, EVOKER, VEX, VINDICATOR, CREEPER, SKELETON, SPIDER,
        ZOMBIE, SLIME, GHAST, PIG_ZOMBIE, ENDERMAN, CAVE_SPIDER, SILVERFISH, BLAZE, MAGMA_CUBE, ENDER_DRAGON, WITHER, BAT, WITCH, ENDERMITE, GUARDIAN, SHULKER, PIG, SHEEP,
        COW, CHICKEN, SQUID, WOLF, MUSHROOM_COW, SNOWMAN, OCELOT, IRON_GOLEM, HORSE, RABBIT, POLAR_BEAR, LLAMA, VILLAGER
    };

    public static String[] colors = new String[]{"white", "orange", "magenta", "light_blue", "yellow", "light_green", "pink", "dark_gray",
        "light_gray", "aqua", "purple", "dark_blue", "brown", "dark_green", "red", "black"};

    public static int[][] color = new int[][]{new int[]{255, 255, 255}, new int[]{255, 127, 0}, new int[]{255, 0, 255},
    new int[]{0, 255, 255}, new int[]{255, 255, 0}, new int[]{0, 255, 0}, new int[]{255, 0, 127}, new int[]{63, 63, 63},
    new int[]{127, 127, 127}, new int[]{0, 127, 127}, new int[]{127, 0, 127}, new int[]{0, 0, 255}, new int[]{127, 63, 0},
    new int[]{0, 127, 0}, new int[]{255, 0, 0}, new int[]{0, 0, 0}};

    public static int[][] mojangColors = new int[][]{new int[]{240, 240, 240}, new int[]{235, 136, 68}, new int[]{195, 84, 205},
    new int[]{102, 137, 211}, new int[]{222, 207, 42}, new int[]{65, 205, 52}, new int[]{216, 129, 152}, new int[]{67, 67, 67},
    new int[]{171, 171, 171}, new int[]{40, 118, 151}, new int[]{123, 47, 190}, new int[]{37, 49, 146}, new int[]{81, 48, 26},
    new int[]{59, 81, 26}, new int[]{179, 49, 44}, new int[]{30, 27, 27}};

    //Collections
    public static final Map<String, Boolean> recipes = new HashMap<>();
    public static final Set<Snowball> bangBalls = new HashSet<>();
    public static final Map<Integer, TNTExplosion> explodingEntities = new HashMap<>();
    public static final Map<Block, TNTExplosion> explodingBlocks = new HashMap<>();
    public static final Map<String, ArrayList<Block>> remoteTnt = new HashMap<>();
    public static final Set<TNTPrimed> tntEntities = new HashSet<>();
    public static final Set<Player> vanishedPlayers = new HashSet<>();
    public static final Set<Block> poweredBlocks = new HashSet<>();
    public static final Map<Item, ItemStack> eastereggs = new HashMap<>();
    public static final Set<Entity> snowballs = new HashSet<>();
    public static final Map<Block, Long> rainbowBlocks = new HashMap<>();
    public static final Map<Location, String> dispenserProj = new HashMap<>();
    public static final Map<Projectile, Integer[]> colorArrows = new HashMap<>();
    public static final Set<Player> partyPlayers = new HashSet<>();
    public static final Map<Player, CraftedFirework> inventories = new HashMap<>();
    public static final Set<Block> blockedSigns = new HashSet<>();

    public static final CompatibilityAdapter COMPATIBILITY_ADAPTER;


    static {
        String versionString = Bukkit.getServer().getClass().getPackage().getName();
        String nmsVersionString = versionString.substring(versionString.lastIndexOf('.') + 1);
        System.out.println("Pyro: Detected NMS version \"" + nmsVersionString + "\"");
        switch (nmsVersionString) {
            case "v1_13_R1":
                COMPATIBILITY_ADAPTER = NMS_1_13_R1.getInstance();
                break;
            case "v1_13_R2":
                COMPATIBILITY_ADAPTER = NMS_1_13_R2.getInstance();
                break;
            default:
                System.out.println(
                    "No compatible adapter available, falling back to Bukkit. Not everything will work!");
                COMPATIBILITY_ADAPTER = zedly.pyro.compatibility.CompatibilityAdapter.getInstance();
                break;
        }
    }
}
