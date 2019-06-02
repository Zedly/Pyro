package zedly.pyro;

import java.util.*;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import zedly.pyro.Core.Storage;
import zedly.pyro.Core.Utilities;
import zedly.pyro.annotations.EffectTask;
import zedly.pyro.enums.Frequency;

public class EasterEggs {

    private static final List<Player> glowViewers = new LinkedList<>();
    private static final Map<Item, ItemStack> tempAddBack = new HashMap<>();

    @EffectTask(Frequency.MEDIUM_LOW)
    public static void taskEasterEggs() {

        Iterator it = Storage.eastereggs.entrySet().iterator();

        if (it.hasNext()) {
            glowViewers.clear();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("pyro.egg")) {
                    glowViewers.add(p);
                }
            }
        }

        tempAddBack.clear();

        while (it.hasNext()) {
            Entry<Item, ItemStack> egg = (Entry<Item, ItemStack>) it.next();
            if (egg.getKey() == null || egg.getKey().isDead()) {
                it.remove();
                ItemStack is = egg.getValue();
                for (Player p : glowViewers) {
                    p.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + ChatColor.DARK_RED + "] " + ChatColor.YELLOW + "An egg containing "
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
        Storage.eastereggs.putAll(tempAddBack);
    }
}