package zedly.pyro.ChromaticArmor;

import java.util.*;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import zedly.pyro.Core.Utilities;
import zedly.pyro.annotations.EffectTask;
import zedly.pyro.enums.Frequency;

public class Helpers {

	// **** Helper functions for all chromatic armor types ****

	@EffectTask(Frequency.HIGH)
	public static void advanceTime() {
		Storage.t++;
	}

	// Advance time forward one tick ot change the color of the passed item
	protected static AbstractMap.SimpleEntry<int[], ItemStack> advanceTime(ItemStack stk, int t, double[] params) {
		return advanceTime(stk, t, params, null);
	}

	// Advance time forward one tick ot change the color of the passed item (for players)
	protected static AbstractMap.SimpleEntry<int[], ItemStack> advanceTime(ItemStack stk, int t, double[] params,
		Player player) {
		// Get the color
		int[] color = params == null ?
			null
			: params.length == 10 ?
				Utilities.getColor(params, t)
				: params.length == 12 ?
					Utilities.getThemedColor(params, t)
					: null;

		if (color != null) {
			// Chromatic armor does not wear
			Utilities.setDamage(stk, 0);

			LeatherArmorMeta meta = (LeatherArmorMeta) stk.getItemMeta();

			// Special case for vanished players
			if (player != null && zedly.pyro.Core.Storage.vanishedPlayers.contains(player)) {
				Random rnd = zedly.pyro.Core.Storage.rnd;
				int i = rnd.nextInt(30);
				if (rnd.nextInt(50) == 10) {
					color = new int[]{255, 255, 255};
				} else {
					color =
						new int[]{rnd.nextInt(20) + 75 + i,
							rnd.nextInt(20) + 75 + i,
							rnd.nextInt(20) + 75 + i};
				}
			}

			// Set the item in the entity's armor slots
			meta.setColor(Color.fromRGB(Utilities.clamp(color[0]), Utilities.clamp(color[1]),
				Utilities.clamp(color[2])));
			stk.setItemMeta(meta);
		}
		return new AbstractMap.SimpleEntry<int[], ItemStack>(color, stk.clone());
	}

	// Return the chromatic parameters of the given item stack. Returns null if it doesn't exist
	protected static double[] getChromo(ItemStack stk) {
		if (stk == null || !stk.hasItemMeta() || !stk.getItemMeta().hasLore()) {
			return null;
		}
		List<String> lore = stk.getItemMeta().getLore();
		double[] params = null;

		// Get the first correct lore string
		String chromoLoreString = null;
		for (String loreString : lore) {
			Map<String, Boolean> strInfo = Utilities.fromInvisibleString(loreString);
			for (Map.Entry<String, Boolean> entry : strInfo.entrySet()) {
				if (!entry.getValue() && entry.getKey().startsWith("py.chromo.")) {
					chromoLoreString = entry.getKey();
				}
			}
		}
		if (chromoLoreString == null) {
			return null;
		}

		// Parse the chromatic armor string
		String[] rawParams = null;
		if (chromoLoreString.startsWith("py.chromo.normal.")) {
			params = new double[10];
			rawParams = chromoLoreString.split("py.chromo.normal.")[1].split(":");
		} else if (chromoLoreString.startsWith("py.chromo.color.")) {
			params = new double[12];
			rawParams = chromoLoreString.split("py.chromo.color.")[1].split(":");

		}
		if (rawParams != null) {
			int i = 0;
			for (String value : rawParams) {
				params[i] = Double.parseDouble(value);
				i++;
			}
		}
		return params;
	}

	// Return the hash code of the given item stack's lore
	protected static int getHash(ItemStack stk) {
		if (stk == null || !stk.hasItemMeta() || !stk.getItemMeta().hasLore()) {
			return -1;
		}
		return stk.getItemMeta().getLore().hashCode();
	}

	// Updates the hash code of the given item stack and, if chromatic armor, will populate the relevant parameter map.
	protected static <T> void updateHash(Map<T, Map<Integer, Integer>> hashMap, Map<T, Map<Integer, double[]>> paramMap,
		T t, ItemStack stk, int slot) {
		// Get the hash of the item
		int hash = getHash(stk);

		// Check if the slot hasn't changed yet
		if (!(hashMap.containsKey(t) && hashMap.get(t).containsKey(slot)
			&& hashMap.get(t).get(slot) == hash)) {

			// Get the new chromatic armor parameters (if they exist)
			double[] params = getChromo(stk);

			// If they do exist, save the relevant information for later
			if (params != null) {
				if (!paramMap.containsKey(t)) {
					paramMap.put(t, new HashMap<>());
				}
				paramMap.get(t).put(slot, params);

				if (!hashMap.containsKey(t)) {
					hashMap.put(t, new HashMap<>());
				}
				hashMap.get(t).put(slot, hash);
			}
		}
	}

}
