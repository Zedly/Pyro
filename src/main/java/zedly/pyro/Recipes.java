package zedly.pyro;

import java.util.*;
import org.bukkit.*;
import static org.bukkit.Material.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class Recipes {

    public static void snowballs() {
        ItemStack rainbowSnowball = new ItemStack(SNOWBALL, 1);
        ItemMeta meta = rainbowSnowball.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "S" + ChatColor.DARK_PURPLE + "n" + ChatColor.DARK_AQUA + "o" + ChatColor.BLUE + "w" + ChatColor.GREEN + "b" + ChatColor.YELLOW + "a" + ChatColor.GOLD + "l" + ChatColor.RED + "l");
        lore.add(ChatColor.GREEN + "Rainbow");
        meta.setLore(lore);
        rainbowSnowball.setItemMeta(meta);
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(Storage.pyro, "rainbow_snowball"), rainbowSnowball);
        for (Material m : Storage.rainbowGlass) {
            recipe.addIngredient(m);
        }
        recipe.addIngredient(SNOWBALL);
        Bukkit.getServer().addRecipe(recipe);
    }

    public static void remotes() {
        ItemStack remote = new ItemStack(NAME_TAG, 1);
        ItemMeta meta = remote.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "<<--->>");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Remote Detonator");
        meta.setLore(lore);
        remote.setItemMeta(meta);
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(Storage.pyro, "remote_trigger"), new ItemStack(remote));
        recipe.addIngredient(TRIPWIRE_HOOK).addIngredient(REDSTONE).addIngredient(COMPARATOR).addIngredient(REDSTONE_TORCH).addIngredient(NAME_TAG);
        Bukkit.getServer().addRecipe(recipe);
    }

    public static void colorArrow() {
        ItemStack arrow = new ItemStack(ARROW, 1);
        ItemMeta meta = arrow.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        lore.add(0, ChatColor.GOLD + "Color Arrow");
        lore.add(1, ChatColor.RED + "R: -" + ChatColor.WHITE + ", " + ChatColor.GREEN + "G: -" + ChatColor.WHITE + ", " + ChatColor.BLUE + "B: -");
        meta.setLore(lore);
        arrow.setItemMeta(meta);
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(Storage.pyro, "color_arrow"), new ItemStack(arrow));
        recipe.addIngredient(GUNPOWDER).addIngredient(ARROW);
        Bukkit.getServer().addRecipe(recipe);
    }

    public static void bang() {
        ItemStack is = new ItemStack(SNOWBALL, 4);
        ItemMeta meta = is.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        lore.add(0, ChatColor.GOLD + "Bang");
        meta.setLore(lore);
        is.setItemMeta(meta);
        Bukkit.getServer().addRecipe(new ShapelessRecipe(new NamespacedKey(Storage.pyro, "bang_snowball"), is).addIngredient(SNOWBALL).addIngredient(SNOWBALL).addIngredient(SNOWBALL).addIngredient(SNOWBALL).addIngredient(GUNPOWDER));
    }
}
