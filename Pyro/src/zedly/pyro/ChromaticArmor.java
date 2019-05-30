package zedly.pyro;

import java.util.*;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import zedly.pyro.annotations.EffectTask;
import zedly.pyro.enums.Frequency;

public class ChromaticArmor implements Listener {


    /*
    Fix item in hand update animation from being triggered constantly

	Faster Updating For:
		Watcher for dropped items (itemdropevent?)
		Watcher for picked up items (playerpickupitemevent)
		Watcher for left & right clicks [InventoryClickEvent]
		Watcher for player join events [?]
		Watcher for inventory open events [?]
     */

	//region Helpers

	private static int t = 0;
	// **** Helper functions for all chromatic armor types ****

	@EffectTask(Frequency.HIGH)
	public static void advanceTime() {
		t++;
	}

	// Advance time forward one tick ot change the color of the passed item
	private static AbstractMap.SimpleEntry<int[], ItemStack> advanceTime(ItemStack stk, int t, double[] params) {
		return advanceTime(stk, t, params, null);
	}

	// Advance time forward one tick ot change the color of the passed item (for players)
	private static AbstractMap.SimpleEntry<int[], ItemStack> advanceTime(ItemStack stk, int t, double[] params,
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
			if (player != null && Storage.vanishedPlayers.contains(player)) {
				int i = Storage.rnd.nextInt(30);
				if (Storage.rnd.nextInt(50) == 10) {
					color = new int[]{255, 255, 255};
				} else {
					color =
						new int[]{Storage.rnd.nextInt(20) + 75 + i,
							Storage.rnd.nextInt(20) + 75 + i,
							Storage.rnd.nextInt(20) + 75 + i};
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
	private static double[] getChromo(ItemStack stk) {
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
	private static int getHash(ItemStack stk) {
		if (stk == null || !stk.hasItemMeta() || !stk.getItemMeta().hasLore()) {
			return -1;
		}
		return stk.getItemMeta().getLore().hashCode();
	}


	public static <T> void updateHash(Map<T, Map<Integer, Integer>> hashMap, Map<T, Map<Integer, double[]>> paramMap,
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

	@EventHandler
	public void onShiftClick(InventoryClickEvent evt) {
		if (evt.getClick().isKeyboardClick() || evt.getClick().isShiftClick()) {
			if (evt.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
				Inventory start = evt.getClickedInventory();
				Inventory end = evt.getWhoClicked().getOpenInventory().getTopInventory() == start
					? evt.getWhoClicked().getOpenInventory().getBottomInventory()
					: evt.getWhoClicked().getOpenInventory().getTopInventory();

				int src;
				int dst;
				if (evt.getWhoClicked().getOpenInventory().getTopInventory().getType() == InventoryType.CRAFTING) {
					src = 0;
					dst = 0;
				} else {
					src = evt.getSlot();
					dst = end.firstEmpty();
				}
				Player player = (Player) evt.getWhoClicked();

				if (end.getType() == InventoryType.PLAYER) {
					updateHash(playerHashes, playerParams, player, player.getInventory().getItem(dst), dst);
				} else {
					updateHash(inventoryHashes, inventoryParams, end, end.getItem(dst), dst);
				}

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.pyro, () -> {
					for (HumanEntity p : evt.getViewers()) {
						((Player) p).updateInventory();
					}
				}, 1);
			}

		} else if (evt.getClick().isLeftClick() || evt.getClick().isRightClick()) {
			// Preset the destination so it automatically starts shifting colors

			// Todo: Fix
			updateHash(playerHashes, playerParams, (Player) evt.getWhoClicked(), evt.getCurrentItem(), 0);
			updateHash(inventoryHashes, inventoryParams, evt.getClickedInventory(), evt.getCurrentItem(), 0);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.pyro, () -> {
				for (HumanEntity p : evt.getViewers()) {
					((Player) p).updateInventory();
				}
			}, 1);


		}
	}

	//endregion


	//region Player Chromatic Armor (In hand, armor slots, and inventory)

	// Map from player UUID to inventory slot to parameter array
	private static final Map<Player, Map<Integer, double[]>> playerParams = new HashMap<>();

	// Map from player UUID to inventory slot to item hash
	private static final Map<Player, Map<Integer, Integer>> playerHashes = new HashMap<>();

	// Scan all players and look for new chromatic armor
	@EffectTask(Frequency.MEDIUM_LOW)
	public static void fetchPlayerChromo() {

		// Scan through all players
		for (Player player : Bukkit.getOnlinePlayers()) {
			// Scan through their inventory slots
			for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
				updateHash(playerHashes, playerParams, player, player.getInventory().getItem(slot), slot);
			}
		}
	}

	// Purge chromatic armor that is different and then advance forward in time inventory chromatic armor
	@EffectTask(Frequency.HIGH)
	public static void updatePlayerChromo() {
		// Scan through all players last known to contain chromatic armor
		for (Player player : playerParams.keySet()) {
			if (!player.isOnline()) {
				continue;
			}

			Set<Integer> removeSlots = new HashSet<>();
			// Scan through all player slots last known to contain chromatic armor
			for (int slot : playerParams.get(player).keySet()) {

				int hash = getHash(player.getInventory().getItem(slot));
				double[] params = null;
				if (playerHashes.get(player).get(slot) == hash) {
					params = playerParams.get(player).get(slot);
				} else {
					removeSlots.add(slot);
					continue;
				}

				if (params != null) {
					AbstractMap.SimpleEntry<int[], ItemStack> values =
						advanceTime(player.getInventory().getItem(slot), t, params, player);
					int[] color = values.getKey();

					// Create sparkle trails
					if ((player.isFlying() || player.isSprinting()) && Storage.rnd.nextBoolean()
						&& !Storage.vanishedPlayers.contains(player)) {
						Location loc = player.getLocation().clone();
						loc.subtract(player.getLocation().getDirection());
						float heightAboveFeetPos = 0;

						switch (slot) {
							case 39:
								heightAboveFeetPos = 1.75f;
								break;
							case 38:
								heightAboveFeetPos = 1.25f;
								break;
							case 37:
								heightAboveFeetPos = .83f;
								break;
							case 36:
								heightAboveFeetPos = .38f;
								break;
						}

						if (heightAboveFeetPos != 0) {
							loc.setY(loc.getY() + heightAboveFeetPos + (Storage.rnd.nextFloat() / 4) * (
								Storage.rnd.nextInt(2) * 2 - 1));
							loc.setX(loc.getX() + (Storage.rnd.nextFloat() / 4) * (Storage.rnd.nextInt(2) * 2 - 1));
							loc.setZ(loc.getZ() + (Storage.rnd.nextFloat() / 4) * (Storage.rnd.nextInt(2) * 2 - 1));
							Color col = Color.fromRGB(Utilities.clamp(color[0]), Utilities.clamp(color[1]),
								Utilities.clamp(color[2]));

							loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1,
								new Particle.DustOptions(col, 1.0f));
						}
					}
				}
			}
			for (int slot : removeSlots) {
				playerParams.get(player).remove(slot);
				playerHashes.get(player).remove(slot);
			}
		}
	}

	//endregion


	//region Entity Chromatic Armor (In hand, armor slots, and inventory)

	// Map from player UUID to inventory slot to parameter array
	private static final Map<LivingEntity, Map<Integer, double[]>> entityParams = new HashMap<>();

	// Map from player UUID to inventory slot to item hash
	private static final Map<LivingEntity, Map<Integer, Integer>> entityHashes = new HashMap<>();


	private static ItemStack getLivingEntitySlot(LivingEntity entity, int slot) {
		switch (slot) {
			case 0:
				return entity.getEquipment().getItemInMainHand();
			case 1:
				return entity.getEquipment().getItemInOffHand();
			case 2:
				return entity.getEquipment().getBoots();
			case 3:
				return entity.getEquipment().getLeggings();
			case 4:
				return entity.getEquipment().getChestplate();
			default:
				return entity.getEquipment().getHelmet();
		}
	}

	private static void setLivingEntitySlot(LivingEntity entity, int slot, ItemStack stk) {
		switch (slot) {
			case 0:
				entity.getEquipment().setItemInMainHand(stk);
				break;
			case 1:
				entity.getEquipment().setItemInOffHand(stk);
				break;
			case 2:
				entity.getEquipment().setBoots(stk);
				break;
			case 3:
				entity.getEquipment().setLeggings(stk);
				break;
			case 4:
				entity.getEquipment().setChestplate(stk);
				break;
			default:
				entity.getEquipment().setHelmet(stk);
		}
	}

	// Scan all living entities and look for new chromatic armor
	@EffectTask(Frequency.MEDIUM_LOW)
	public static void fetchEntityChromo() {
		// Scan through all entities
		for (World world : Bukkit.getWorlds()) {
			for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class)) {
				if (!(entity instanceof Player)) {
					// Scan through their inventory slots
					for (int slot = 0; slot < 6; slot++) {
						// Hand = 0, OffHand = 1, Boots = 2, Leggings = 3, Chestplate = 4, Helmet = 5

						ItemStack stk = getLivingEntitySlot(entity, slot);
						updateHash(entityHashes, entityParams, entity, stk, slot);
					}
				}
			}
		}
	}

	// Purge chromatic armor that is different and then advance forward in time inventory chromatic armor
	@EffectTask(Frequency.HIGH)
	public static void updateEntityChromo() {
		Set<LivingEntity> removeEntity = new HashSet<>();

		// Scan through all entities last known to contain chromatic armor
		for (LivingEntity entity : entityParams.keySet()) {
			if (entity.isDead()) {
				removeEntity.add(entity);
				continue;
			}

			Set<Integer> removeSlots = new HashSet<>();
			// Scan through all entity slots last known to contain chromatic armor
			for (int slot : entityParams.get(entity).keySet()) {
				ItemStack stk = getLivingEntitySlot(entity, slot);

				int hash = getHash(stk);

				double[] params = null;
				if (entityHashes.get(entity).get(slot) == hash) {
					params = entityParams.get(entity).get(slot);
				} else {
					removeSlots.add(slot);
					continue;
				}

				if (params != null) {
					setLivingEntitySlot(entity, slot, advanceTime(stk, t, params).getValue());
				}

			}
			for (int slot : removeSlots) {
				entityParams.get(entity).remove(slot);
				entityHashes.get(entity).remove(slot);
			}
		}
		for (LivingEntity entity : removeEntity) {
			entityParams.remove(entity);
			entityHashes.remove(entity);
		}
	}

	//endregion


	//region Inventory Chromatic Armor (In chests, hoppers, etc)

	// Map from player UUID to inventory slot to parameter array
	private static final Map<Inventory, Map<Integer, double[]>> inventoryParams = new HashMap<>();


	// Map from player UUID to inventory slot to item hash
	private static final Map<Inventory, Map<Integer, Integer>> inventoryHashes = new HashMap<>();

	@EffectTask(Frequency.MEDIUM_LOW)
	public static void fetchInventoryChromo() {
		// Scan through all players
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getOpenInventory().getTopInventory() != null) {
				// Scan through their inventory slots
				for (int slot = 0; slot < player.getOpenInventory().getTopInventory().getSize(); slot++) {
					updateHash(inventoryHashes, inventoryParams, player.getOpenInventory().getTopInventory(),
						player.getOpenInventory().getTopInventory().getItem(slot), slot);
				}
			}
		}
	}

	@EffectTask(Frequency.HIGH)
	public static void updateInventoryChromo() {
		Set<Inventory> removeInv = new HashSet<>();

		// Scan through all players last known to contain chromatic armor
		for (Inventory inventory : inventoryParams.keySet()) {
			if (inventory == null) {
				removeInv.add(inventory);
				continue;
			}

			Set<Integer> removeSlots = new HashSet<>();
			// Scan through all player slots last known to contain chromatic armor
			for (int slot : inventoryParams.get(inventory).keySet()) {
				int hash = getHash(inventory.getItem(slot));
				double[] params = null;
				if (inventoryHashes.get(inventory).get(slot) == hash) {
					params = inventoryParams.get(inventory).get(slot);
				} else {
					removeSlots.add(slot);
					continue;
				}

				if (params != null) {
					AbstractMap.SimpleEntry<int[], ItemStack> values = advanceTime(inventory.getItem(slot), t, params);
				}
			}

			for (int slot : removeSlots) {
				inventoryParams.get(inventory).remove(slot);
				inventoryHashes.get(inventory).remove(slot);
			}
		}

		for (Inventory inventory : removeInv) {
			inventoryParams.remove(inventory);
			inventoryHashes.remove(inventory);
		}
	}

	//endregion


	//region Item Frame Chromatic Armor

	// Map from player UUID to inventory slot to parameter array
	private static final Map<ItemFrame, Map<Integer, double[]>> frameParams = new HashMap<>();


	// Map from player UUID to inventory slot to item hash
	private static final Map<ItemFrame, Map<Integer, Integer>> frameHashes = new HashMap<>();

	@EffectTask(Frequency.MEDIUM_LOW)
	public static void fetchItemFrameChromo() {
		// Scan through all entities
		for (World world : Bukkit.getWorlds()) {
			for (ItemFrame frame : world.getEntitiesByClass(ItemFrame.class)) {
				updateHash(frameHashes, frameParams, frame, frame.getItem(), 0);
			}
		}
	}

	@EffectTask(Frequency.HIGH)
	public static void updateItemFrameChromo() {
		// Scan through all entities last known to contain chromatic armor

		Set<ItemFrame> removeFrames = new HashSet<>();
		for (ItemFrame frame : frameParams.keySet()) {
			if (frame.isDead()) {
				removeFrames.add(frame);
				continue;
			}

			ItemStack stk = frame.getItem();
			int hash = getHash(stk);

			double[] params = null;
			if (frameHashes.get(frame).get(0) == hash) {
				params = frameParams.get(frame).get(0);
			} else {
				removeFrames.add(frame);
			}
			if (params != null) {
				frame.setItem(advanceTime(stk, t, params).getValue());
			}
		}
		for (ItemFrame frame : removeFrames) {
			frameParams.remove(frame);
			frameHashes.remove(frame);
		}
	}

	//endregion


	//region Dropped Item Chromatic Armor

	// Map from player UUID to inventory slot to parameter array
	private static final Map<Item, Map<Integer, double[]>> droppedParams = new HashMap<>();


	// Map from player UUID to inventory slot to item hash
	private static final Map<Item, Map<Integer, Integer>> droppedHashes = new HashMap<>();

	@EffectTask(Frequency.MEDIUM_LOW)
	public static void fetchDroppedItemChromo() {
		// Scan through all entities
		for (World world : Bukkit.getWorlds()) {
			for (Item item : world.getEntitiesByClass(Item.class)) {
				updateHash(droppedHashes, droppedParams, item, item.getItemStack(), 0);
			}
		}
	}

	@EffectTask(Frequency.HIGH)
	public static void updateDroppedItemChromo() {
		// Scan through all entities last known to contain chromatic armor

		Set<Item> removeDropped = new HashSet<>();
		for (Item item : droppedParams.keySet()) {
			if (item.isDead()) {
				removeDropped.add(item);
				continue;
			}

			ItemStack stk = item.getItemStack();
			int hash = getHash(stk);

			double[] params = null;
			if (droppedHashes.get(item).get(0) == hash) {
				params = droppedParams.get(item).get(0);
			} else {
				removeDropped.add(item);
			}
			if (params != null) {
				item.setItemStack(advanceTime(stk, t, params).getValue());
			}
		}
		for (Item item : removeDropped) {
			droppedParams.remove(item);
			droppedHashes.remove(item);
		}
	}

	//endregion


	//region Cursor Chromatic Armor

	// Map from player UUID to inventory slot to parameter array
	private static final Map<Player, Map<Integer, double[]>> cursorParams = new HashMap<>();

	// Map from player UUID to inventory slot to item hash
	private static final Map<Player, Map<Integer, Integer>> cursorHashes = new HashMap<>();

	@EffectTask(Frequency.MEDIUM_LOW)
	public static void fetchCursorChromo() {
		// Scan through all players
		for (Player player : Bukkit.getOnlinePlayers()) {
			updateHash(cursorHashes, cursorParams, player, player.getOpenInventory().getCursor(), 0);
		}
	}

	@EffectTask(Frequency.HIGH)
	public static void updateCursorChromo() {
		// Scan through all players last known to contain chromatic armor
		for (Player player : cursorParams.keySet()) {
			if (!player.isOnline()) {
				continue;
			}

			Set<Integer> removeSlots = new HashSet<>();
			// Scan through all player slots last known to contain chromatic armor


			int hash = getHash(player.getOpenInventory().getCursor());
			double[] params = null;
			if (cursorHashes.get(player).get(0) == hash) {
				params = cursorParams.get(player).get(0);
			} else {
				cursorParams.remove(player);
				cursorHashes.remove(player);
			}

			if (params != null) {
				AbstractMap.SimpleEntry<int[], ItemStack> values =
					advanceTime(player.getOpenInventory().getCursor(), t, params, player);
				player.getOpenInventory().setCursor(values.getValue());
			}
		}
	}

	//endregion


}
