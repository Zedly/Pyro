package zedly.pyro;

import java.util.*;
import java.util.Map.Entry;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class MFEffects implements Runnable {

    @Override
    public void run() {
        //Easter Eggs
        Iterator it = Storage.eastereggs.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Item, ItemStack> egg = (Entry<Item, ItemStack>) it.next();
            if (egg.getKey() == null || egg.getKey().isDead()) {
                it.remove();
            } else if (egg.getKey().getTicksLived() > 1200) {
                Location loc = egg.getKey().getLocation();
                ItemStack eggContent = egg.getValue();
                ItemStack eggItem = egg.getKey().getItemStack();
                egg.getKey().remove();
                it.remove();
                Item item = loc.getWorld().dropItem(loc, eggItem);
                item.setVelocity(new Vector(0, 0, 0));
                Storage.eastereggs.put(item, eggContent);
            }
        }
    }
}
