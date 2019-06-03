package zedly.pyro.ChromaticArmor;

import java.util.*;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import zedly.pyro.Core.Utilities;
import zedly.pyro.annotations.EffectTask;
import zedly.pyro.enums.Frequency;

public class Tasks implements Listener {



	//region Player Chromatic Armor (In hand, armor slots, and inventory)

	// Scan all players and look for new chromatic armor
	@EffectTask(Frequency.LOW)
	public static void fetchPlayerChromo() {
		// Scan through all players
		for (Player player : Bukkit.getOnlinePlayers()) {
			// Scan through their inventory slots
			for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
				Helpers.updateHash(Storage.playerHashes, Storage.playerParams, player, player.getInventory().getItem(slot), slot);
			}
		}
	}

	// Purge chromatic armor that is different and then advance forward in time inventory chromatic armor
	@EffectTask(Frequency.HIGH)
	public static void updatePlayerChromo() {
		// Scan through all players last known to contain chromatic armor
		for (Player player : Storage.playerParams.keySet()) {
			if (!player.isOnline()) {
				continue;
			}

			Set<Integer> removeSlots = new HashSet<>();
			// Scan through all player slots last known to contain chromatic armor
			for (int slot : Storage.playerParams.get(player).keySet()) {

				int hash = Helpers.getHash(player.getInventory().getItem(slot));
				double[] params = null;
				if (Storage.playerHashes.get(player).get(slot) == hash) {
					params = Storage.playerParams.get(player).get(slot);
				} else {
					removeSlots.add(slot);
					continue;
				}

				if (params != null) {
					AbstractMap.SimpleEntry<int[], ItemStack> values =
						Helpers.advanceTime(player.getInventory().getItem(slot), Storage.t, params, player);
					int[] color = values.getKey();
					Random rnd =  zedly.pyro.Core.Storage.rnd;
					// Create sparkle trails
					if ((player.isFlying() || player.isSprinting()) &&  rnd.nextBoolean()
						&& ! zedly.pyro.Core.Storage.vanishedPlayers.contains(player)) {
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
							loc.setY(loc.getY() + heightAboveFeetPos + (rnd.nextFloat() / 4) * (
								rnd.nextInt(2) * 2 - 1));
							loc.setX(loc.getX() + (rnd.nextFloat() / 4) * (rnd.nextInt(2) * 2 - 1));
							loc.setZ(loc.getZ() + (rnd.nextFloat() / 4) * (rnd.nextInt(2) * 2 - 1));
							Color col = Color.fromRGB(Utilities.clamp(color[0]), Utilities.clamp(color[1]),
								Utilities.clamp(color[2]));

							loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1,
								new Particle.DustOptions(col, 1.0f));
						}
					}
				}
			}
			for (int slot : removeSlots) {
				Storage.playerParams.get(player).remove(slot);
				Storage.playerHashes.get(player).remove(slot);
			}
		}
	}

	//endregion


	//region Entity Chromatic Armor (In hand, armor slots, and inventory)

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
	@EffectTask(Frequency.SLOW)
	public static void fetchEntityChromo() {
		// Scan through all entities
		for (World world : Bukkit.getWorlds()) {
			for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class)) {
				if (!(entity instanceof Player)) {
					// Scan through their inventory slots
					for (int slot = 0; slot < 6; slot++) {
						// Hand = 0, OffHand = 1, Boots = 2, Leggings = 3, Chestplate = 4, Helmet = 5

						ItemStack stk = getLivingEntitySlot(entity, slot);
						Helpers.updateHash(Storage.entityHashes, Storage.entityParams, entity, stk, slot);
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
		for (LivingEntity entity : Storage.entityParams.keySet()) {
			if (entity.isDead()) {
				removeEntity.add(entity);
				continue;
			}

			Set<Integer> removeSlots = new HashSet<>();
			// Scan through all entity slots last known to contain chromatic armor
			for (int slot : Storage.entityParams.get(entity).keySet()) {
				ItemStack stk = getLivingEntitySlot(entity, slot);

				int hash = Helpers.getHash(stk);

				double[] params = null;
				if (Storage.entityHashes.get(entity).get(slot) == hash) {
					params = Storage.entityParams.get(entity).get(slot);
				} else {
					removeSlots.add(slot);
					continue;
				}

				if (params != null) {
					setLivingEntitySlot(entity, slot, Helpers.advanceTime(stk, Storage.t, params).getValue());
				}

			}
			for (int slot : removeSlots) {
				Storage.entityParams.get(entity).remove(slot);
				Storage.entityHashes.get(entity).remove(slot);
			}
		}
		for (LivingEntity entity : removeEntity) {
			Storage.entityParams.remove(entity);
			Storage.entityHashes.remove(entity);
		}
	}

	//endregion


	//region Inventory Chromatic Armor (In chests, hoppers, etc)

	@EffectTask(Frequency.LOW)
	public static void fetchInventoryChromo() {
		// Scan through all players
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getOpenInventory().getTopInventory() != null) {
				// Scan through their inventory slots
				for (int slot = 0; slot < player.getOpenInventory().getTopInventory().getSize(); slot++) {
					Helpers.updateHash(Storage.inventoryHashes, Storage.inventoryParams, player.getOpenInventory().getTopInventory(),
						player.getOpenInventory().getTopInventory().getItem(slot), slot);
				}
			}
		}
	}

	@EffectTask(Frequency.HIGH)
	public static void updateInventoryChromo() {
		Set<Inventory> removeInv = new HashSet<>();

		// Scan through all players last known to contain chromatic armor
		for (Inventory inventory : Storage.inventoryParams.keySet()) {
			if (inventory == null) {
				removeInv.add(inventory);
				continue;
			}

			Set<Integer> removeSlots = new HashSet<>();
			// Scan through all player slots last known to contain chromatic armor
			for (int slot : Storage.inventoryParams.get(inventory).keySet()) {
				int hash = Helpers.getHash(inventory.getItem(slot));
				double[] params = null;
				if (Storage.inventoryHashes.get(inventory).get(slot) == hash) {
					params = Storage.inventoryParams.get(inventory).get(slot);
				} else {
					removeSlots.add(slot);
					continue;
				}

				if (params != null) {
					AbstractMap.SimpleEntry<int[], ItemStack> values = Helpers.advanceTime(inventory.getItem(slot), Storage.t, params);
				}
			}

			for (int slot : removeSlots) {
				Storage.inventoryParams.get(inventory).remove(slot);
				Storage.inventoryHashes.get(inventory).remove(slot);
			}
		}

		for (Inventory inventory : removeInv) {
			Storage.inventoryParams.remove(inventory);
			Storage.inventoryHashes.remove(inventory);
		}
	}

	//endregion


	//region Item Frame Chromatic Armor

	@EffectTask(Frequency.MEDIUM_LOW)
	public static void fetchItemFrameChromo() {
		// Scan through all entities
		for (World world : Bukkit.getWorlds()) {
			for (ItemFrame frame : world.getEntitiesByClass(ItemFrame.class)) {
				Helpers.updateHash(Storage.frameHashes, Storage.frameParams, frame, frame.getItem(), 0);
			}
		}
	}

	@EffectTask(Frequency.HIGH)
	public static void updateItemFrameChromo() {
		// Scan through all entities last known to contain chromatic armor

		Set<ItemFrame> removeFrames = new HashSet<>();
		for (ItemFrame frame : Storage.frameParams.keySet()) {
			if (frame.isDead()) {
				removeFrames.add(frame);
				continue;
			}

			ItemStack stk = frame.getItem();
			int hash = Helpers.getHash(stk);

			double[] params = null;
			if (Storage.frameHashes.get(frame).get(0) == hash) {
				params = Storage.frameParams.get(frame).get(0);
			} else {
				removeFrames.add(frame);
			}
			if (params != null) {
				frame.setItem(Helpers.advanceTime(stk, Storage.t, params).getValue(), false);
			}
		}
		for (ItemFrame frame : removeFrames) {
			Storage.frameParams.remove(frame);
			Storage.frameHashes.remove(frame);
		}
	}

	//endregion


	//region Dropped Item Chromatic Armor

	@EffectTask(Frequency.MEDIUM_LOW)
	public static void fetchDroppedItemChromo() {
		// Scan through all entities
		for (World world : Bukkit.getWorlds()) {
			for (Item item : world.getEntitiesByClass(Item.class)) {
				Helpers.updateHash(Storage.droppedHashes, Storage.droppedParams, item, item.getItemStack(), 0);
			}
		}
	}

	@EffectTask(Frequency.HIGH)
	public static void updateDroppedItemChromo() {
		// Scan through all entities last known to contain chromatic armor

		Set<Item> removeDropped = new HashSet<>();
		for (Item item : Storage.droppedParams.keySet()) {
			if (item.isDead()) {
				removeDropped.add(item);
				continue;
			}

			ItemStack stk = item.getItemStack();
			int hash = Helpers.getHash(stk);

			double[] params = null;
			if (Storage.droppedHashes.get(item).get(0) == hash) {
				params = Storage.droppedParams.get(item).get(0);
			} else {
				removeDropped.add(item);
			}
			if (params != null) {
				item.setItemStack(Helpers.advanceTime(stk, Storage.t, params).getValue());
			}
		}
		for (Item item : removeDropped) {
			Storage.droppedParams.remove(item);
			Storage.droppedHashes.remove(item);
		}
	}

	//endregion


	//region Cursor Chromatic Armor

	@EffectTask(Frequency.MEDIUM_LOW)
	public static void fetchCursorChromo() {
		// Scan through all players
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getGameMode().equals(GameMode.CREATIVE)) {
				//double[] ddd = {7.0,7.0,7.0,0.0,120.0,240.0,127.0,0.0,40.0,1.0};
				Helpers.updateHash(Storage.cursorHashes, Storage.cursorParams, player,
					player.getOpenInventory().getCursor(), 0);
			} else {
				Helpers.updateHash(Storage.cursorHashes, Storage.cursorParams, player,
					player.getOpenInventory().getCursor(), 0);
			}
		}
	}

	@EffectTask(Frequency.HIGH)
	public static void updateCursorChromo() {
		// Scan through all players last known to contain chromatic armor
		for (Player player : Storage.cursorParams.keySet()) {
			if (!player.isOnline()) {
				continue;
			}
			Set<Integer> removeSlots = new HashSet<>();
			// Scan through all player slots last known to contain chromatic armor


			int hash = Helpers.getHash(player.getOpenInventory().getCursor());
			double[] params = null;
			if (Storage.cursorHashes.get(player).get(0) == hash) {
				params = Storage.cursorParams.get(player).get(0);
			} else {
				Storage.cursorParams.remove(player);
				Storage.cursorHashes.remove(player);
			}

			if (params != null) {
				AbstractMap.SimpleEntry<int[], ItemStack> values =
					Helpers.advanceTime(player.getOpenInventory().getCursor(), Storage.t, params, player);
				player.getOpenInventory().setCursor(values.getValue());
			}
		}
	}

	//endregion


}
