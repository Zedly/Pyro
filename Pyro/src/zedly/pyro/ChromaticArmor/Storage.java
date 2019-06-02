package zedly.pyro.ChromaticArmor;

import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class Storage {

	protected static int t = 0;



	// Map from player to inventory slot to parameter array
	protected static final Map<Player, Map<Integer, double[]>> playerParams = new HashMap<>();

	// Map from player to inventory slot to item hash
	protected static final Map<Player, Map<Integer, Integer>> playerHashes = new HashMap<>();



	// Map from living entity (not a player) to inventory slot to parameter array
	protected static final Map<LivingEntity, Map<Integer, double[]>> entityParams = new HashMap<>();

	// Map from living entity (not a player) to inventory slot to item hash
	protected static final Map<LivingEntity, Map<Integer, Integer>> entityHashes = new HashMap<>();



	// Map from inventory to inventory slot to parameter array
	protected static final Map<Inventory, Map<Integer, double[]>> inventoryParams = new HashMap<>();

	// Map from inventory to inventory slot to item hash
	protected static final Map<Inventory, Map<Integer, Integer>> inventoryHashes = new HashMap<>();



	// Map from item frame to inventory slot (Always 0) to parameter array (Extra dimension added for generic helper functions)
	protected static final Map<ItemFrame, Map<Integer, double[]>> frameParams = new HashMap<>();

	// Map from item frame to inventory slot (Always 0) item hash (Extra dimension added for generic helper functions)
	protected static final Map<ItemFrame, Map<Integer, Integer>> frameHashes = new HashMap<>();



	// Map from dropped item stack to parameter array (Extra dimension added for generic helper functions)
	protected static final Map<Item, Map<Integer, double[]>> droppedParams = new HashMap<>();

	// Map from dropped item stack to item hash (Extra dimension added for generic helper functions)
	protected static final Map<Item, Map<Integer, Integer>> droppedHashes = new HashMap<>();



	// Map from player to cursor slot to parameter array (Extra dimension added for generic helper functions)
	protected static final Map<Player, Map<Integer, double[]>> cursorParams = new HashMap<>();

	// Map from player to cursor slot to item hash  (Extra dimension added for generic helper functions)
	protected static final Map<Player, Map<Integer, Integer>> cursorHashes = new HashMap<>();
}
