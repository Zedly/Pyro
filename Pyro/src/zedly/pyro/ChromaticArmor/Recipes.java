package zedly.pyro.ChromaticArmor;

import java.util.*;

import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import zedly.pyro.Core.Storage;

import static org.bukkit.Material.*;

public class Recipes {

	public static void chromoRecipe() {
		Material[] mats = new Material[]{LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS};
		String[] names = new String[]{"HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS"};
		for (int i = 0; i < mats.length; i++) {
			ItemStack armor = new ItemStack(mats[i]);
			ItemMeta meta = armor.getItemMeta();
			ArrayList<String> lore = new ArrayList<>();
			lore.add(0, ChatColor.GREEN + "Chromatic Armor: " + ChatColor.GOLD + "Not Configured");
			lore.add(1, ChatColor.GRAY + "Not Configured");
			meta.setLore(lore);
			armor.setItemMeta(meta);
			ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(Storage.pyro, "CHROMATIC_" + names[i]), new ItemStack(armor));
			recipe.addIngredient(mats[i]).addIngredient(NETHER_STAR);
			Bukkit.getServer().addRecipe(recipe);
		}
	}

}
