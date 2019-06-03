package zedly.pyro.ChromaticArmor;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class EventListener implements org.bukkit.event.Listener {


    /*
    Fix item in hand update animation from being triggered constantly

	Faster Updating For:
		Watcher for inventory open events [InventoryOpenEvent]
		Watcher for entities picking up chromo [EntityPickupItemEvent]
     */


	// Generic Method to update Chromatic Armor hashes for InventoryClickEvent and InventoryDragEvent
	private void update (InventoryEvent evt, Player player) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(zedly.pyro.Core.Storage.pyro, () -> {
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
		}, 1);
	}

	// Listens for inventory events to ensure Chromatic Armor has a smoother transition
	@EventHandler
	public void onShiftClick(InventoryClickEvent evt) {
		update(evt, (Player) evt.getWhoClicked());
	}

	// Listens for inventory events to ensure Chromatic Armor has a smoother transition
	@EventHandler
	public void onDrag(InventoryDragEvent evt) {
		update(evt, (Player) evt.getWhoClicked());
	}


	// Listens for item pickup events to ensure Chromatic Armor has a smoother transition
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

	// Listens for newly spawned item stacks to ensure Chromatic Armor has a smoother transition
	@EventHandler
	public void onDropItem(ItemSpawnEvent evt) {
		Helpers.updateHash(Storage.droppedHashes, Storage.droppedParams, evt.getEntity(), evt.getEntity().getItemStack(), 0);
	}

	// Listens for edited ItemFrames to ensure Chromatic Armor has a smoother transition
	@EventHandler
	public void onItemFrameEdit(PlayerInteractAtEntityEvent evt) {
		if (evt.getRightClicked() instanceof ItemFrame) {
			ItemFrame frame = (ItemFrame) evt.getRightClicked();
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(zedly.pyro.Core.Storage.pyro, () -> {
				Helpers.updateHash(Storage.frameHashes, Storage.frameParams, frame, frame.getItem(), 0);
			}, 0);

		}
	}

}
