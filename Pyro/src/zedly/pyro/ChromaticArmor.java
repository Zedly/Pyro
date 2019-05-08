package zedly.pyro;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import zedly.pyro.annotations.EffectTask;
import zedly.pyro.enums.Frequency;

public class ChromaticArmor {

    private static final Map<Integer, Integer> chromaticColorProgress = new HashMap<>();
    private static final Map<Integer, double[]> chromoParams = new HashMap<>();
    private static final Map<Integer, int[]> armorHashes = new HashMap<>();

    @EffectTask(Frequency.HIGH)
    public static void taskEntityChromaticArmor() {
        // TODO: Update to change for items in hands

        // Look through every entity on the server
        for (World world : Bukkit.getServer().getWorlds()) {
            for (LivingEntity entity : world.getLivingEntities()) {
                int entityId = entity.getEntityId();

                // Get the progression through the color cycle and increment it
                int counter = chromaticColorProgress.getOrDefault(entityId, 0);
                chromaticColorProgress.put(entityId, counter + 1);

                // Iterate through all the armor slots
                for (int armorSlotID = 0; armorSlotID < 4; armorSlotID++) {
                    ItemStack stk = entity.getEquipment().getArmorContents()[armorSlotID];

                    // Get the lore (if it exists)
                    if (stk != null && stk.hasItemMeta() && stk.getItemMeta().hasLore()
                        && !stk.getItemMeta().getLore().isEmpty()) {
                        if (Storage.COMPATIBILITY_ADAPTER.LeatherArmors().contains(stk.getType())) {
                            List<String> lore = stk.getItemMeta().getLore();

                            // Get the parameters for the chromatic armor (if it exists)
                            double[] params = null;
                            if (armorHashes.containsKey(entityId)
                                && lore.hashCode() == armorHashes.get(entityId)[armorSlotID]) { // If it has already been parsed
                                params = chromoParams.get(entityId);
                            } else {                                                            // If it hasn't been parsed
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
                                    continue;
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

                                // Update / Add the new hash code and params
                                chromoParams.put(entityId, params);
                                if (!armorHashes.containsKey(entityId)) {
                                    armorHashes.put(entityId, new int[4]);
                                }
                                armorHashes.get(entityId)[armorSlotID] = lore.hashCode();
                            }

                            // Get the color
                            int[] color = params == null ?
                                null
                                : params.length == 10 ?
                                    Utilities.getColor(params, counter)
                                    : params.length == 12 ?
                                        Utilities.getThemedColor(params, counter)
                                        : null;

                            if (color != null) {
                                // Chromatic armor does not wear
                                Utilities.setDamage(stk, 0);

                                LeatherArmorMeta meta = (LeatherArmorMeta) stk.getItemMeta();

                                // Special case for vanished players
                                if (entity instanceof Player) {
                                    if (Storage.vanishedPlayers.contains(entity)) {
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
                                }

                                // Set the item in the entity's armor slots
                                meta.setColor(Color.fromRGB(Utilities.clamp(color[0]), Utilities.clamp(color[1]),
                                    Utilities.clamp(color[2])));
                                stk.setItemMeta(meta);
                                switch (stk.getType()) {
                                    case LEATHER_HELMET:
                                        entity.getEquipment().setHelmet(stk);
                                        break;
                                    case LEATHER_CHESTPLATE:
                                        entity.getEquipment().setChestplate(stk);
                                        break;
                                    case LEATHER_LEGGINGS:
                                        entity.getEquipment().setLeggings(stk);
                                        break;
                                    case LEATHER_BOOTS:
                                        entity.getEquipment().setBoots(stk);
                                        break;
                                    default:
                                        break;
                                }

                                // Create sparkle trails
                                if (entity instanceof Player) {
                                    Player player = (Player) entity;
                                    if ((player.isFlying() || player.isSprinting()) && Storage.rnd.nextBoolean()
                                        && !Storage.vanishedPlayers.contains(player)) {
                                        Location loc = player.getLocation().clone();
                                        loc.subtract(player.getLocation().getDirection());
                                        float heightAboveFeetPos = 0;
                                        switch (stk.getType()) {
                                            case LEATHER_HELMET:
                                                heightAboveFeetPos = 1.75f;
                                                break;
                                            case LEATHER_CHESTPLATE:
                                                heightAboveFeetPos = 1.25f;
                                                break;
                                            case LEATHER_LEGGINGS:
                                                heightAboveFeetPos = .83f;
                                                break;
                                            case LEATHER_BOOTS:
                                                heightAboveFeetPos = .38f;
                                                break;
                                        }
                                        loc.setY(loc.getY() + heightAboveFeetPos + (Storage.rnd.nextFloat() / 4) * (
                                            Storage.rnd.nextInt(2) * 2 - 1));
                                        loc.setX(
                                            loc.getX() + (Storage.rnd.nextFloat() / 4) * (Storage.rnd.nextInt(2) * 2
                                                - 1));
                                        loc.setZ(
                                            loc.getZ() + (Storage.rnd.nextFloat() / 4) * (Storage.rnd.nextInt(2) * 2
                                                - 1));
                                        Color col = Color.fromRGB(Utilities.clamp(color[0]), Utilities.clamp(color[1]),
                                            Utilities.clamp(color[2]));

                                        loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1,
                                            new Particle.DustOptions(col, 1.0f));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @EffectTask(Frequency.HIGH)
    public static void taskInventoryChromaticArmor() {
        // TODO: Make it change while in the inventory
    }

    @EffectTask(Frequency.HIGH)
    public static void taskDroppedItemChromaticArmor() {
        // TODO: Make it change while dropped
    }

    @EffectTask(Frequency.HIGH)
    public static void taskItemFrameChromaticArmor() {
        // TODO: Make it change while in an item frame
    }
}
