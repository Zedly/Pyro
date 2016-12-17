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

public class Pyro extends JavaPlugin {

    @Override
    public void onEnable() {
        Storage.pyro = this;
        this.saveDefaultConfig();
        for (int x = this.getConfig().getList("Recipes").size() - 1; x >= 0; x--) {
            String str = "" + this.getConfig().getList("Recipes").get(x);
            boolean b;
            try {
                b = Boolean.parseBoolean(str.split("=")[1].replace("}", ""));
            } catch (NumberFormatException e) {
                b = true;
            }
            Storage.recipes.put(str.split("=")[0].replace("{", ""), b);
        }
        getServer().getPluginManager().registerEvents(new Watcher(), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new HFEffects(), 0, 1);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new TaskChromaticArmor(), 0, 1);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new PlayParty(), 0, 3);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new MFEffects(), 0, 10);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new TaskItemTrails(), 0, 10);
        String col = ChatColor.GOLD + "Colors: " + ChatColor.YELLOW + "";
        for (int i = 0; i < Storage.colors.length; i++) {
            col += Storage.colors[i];
            if (i != Storage.colors.length - 1) {
                col += ChatColor.GOLD + ", " + ChatColor.YELLOW + "";
            }
        }
        Storage.colorString = col;
        if (Storage.recipes.get("Remote Detonator")) {
            Recipes.remotes();
        }
        if (Storage.recipes.get("Rainbow Snowball")) {
            Recipes.snowballs();
        }
        if (Storage.recipes.get("New Color Arrow")) {
            Recipes.colorArrow();
        }
        if (Storage.recipes.get("New Chromatic Armor")) {
            Recipes.chromo();
        }
        if (Storage.recipes.get("Bang Snowball")) {
            Recipes.bang();
        }
        
        
        
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelAllTasks();
        for (Item item : Storage.eastereggs.keySet()) {
            item.getWorld().dropItemNaturally(item.getLocation(), Storage.eastereggs.get(item));
            item.remove();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandlabel, String[] args) {
        CommandProcessor.run(sender, command, commandlabel, args);
        return true;
    }
}
