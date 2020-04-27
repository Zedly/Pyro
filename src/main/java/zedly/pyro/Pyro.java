package zedly.pyro;

//Random Firework Signs/TNT
//Random in GUI
//Make detonate signs by sneak right clicking and by command
//Add permissions for recipes, placing signs, everything
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;
import zedly.pyro.command.CommandDelegator;

public class Pyro extends JavaPlugin {

    @Override
    public void onEnable() {
        Storage.pyro = this;
        this.saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new Watcher(), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new HFEffects(), 0, 1);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new PlayParty(), 0, 3);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new MFEffects(), 0, 10);
        String col = ChatColor.GOLD + "Colors: " + ChatColor.YELLOW + "";
        for (int i = 0; i < Storage.FW_COLOR_FRIENDLY_NAMES.length; i++) {
            col += Storage.FW_COLOR_FRIENDLY_NAMES[i];
            if (i != Storage.FW_COLOR_FRIENDLY_NAMES.length - 1) {
                col += ChatColor.GOLD + ", " + ChatColor.YELLOW + "";
            }
        }
        Storage.colorString = col;
        if (getConfig().getBoolean("recipes.remote-detonator", false)) {
            Recipes.remotes();
        }
        if (getConfig().getBoolean("recipes.rainbow-snowballs", false)) {
            Recipes.rainbowSnowballs();
        }
        if (getConfig().getBoolean("recipes.color-arrows", false)) {
            Recipes.colorArrow();
        }
        if (getConfig().getBoolean("recipes.bang-snowballs", false)) {
            Recipes.bang();
        }
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        for (Item item : Storage.eastereggs.keySet()) {
            item.getWorld().dropItemNaturally(item.getLocation(), Storage.eastereggs.get(item));
            item.remove();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandlabel, String[] args) {
        return CommandDelegator.onCommand(sender, command.getLabel().toLowerCase(), args);
    }
}
