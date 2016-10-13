package zedly.pyro;

import java.util.*;
import org.bukkit.*;
import static org.bukkit.Material.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class Recipes {

    public static void snowballs() {
        ItemStack snowball = new ItemStack(SNOW_BALL, 1, (short) 0);
        ItemMeta meta = snowball.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "S" + ChatColor.DARK_PURPLE + "n" + ChatColor.DARK_AQUA + "o" + ChatColor.BLUE + "w" + ChatColor.GREEN + "b" + ChatColor.YELLOW + "a" + ChatColor.GOLD + "l" + ChatColor.RED + "l");
        lore.add(ChatColor.GREEN + "Rainbow");
        meta.setLore(lore);
        snowball.setItemMeta(meta);
        ShapelessRecipe recipe = new ShapelessRecipe(new ItemStack(snowball));
        int[] colors = new int[]{6, 2, 11, 5, 4, 1, 14};
        for (int i : colors) {
            recipe.addIngredient(STAINED_GLASS, i);
        }
        recipe.addIngredient(SNOW_BALL);
        Bukkit.getServer().addRecipe(recipe);
    }

    public static void remotes() {
        ItemStack remote = new ItemStack(NAME_TAG, 1, (short) 0);
        ItemMeta meta = remote.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "<<--->>");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Remote Detonator");
        meta.setLore(lore);
        remote.setItemMeta(meta);
        ShapelessRecipe recipe = new ShapelessRecipe(new ItemStack(remote));
        recipe.addIngredient(TRIPWIRE_HOOK).addIngredient(REDSTONE).addIngredient(REDSTONE_COMPARATOR).addIngredient(REDSTONE_TORCH_ON).addIngredient(NAME_TAG);
        Bukkit.getServer().addRecipe(recipe);
    }

    public static void colorArrow() {
        ItemStack arrow = new ItemStack(ARROW, 1, (short) 0);
        ItemMeta meta = arrow.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        lore.add(0, ChatColor.GOLD + "Color Arrow");
        lore.add(1, ChatColor.RED + "R: -" + ChatColor.WHITE + ", " + ChatColor.GREEN + "G: -" + ChatColor.WHITE + ", " + ChatColor.BLUE + "B: -");
        meta.setLore(lore);
        arrow.setItemMeta(meta);
        ShapelessRecipe recipe = new ShapelessRecipe(new ItemStack(arrow));
        recipe.addIngredient(SULPHUR).addIngredient(ARROW);
        Bukkit.getServer().addRecipe(recipe);
    }

    public static void bang() {
        ItemStack is = new ItemStack(SNOW_BALL, 4);
        ItemMeta meta = is.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        lore.add(0, ChatColor.GOLD + "Bang");
        meta.setLore(lore);
        is.setItemMeta(meta);
        Bukkit.getServer().addRecipe(new ShapelessRecipe(is).addIngredient(SNOW_BALL).addIngredient(SNOW_BALL).addIngredient(SNOW_BALL).addIngredient(SNOW_BALL).addIngredient(SULPHUR));
    }

    public static void chromo() {
        for (Material m : new Material[]{LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS}) {
            ItemStack armor = new ItemStack(m);
            ItemMeta meta = armor.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();
            lore.add(0, ChatColor.GREEN + "Chromatic Armor: " + ChatColor.GOLD + "Not Configured");
            lore.add(1, ChatColor.GRAY + "Not Configured");
            meta.setLore(lore);
            armor.setItemMeta(meta);
            ShapelessRecipe recipe = new ShapelessRecipe(new ItemStack(armor));
            recipe.addIngredient(m).addIngredient(NETHER_STAR);
            Bukkit.getServer().addRecipe(recipe);
        }
    }
}
