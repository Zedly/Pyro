package zedly.pyro;

//Random Firework Signs/TNT
//Random in GUI
//Make detonate signs by sneak right clicking and by command
//Add permissions for recipes, placing signs, everything
import zedly.pyro.features.AdvancedProjectiles;
import zedly.pyro.features.ColorArrows;
import zedly.pyro.features.RemoteTNT;
import zedly.pyro.features.EasterEggs;
import zedly.pyro.features.BangSnowballs;
import zedly.pyro.features.CraftingGUI;
import zedly.pyro.features.TaskPartyFirework;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;
import zedly.pyro.command.CommandDelegator;
import zedly.pyro.features.FeatureClass;
import zedly.pyro.features.FireworkTNT;
import zedly.pyro.features.Rainboom;
import zedly.pyro.features.RainbowSnowballs;

public class Pyro extends JavaPlugin {

    @Override
    public void onEnable() {
        Storage.pyro = this;
        this.saveDefaultConfig();

        registerFeatureClass(AdvancedProjectiles.INSTANCE);
        registerFeatureClass(BangSnowballs.INSTANCE);
        registerFeatureClass(ColorArrows.INSTANCE);
        registerFeatureClass(CraftingGUI.INSTANCE);
        registerFeatureClass(EasterEggs.INSTANCE);
        registerFeatureClass(FireworkTNT.INSTANCE);
        registerFeatureClass(Rainboom.INSTANCE);
        registerFeatureClass(RainbowSnowballs.INSTANCE);
        registerFeatureClass(RemoteTNT.INSTANCE);
        registerFeatureClass(TaskPartyFirework.INSTANCE);
        
        getServer().getPluginManager().registerEvents(SignMechanics.INSTANCE, this);
        
        if (getConfig().getBoolean("recipes.remote-detonator", false)) {
            Recipes.registerRemoteRecipe();
        }
        if (getConfig().getBoolean("recipes.rainbow-snowballs", false)) {
            Recipes.registerRainbowRecipe();
        }
        if (getConfig().getBoolean("recipes.color-arrows", false)) {
            Recipes.registerColorArrowRecipe();
        }
        if (getConfig().getBoolean("recipes.bang-snowballs", false)) {
            Recipes.registerBangRecipe();
        }
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        for (Item item : EasterEggs.eastereggs.keySet()) {
            item.getWorld().dropItemNaturally(item.getLocation(), EasterEggs.eastereggs.get(item));
            item.remove();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandlabel, String[] args) {
        return CommandDelegator.onCommand(sender, command.getLabel().toLowerCase(), args);
    }

    private void registerFeatureClass(FeatureClass feature) {
        getServer().getScheduler().scheduleSyncRepeatingTask(this, feature, 0, feature.getTaskFrequency());
        getServer().getPluginManager().registerEvents(feature, this);
    }
}
