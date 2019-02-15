package zedly.pyro;

import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

public class TNTExplosion {

    private int randomRadius = 4;
    private int repulsion = 0;
    public List<String> params;

    public TNTExplosion(List<String> params) {
        this.params = params;
    }

    public void explode(Entity entity) {
        Location loc = Utilities.getCenter(entity.getLocation().getBlock().getLocation());
        for (String s : params) {
            s = ChatColor.stripColor(s);
            if (s.length() != 15) {
                continue;
            }
            Utilities.explodeFromString(loc, s);
        }
        Location epicenter = loc.add(new Vector(0, -2.5, 0));
        for (Entity ent : entity.getNearbyEntities(10, 5, 10)) {
            Vector dv = ent.getLocation().subtract(epicenter).toVector();
            dv.multiply(repulsion / (dv.length() * dv.length() + 0.5));
            ent.setVelocity(dv);
        }
    }
}
