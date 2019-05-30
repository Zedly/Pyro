package zedly.pyro;

//Random Firework Signs/TNT
//Random in GUI
//Make detonate signs by sneak right clicking and by command
//Add permissions for recipes, placing signs, everything
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;
import zedly.pyro.enums.Frequency;

public class Pyro extends JavaPlugin {

    public void onEnable() {
        Storage.pyro = this;
        Storage.pluginPath = Bukkit.getPluginManager().getPlugin("Pyro").getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        this.saveDefaultConfig();

        // Load Recipes?
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

        getCommand("chromo").setTabCompleter(new CommandProcessor.ChromoTabCompletion());

        getServer().getPluginManager().registerEvents(new Watcher(), this);
        getServer().getPluginManager().registerEvents(new ChromaticArmor(), this);

        for (Frequency f : Frequency.values()) {
            getServer().getScheduler().scheduleSyncRepeatingTask(this, new TaskRunner(f), 1, f.period);
        }

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
        for (Frequency f : Frequency.values()) {
            getServer().getScheduler().scheduleSyncRepeatingTask(this, new TaskRunner(f), 1, f.period);
        }
    }

    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        for (Item item : Storage.eastereggs.keySet()) {
            item.getWorld().dropItemNaturally(item.getLocation(), Storage.eastereggs.get(item));
            item.remove();
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String commandlabel, String[] args) {
        CommandProcessor.run(sender, command, commandlabel, args);
        return true;
    }
}
