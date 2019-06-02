package zedly.pyro.ChromaticArmor;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class EventListener implements org.bukkit.event.Listener {


    /*
    Fix item in hand update animation from being triggered constantly

	Faster Updating For:
		Watcher for dropped items [EntitySpawnEvent]
		Watcher for inventory open events [InventoryOpenEvent]
     */

	private int getDestination(Player player, int defaultDst) {
		for (int i = 0; i < 36; i++) {
			int j = i;
			if (i < 27) {
				j += 9;
			} else {
				j -= 27;
			}
			if (player.getInventory().getItem(j) == null) {
				return j;
			}
		}
		return defaultDst;
	}

	private int getHotbarDestination(Player player, int defaultDst) {
		for (int i = 0; i < 9; i++) {
			if (player.getInventory().getItem(i) == null) {
				return i;
			}
		}
		return defaultDst;
	}

	private int getMainInvDestination(Player player, int defaultDst) {
		for (int i = 9; i < 36; i++) {
			if (player.getInventory().getItem(i) == null) {
				return i;
			}
		}
		return defaultDst;
	}

	private int getFlippedDestination(Player player, int defaultDst) {
		for (int i = 8; i >= 0; i--) {
			if (player.getInventory().getItem(i) == null) {
				return i;
			}
		}
		for (int i = 35; i >= 0; i--) {
			if (player.getInventory().getItem(i) == null) {
				return i;
			}
		}
		return defaultDst;
	}

	private int getStorageDestination(Inventory inv, int defaultDst) {
		for (int i = 0; i < inv.getSize(); i++) {
			if (inv.getItem(i) == null) {
				return i;
			}
		}
		return defaultDst;
	}


	// Listens for inventory events so that Chromatic Armor has a smoother transition
	@EventHandler
	public void onShiftClick(InventoryClickEvent evt) {
		// First, determine what the start and end inventories, and the relevant slots for each

		long l = System.nanoTime();
		Inventory start = evt.getClickedInventory();
		Inventory end = evt.getWhoClicked().getOpenInventory().getTopInventory() == start
			? evt.getWhoClicked().getOpenInventory().getBottomInventory()
			: evt.getWhoClicked().getOpenInventory().getTopInventory();

		int dst = evt.getSlot();

		// Can't shift click into a crafting slot
		if (start.getType().equals(InventoryType.PLAYER)
			&& end.getType().equals(InventoryType.CRAFTING)) {
			end = start;
		}

		Player player = (Player) evt.getWhoClicked();
		if (start.equals(end)) {
			boolean slotCheck = evt.getClickedInventory().getItem(evt.getSlot()) != null;
			if (slotCheck && evt.getClickedInventory().getItem(evt.getSlot()).getType().equals(Material.LEATHER_HELMET)
				&& player.getInventory().getHelmet() == null) {
				dst = 39;
			} else if (slotCheck && evt.getClickedInventory().getItem(evt.getSlot()).getType().equals(Material.LEATHER_CHESTPLATE)
				&& player.getInventory().getChestplate() == null) {
				dst = 38;
			} else if (slotCheck && evt.getClickedInventory().getItem(evt.getSlot()).getType().equals(Material.LEATHER_LEGGINGS)
				&& player.getInventory().getLeggings() == null) {
				dst = 37;
			} else if (slotCheck && evt.getClickedInventory().getItem(evt.getSlot()).getType().equals(Material.LEATHER_BOOTS)
				&& player.getInventory().getBoots() == null) {
				dst = 36;
			} else if (evt.getSlot() < 9) {
				// Main hotbar: we only want it to go into the rest of the inventory
				dst = getMainInvDestination(player, dst);
			} else if (evt.getSlot() < 36) {
				// Main inventory: we only want it to go into the hotbar
				dst = getHotbarDestination(player, dst);
			} else {
				// From the armor slots, first do the main inv, then the hotbar
				dst = getDestination(player, dst);
			}
		} else if (start.getType().equals(InventoryType.CRAFTING)){
			dst = getDestination(player, dst);
		} else {
			if (end.getType().equals(InventoryType.PLAYER)) {
				dst = getFlippedDestination(player, dst);
			} else {
				dst = getStorageDestination(end, dst);
			}
		}

		final int dst_f = dst;
		final Inventory end_f = end;
		// Two cases: One, an instant movement, the other, cursor movements
		if (evt.getClick().isKeyboardClick() || evt.getClick().isShiftClick()) {

			// See if we're moving an item to another inventory
			if (evt.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(zedly.pyro.Core.Storage.pyro, () -> {
					// If so, check against the destination and update the hash for that slot
					if (end_f.getType() == InventoryType.PLAYER) {
						//Helpers.updateHash(Storage.playerHashes, Storage.playerParams, player, player.getInventory().getItem(dst_f), dst_f);
					} else {
						//Helpers.updateHash(Storage.inventoryHashes, Storage.inventoryParams, end_f, end_f.getItem(dst_f), dst_f);
					}
				}, 1);


				// Update the inventory after 1 tick to prevent ghost chromatic armor item stacks
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(zedly.pyro.Core.Storage.pyro, () -> {
					for (HumanEntity p : evt.getViewers()) {
						 //((Player) p).updateInventory();
					}
				}, 1);
			}

		} else if (evt.getClick().isLeftClick() || evt.getClick().isRightClick()) {
			Bukkit.broadcastMessage(evt.getSlot() + "");
			if (player.getGameMode().equals(GameMode.CREATIVE)) {
				//for (ItemStack stk : player.getInventory().getContents()) {
					//if (stk != null && stk.getType() != Material.AIR) {

					//}
				//}
			}

			// Preset the destination so it automatically starts shifting colors
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(zedly.pyro.Core.Storage.pyro, () -> {
				for (HumanEntity p : evt.getViewers()) {
					// Check against the destination
					if (end_f.getType() == InventoryType.CRAFTING) {
						//Helpers.updateHash(Storage.playerHashes, Storage.playerParams, player, evt.getCurrentItem(), evt.getSlot());
					} else {
						//Helpers.updateHash(Storage.inventoryHashes, Storage.inventoryParams, evt.getClickedInventory(), evt.getCurrentItem(), evt.getSlot());
					}

					// Update the cursor hash if it isn't air
					if (!player.getOpenInventory().getCursor().getType().equals(Material.AIR)) {
						//Helpers.updateHash(Storage.cursorHashes, Storage.cursorParams, player, player.getOpenInventory().getCursor(), 0);
					}
					// Update the inventory after 1 tick to prevent ghost chromatic armor item stacks
					if (!p.getGameMode().equals(GameMode.CREATIVE)) {
						//((Player) p).updateInventory();
					}

				}
			}, 1);
		}
		Bukkit.broadcastMessage((System.nanoTime() - l) + "");
		l = System.nanoTime();




		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(zedly.pyro.Core.Storage.pyro, () -> {
			long lll = System.nanoTime();
			// Scan through their inventory slots
			for (int slot = 0; slot < player.getOpenInventory().getTopInventory().getSize(); slot++) {
				Helpers.updateHash(Storage.inventoryHashes, Storage.inventoryParams, player.getOpenInventory().getTopInventory(),
					player.getOpenInventory().getTopInventory().getItem(slot), slot);
			}

			// Scan through their inventory slots
			for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
				Helpers.updateHash(Storage.playerHashes, Storage.playerParams, player, player.getInventory().getItem(slot), slot);
			}

			if (!player.getOpenInventory().getCursor().getType().equals(Material.AIR)) {
				Helpers.updateHash(Storage.cursorHashes, Storage.cursorParams, player, player.getOpenInventory().getCursor(), 0);
			}

			for (HumanEntity p : evt.getViewers()) {
				// Update the inventory after 1 tick to prevent ghost chromatic armor item stacks
				if (!p.getGameMode().equals(GameMode.CREATIVE)) {
					((Player) p).updateInventory();
				}
			}
			Bukkit.broadcastMessage((System.nanoTime() - lll) + "");
		}, 1);


	}

	@EventHandler
	public void onDrag(InventoryDragEvent evt) {
		Bukkit.broadcastMessage("Drag Event");
	}


	// Listens for item pickup events so that Chromatic Armor has a smoother transition
	@EventHandler
	public void onPickupItem(EntityPickupItemEvent evt) {
		if (evt.getEntity() instanceof Player) {
			Player player = (Player) evt.getEntity();
			int slotNum = player.getInventory().firstEmpty();

			// Preset the destination so it automatically starts shifting colors
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(zedly.pyro.Core.Storage.pyro, () -> {
				Helpers.updateHash(Storage.playerHashes, Storage.playerParams, player, player.getInventory().getItem(slotNum), slotNum);
			}, 0);
		}
	}
}
