package zedly.pyro.features;

import java.util.*;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import zedly.pyro.Storage;
import zedly.pyro.Utilities;

public class EasterEggs extends FeatureClass {

    public static final String TRANSIENT_NAME = ChatColor.MAGIC + "Transient";
    public static final EasterEggs INSTANCE = new EasterEggs();
    public static final HashMap<Item, ItemStack> eastereggs = new HashMap<>();

    private final LinkedList<Player> glowViewers = new LinkedList<>();
    private final HashMap<Item, ItemStack> tempAddBack = new HashMap<>();

    private EasterEggs() {
    }

    @Override
    public int getTaskFrequency() {
        return 10;
    }

    @Override
    public void run() {
        Iterator it = eastereggs.entrySet().iterator();

        if (it.hasNext()) {
            glowViewers.clear();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("pyro.eegg")) {
                    glowViewers.add(p);
                }
            }
        }

        tempAddBack.clear();

        while (it.hasNext()) {
            Entry<Item, ItemStack> egg = (Entry<Item, ItemStack>) it.next();
            if (egg.getKey() == null || !egg.getKey().isValid()) {
                it.remove();
                ItemStack is = egg.getValue();
                for (Player p : glowViewers) {
                    p.sendMessage(Storage.logo + ChatColor.YELLOW + " An egg containing "
                            + is.getType() + " has been lost");
                }
                continue;
            } else if (egg.getKey().getTicksLived() > 1200) {
                Location loc = egg.getKey().getLocation();
                ItemStack eggContent = egg.getValue();
                ItemStack eggItem = egg.getKey().getItemStack();
                egg.getKey().remove();
                it.remove();
                Item item = loc.getWorld().dropItem(loc, eggItem);
                item.setVelocity(new Vector(0, 0, 0));
                tempAddBack.put(item, eggContent);
                for (Player p : glowViewers) {
                    Utilities.setEntityGlowing(item, p, true);
                }
            } else {
                for (Player p : glowViewers) {
                    Utilities.setEntityGlowing(egg.getKey(), p, true);
                }
            }
        }
        eastereggs.putAll(tempAddBack);
    }

    @EventHandler // Stops Easter eggs from being pickef up
    public boolean onItemPickup(InventoryPickupItemEvent evt) {
        Item item = evt.getItem();
        if (eastereggs.containsKey(item)) {
            evt.setCancelled(true);
            ItemStack newitem = eastereggs.get(item);
            evt.getItem().setItemStack(newitem);
            eastereggs.remove(item);
        }
        return true;
    }

    @EventHandler // Stops Easter eggs from being pickef up
    public void onItemPickup(EntityPickupItemEvent evt) {
        Item item = evt.getItem();
        if (eastereggs.containsKey(item)) {
            evt.setCancelled(true);
            if (!(evt.getEntity() instanceof Player)) {
                return;
            }
            ItemStack newitem = eastereggs.get(item);
            evt.getItem().setItemStack(newitem);
            eastereggs.remove(item);
        } else {
            ItemStack is = item.getItemStack();
            if (is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()
                    && is.getItemMeta().getDisplayName().equals(TRANSIENT_NAME)) {
                evt.setCancelled(true);
                item.remove();
            }
        }
        return;
    }

}
