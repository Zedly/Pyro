package zedly.pyro.projectiles;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.SmallFireball;
import org.bukkit.inventory.ItemStack;
import zedly.pyro.Utilities;

public abstract class AdvancedProjectile {

    protected int tick = 0;
    protected SmallFireball sf;

    protected AdvancedProjectile(SmallFireball sf) {
        this.sf = sf;
    }

    private static final HashMap<String, ProjectileSource> projectileTable;

    public SmallFireball getEntity() {
        return sf;
    }

    public boolean isAlive() {
        return tick <= 600 && !sf.isDead();
    }

    public void incrementTick() {
        tick++;
    }

    public abstract void trail();

    public abstract void impact();

    /**
     * Performs all the checks to assure the given ItemStack is an advanced projectile.
     * @param is the item stack to check
     * @return true if the stack meets all ctireria for an advanced fireball
     */
    public static boolean isAdvancedFireball(ItemStack is) {
        if (!Utilities.matchItemStack(is, Material.FIRE_CHARGE, null, null)) {
            return false;
        }
        if (is.hasItemMeta() && is.getItemMeta().hasLore() && is.getItemMeta().getLore().size() >= 1
                && projectileTable.containsKey(is.getItemMeta().getLore().get(0))) {
            return true;
        }
        return false;
    }

    /**
     * Creates an AdvancedProjectile object based on the metadata of the given ItemStack. Assumes the lore is valid. Assert using isAdvancedFireball()
     * @param is the item stack to base the type of fireball on
     * @param sf the SmallFireball entity produced for the projectile object
     * @return 
     */
    public static AdvancedProjectile create(ItemStack is, SmallFireball sf) {
        String name = is.getItemMeta().getLore().get(0);
        if (!projectileTable.containsKey(name)) {
            return null;
        }
        return create(name, sf);
    }

    /**
     * Creates an AdvancedProjectile object based on its name. Assumes the name is valid. Assert using isAdvancedFireball()
     * @param name name to base the type of fireball on
     * @param sf the SmallFireball entity produced for the projectile object
     * @return 
     */
    public static AdvancedProjectile create(String name, SmallFireball sf) {
        if (!projectileTable.containsKey(name)) {
            return null;
        }
        return (AdvancedProjectile) projectileTable.get(name).produce(sf);
    }

    private static interface ProjectileSource {
        public AdvancedProjectile produce(SmallFireball sf);
    }

    static {
        projectileTable = new HashMap<>();
        projectileTable.put(ChatColor.GOLD + "Repulsor", (sf) -> {
            return new ProjectileRepulsor(sf);
        });
        projectileTable.put(ChatColor.YELLOW + "Spark", (sf) -> {
            return new ProjectileSpark(sf);
        });
        projectileTable.put(ChatColor.YELLOW + "Signal", (sf) -> {
            return new ProjectileSignal(sf);
        });
    }

}
