package zedly.pyro;

import java.util.*;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import static org.bukkit.Material.*;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.util.Vector;
import zedly.pyro.compatibility.BaseColor;

public class CommandProcessor {


	public static class ChromoTabCompletion implements TabCompleter {

		private static final String normalChromoStr[] =
			new String[]{"<Brigtness [0..127]>", "<Offset [0..]>", "<Speed [1..]>"};

		private static final String pastelChromoStr[] = new String[]{"<Offset [0..]>", "<Speed [1..]>"};

		private static final String grayscaleChromoStr[] =
			new String[]{"<Brigtness [0..127]>", "<Offset [0..]>", "<Speed [1..]>"};

		private static final String brightChromoStr[] = new String[]{"<Offset [0..]>", "<Speed [1..]>"};

		private static final String colorChromoStr[] = new String[]{};

		private static final String customChromoStr[] = new String[]{"<Red Frequency[-180..180]>",
			"<Green Frequency [-180..180]>", "<Blue Frequency [-180..180]>", "<Red Delay [0..]>",
			"<Green Delay [0..]>", "<Blue Delay [0..]>", "<Brightness [0..127>", "<Offset [0..]>",
			"<Speed [1..]>", "<Gamma [-1.0..]>"};

		private static final String chromoTypeStr[] = new String[]{"normal", "pastel", "grayscale", "bright", "color",
			"custom"};

		@Override
		public List<String> onTabComplete(CommandSender sender, Command commandlabel, String alias, String[] args) {
			if (args.length == 0 || !(sender instanceof Player)) {
				return null;
			}

			String label = args[0].toLowerCase();
			List<String> results = new LinkedList<>();

			if (args.length == 1) {
				for (String str : chromoTypeStr) {
					if (str.startsWith(args[0])) {
						results.add(str);
					}
				}
			} else {
				switch (label) {
					case "normal":
						if (args.length <= normalChromoStr.length + 1) {
							results.add(normalChromoStr[args.length - 2]);
						}
						break;
					case "pastel":
						if (args.length <= pastelChromoStr.length + 1) {
							results.add(pastelChromoStr[args.length - 2]);
						}
						break;
					case "grayscale":
						if (args.length <= grayscaleChromoStr.length + 1) {
							results.add(grayscaleChromoStr[args.length - 2]);
						}
						break;
					case "bright":
						if (args.length <= brightChromoStr.length + 1) {
							results.add(brightChromoStr[args.length - 2]);
						}
						break;
					case "color":
						if (args.length == 2) {
							for (Object objColor : Storage.COMPATIBILITY_ADAPTER.BaseColors().getEnumList()) {
								BaseColor color = (BaseColor) objColor;
								if (color.toString().toUpperCase().startsWith(args[1].toUpperCase())) {
									results.add(color.name());
								}
							}
						} else if (args.length <= pastelChromoStr.length + 2) {
							results.add(pastelChromoStr[args.length - 3]);
						}
						break;
					case "custom":
						if (args.length <= customChromoStr.length + 1) {
							results.add(customChromoStr[args.length - 2]);
						}
						break;
				}
			}
			return results;
		}
	}

	public static String getChromaticConfig(Player player, String[] args) {
		String str = null;

		// Normal parameters; These aren't used for the color option
		float redFrequency = 7;
		float greenFrequency = 7;
		float blueFrequency = 7;
		float redDelay = 0;
		float greenDelay = 120;
		float blueDelay = 240;
		float brightness = 0;
		float offset = 0;
		float speed = 0;
		float gamma = 1;

		switch (args[0].toLowerCase()) {
			case "custom":
				if (args.length == 11) {
					redFrequency = Float.parseFloat(args[1]);
					greenFrequency = Float.parseFloat(args[2]);
					blueFrequency = Float.parseFloat(args[3]);
					redDelay = Float.parseFloat(args[4]);
					greenDelay = Float.parseFloat(args[5]);
					blueDelay = Float.parseFloat(args[6]);
					brightness = Float.parseFloat(args[7]);
					offset = Float.parseFloat(args[8]);
					speed = Float.parseFloat(args[9]);
					gamma = Float.parseFloat(args[10]);
				} else {
					player.sendMessage(Storage.logo
						+ " <Red Frequency[-180..180]> <Green Frequency [-180..180]> <Blue Frequency [-180..180]> <Red"
						+ " Delay [0..]> <Green Delay [0..]> <Blue Delay [0..]> <Brightness [0."
						+ ".127> <Offset [0..]> <Speed [1..]> <Gamma [-1.0..]>");
					return null;
				}
				break;
			case "grayscale":
				if (args.length == 4) {
					greenDelay = 0;
					blueDelay = 0;
					brightness = Float.parseFloat(args[1]);
					offset = Float.parseFloat(args[2]);
					speed = Float.parseFloat(args[3]);
				} else {
					player.sendMessage(Storage.logo + " <Brigtness [0..127]> <Offset [0..]> <Speed [1..]>");
					return null;
				}
				break;
			case "normal":
				if (args.length == 4) {
					brightness = Float.parseFloat(args[1]);
					offset = Float.parseFloat(args[2]);
					speed = Float.parseFloat(args[3]);
				} else {
					player.sendMessage(Storage.logo + " <Brigtness [0..127]> <Offset [0..]> <Speed [1..]>");
					return null;
				}
				break;
			case "pastel":
				if (args.length == 3) {
					brightness = 35;
					offset = Float.parseFloat(args[1]);
					speed = Float.parseFloat(args[2]);
				} else {
					player.sendMessage(Storage.logo + " <Offset [0..]> <Speed [1..]>");
					return null;
				}
				break;
			case "bright":
				if (args.length == 3) {
					brightness = 127;
					offset = Float.parseFloat(args[1]);
					speed = Float.parseFloat(args[2]);
					gamma = 0.00f;
				} else {
					player.sendMessage(Storage.logo + " <Offset [0..]> <Speed [1..]>");
					return null;
				}
				break;
			case "color":
				if (args.length == 4) {
					int c = ArrayUtils.indexOf(Storage.colors, args[1].toLowerCase());
					offset = Float.parseFloat(args[2]);
					speed = Float.parseFloat(args[3]);
					if (ArrayUtils.contains(Storage.colors, args[1].toLowerCase())) {
						float[] p1 = {2, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
						float[] p2 = {3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
						float[] p3 = {180, 7, 10, 10, 7, 25, 10, 15, 15, 15, 10, 15, 10, 15, 15, 180};
						float[] p4 = {0.05f, 0, 0, 0.3f, 0, 0.1f, 0, 0, 0, 0.05f, 0, 0.05f, 0.05f, 0.05f, 0, 0.05f};
						float[] p5 =
							{0.05f, 0.1f, 0.15f, 0.15f, 0.15f, 0.15f, 0.1f, 0.1f, 0.15f, 0.05f, 0.20f, 0.25f, 0.1f,
								0.1f, 0.2f, 0.05f};
						float[] p6 = {62, 30, 300, 200, 62, 110, 327, 25, 25, 180, 280, 240, 25, 96, 0, 180};
						float[] p7 = {0.05f, 1, 1, 0.7f, 1, 0.9f, 1, 0, 0, 0.95f, 1, 0.95f, 0.55f, 0.93f, 1, 0.15f};
						float[] p8 =
							{0.95f, 0.9f, 0.85f, 0.85f, 0.85f, 0.85f, 0.9f, 0.45f, 0.75f, 0.75f, 0.80f, 0.75f, 0.45f,
								0.4f, 0.8f, 0.15f};

						str = speed + ":" + p1[c] + ":" + p2[c] + ":" + p3[c] + ":" + p4[c] + ":" + p5[c] + ":"
							+ p6[c] + ":" + p7[c] + ":" + p8[c] + ":" + offset + ":" + offset + ":" + offset;
						return Utilities.toInvisibleString("py.chromo.color." + str);
					}
				} else {
					player.sendMessage(Storage.logo + " <Color> <Offset [0..]> <Speed [1..]>");
					player.sendMessage(ChatColor.YELLOW + Storage.colorString);
					return null;
				}
				break;
		}

		// Parameter range check
		if (redFrequency < -180 || redFrequency > 180) {
			player.sendMessage(Storage.logo + " Red frequency must be -180 to 180");
			return null;
		}
		if (greenFrequency < -180 || greenFrequency > 180) {
			player.sendMessage(Storage.logo + " Green frequency must be -180 to 180");
			return null;
		}
		if (blueFrequency < -180 || blueFrequency > 180) {
			player.sendMessage(Storage.logo + " Blue frequency must be -180 to 180");
			return null;
		}
		if (redDelay < 0) {
			player.sendMessage(Storage.logo + " Red delay must be >= 0");
			return null;
		}
		if (greenDelay < 0) {
			player.sendMessage(Storage.logo + " Green delay must be >= 0");
			return null;
		}
		if (blueDelay < 0) {
			player.sendMessage(Storage.logo + " Blue delay must be >= 0");
			return null;
		}
		if (brightness < 0 || brightness > 127) {
			player.sendMessage(Storage.logo + " Brightness must be 0 to 127");
			return null;
		}
		if (offset < 0) {
			player.sendMessage(Storage.logo + " Offset must be >= 0");
			return null;
		}
		if (speed < 1) {
			player.sendMessage(Storage.logo + " Speed must be >= 1");
			return null;
		}
		if (gamma < -1) {
			player.sendMessage(Storage.logo + " Gamma must be >= -1");
			return null;
		}

		str = redFrequency + ":" + greenFrequency + ":" + blueFrequency + ":" + redDelay + ":" + greenDelay + ":" + blueDelay + ":"
			+ brightness + ":" + offset + ":" + speed + ":" + gamma;

		return Utilities.toInvisibleString("py.chromo.normal." + str);
	}

	private static void trl(Player player, String[] args) {
		String[] trlList = new String[]{"custom", "mineral", "color", "flower"};
		ArrayList<String> trails = new ArrayList<>();
		ArrayList<String> notTrails = new ArrayList<>();
		List<String> lore = player.getInventory().getItemInMainHand().getItemMeta().getLore();
		HashSet<String> oldItems = new HashSet<>();
		HashSet<String> newItems = new HashSet<>();
		if (lore != null) {
			for (String s : lore) {
				if (ArrayUtils.contains(trlList, ChatColor.stripColor(s.toLowerCase().replace(" trail", "")))) {
					trails.add(ChatColor.stripColor(s.toLowerCase().replace(" trail", "")));
				} else {
					String[] itemData = s.split(":");
					if (itemData.length <= 2) {
						Material m =
							Material.matchMaterial(ChatColor.stripColor(itemData[0].toUpperCase().replace(" ", "_")));//
						if (m != null) {
							if (m != Material.AIR && !ArrayUtils.contains(Storage.badIds, m.getId())) {
								if (itemData.length == 2) {
									int i = Integer.parseInt(ChatColor.stripColor(s.split(":")[1]));
									oldItems.add(m + ":" + i);
								}
							}
							continue;
						}
					}
					notTrails.add(s);
				}
			}
		}
		for (String s : args) {
			int i = 0;
			if (s.contains(":")) {
				try {
					i = Integer.parseInt(s.split(":")[1]);
				} catch (NumberFormatException e) {
					i = 0;
				}
			}
			Material m = Material.matchMaterial(s.split(":")[0].toUpperCase().replace(" ", "_"));
			if (m == AIR || m == null) {
				int id = 0;
				try {
					id = Integer.parseInt(s.split(":")[0]);
				} catch (NumberFormatException e) {
				}
				//m = Material.getMaterial(id);
			}
			if (m != AIR && m != null && !ArrayUtils.contains(Storage.badIds, m.getId())) {
				newItems.add(m + ":" + i);
			}
		}
		if (args[0].equalsIgnoreCase("add")) {
			if (ArrayUtils.contains(trlList, args[1].toLowerCase()) && !trails.contains(args[1].toLowerCase())) {
				trails.add(args[1].toLowerCase());
			}
			oldItems.addAll(newItems);
		} else if (args[0].equalsIgnoreCase("del")) {
			if (ArrayUtils.contains(trlList, args[1].toLowerCase())) {
				trails.remove(args[1].toLowerCase());
			}
			if (args.length == 2 && args[1].equalsIgnoreCase("custom")) {
				oldItems.clear();
			} else {
				oldItems.removeAll(newItems);
			}
		}
		if (oldItems.isEmpty()) {
			trails.remove("custom");
		} else if (!trails.contains("custom")) {
			trails.add("custom");
		}
		List<String> newLore = new ArrayList<>();
		newLore.addAll(notTrails);
		for (String trl : trails) {
			newLore.add(ChatColor.GOLD + WordUtils.capitalize(trl) + " Trail");
		}
		for (String s : oldItems) {
			newLore.add(ChatColor.GREEN + WordUtils.capitalize(s.toLowerCase().replace("_", " ")));
		}
		player.sendMessage(Storage.logo + " Trail effect changed.");
		ItemMeta meta = player.getItemInHand().getItemMeta();
		meta.setLore(newLore);
		player.getItemInHand().setItemMeta(meta);
	}

	public static void run(CommandSender sender, Command command, String commandlabel, String[] args) {
		if (!(sender instanceof Player)) {
			return;
		}
		switch (commandlabel.toLowerCase()) {
			case "cannon": {
				if (!sender.hasPermission("pyro.cannon") || !(sender instanceof Player)) {
					sender.sendMessage(Storage.logo + " You do not have permission to do this!");
					return;
				}
				if (args.length == 1) {
					Material m;
					short data;
					try {
						data = Short.parseShort(args[0].split(":")[1]);
					} catch (Exception e) {
						data = 0;
					}
					try {
						//m = Material.getMaterial(Integer.parseInt(args[0].split(":")[0]));
						m = Material.AIR;
					} catch (Exception e) {
						m = Material.getMaterial(args[0].split(":")[0].toUpperCase());
					}
					if (m != null && !ArrayUtils.contains(Storage.badIds, m.getId())) {
						Player player = (Player) sender;
						player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_DEATH, 10, 100f);
						final Item item = player.getWorld().dropItem(player.getLocation().add(new Vector(0, 1.62, 0)),
							new ItemStack(m, 0, data));
						item.setVelocity(player.getLocation().getDirection().multiply(1.5));
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.pyro, new Runnable() {
							@Override
							public void run() {
								item.getWorld().createExplosion(item.getLocation(), 0);
								item.remove();
							}
						}, 40);
					} else {
						sender.sendMessage(Storage.logo + " " + args[0] + " is not an item!");
					}
				} else {
					sender.sendMessage(Storage.logo + " /cannon <Item ID/Item Name>:<MaterialData?>");
				}
				break;
			}
			case "color": {
				if (!sender.hasPermission("pyro.colorarrow")) {
					sender.sendMessage(Storage.logo + " You do not have permission to do this!");
					return;
				}
				if (args.length < 1) {
					sender.sendMessage(
						Storage.logo + " /color <red [0..255]> <green [0..255]> <blue [0..255]>\n/color <color name>\n"
							+ Storage.colorString);
				} else {
					int[] color = new int[]{0, 0, 0};
					try {
						color[0] = Math.min(Math.abs(Integer.parseInt(args[0])), 255);
						color[1] = Math.min(Math.abs(Integer.parseInt(args[1])), 255);
						color[2] = Math.min(Math.abs(Integer.parseInt(args[2])), 255);
					} catch (Exception e) {
						if (ArrayUtils.contains(Storage.colors, args[0].toLowerCase())) {
							color = Storage.color[ArrayUtils.indexOf(Storage.colors, args[0].toLowerCase())];
						} else {
							sender.sendMessage(Storage.logo
								+ " /color <red [0..255]> <green [0..255]> <blue [0..255]>\n/color <color name>\n"
								+ Storage.colorString);
							return;
						}
					}
					Player player = (Player) sender;
					if (player.getInventory().getItemInMainHand().getType().equals(ARROW)) {
						ItemStack stk = player.getInventory().getItemInMainHand();
						ItemMeta meta = stk.getItemMeta();
						List<String> lore = new ArrayList<>();
						lore.add(ChatColor.GOLD + "Color Arrow");
						lore.add(ChatColor.RED + "R: " + color[0] + ChatColor.WHITE + ", " + ChatColor.GREEN + "G: "
							+ color[1] + ChatColor.WHITE + ", " + ChatColor.BLUE + "B: " + color[2]);
						meta.setLore(lore);
						stk.setItemMeta(meta);
						player.sendMessage(Storage.logo + " Color arrow created!");
					} else {
						player.sendMessage(Storage.logo + " You need to be holding an arrow!");
					}
				}
				break;
			}
			case "chromo": {
				// Set up player and item and check if the player has valid permissions
				Player player = (Player) sender;
				ItemStack hand = player.getInventory().getItemInMainHand();
				if (!player.hasPermission("pyro.chromo")) {
					player.sendMessage(Storage.logo + " You do not have permission to do this!");
					return;
				}
				// Make sure we're adding chromo to a valid item
				if (Storage.COMPATIBILITY_ADAPTER.LeatherArmors().contains(hand.getType())) {
					if (args.length == 0) {
						sender.sendMessage(Storage.logo
							+ " Chromatic Armor Types: \nCustom\nGrayscale\nNormal\nPastel\nBright\nColor");
						return;
					}

					// Ensure that the parameters are valid numbers
					boolean allValid = true;
					for (int i = 1; i < args.length; i++) {
						try {
							Float.parseFloat(args[i]);
						} catch (NumberFormatException e) {
							if (!(i == 1 && ArrayUtils.contains(Storage.colors, args[i].toLowerCase()))) {
								allValid = false;
							}
						}
					}
					if (!allValid) {
						player.sendMessage(Storage.logo + " One of those values is not a number!");
						return;
					}

					// Get the lore of the item we're adding chromatic armor to
					ItemMeta meta = hand.getItemMeta();
					List<String> lore = meta.hasLore() ? hand.getItemMeta().getLore() : new ArrayList<>();
					String config = getChromaticConfig(player, args);

					if (config != null) {
						String chromoLoreString = ChatColor.GREEN + "Chromatic Armor: " + ChatColor.GOLD + WordUtils.capitalize(args[0].replace("_", " ")) + ChatColor.RESET + config;

						// Remove old, valid chromatic armor lore
						for (int i = 0; i < lore.size(); i++) {
							Map<String, Boolean> strInfo = Utilities.fromInvisibleString(lore.get(i));
							for (Map.Entry<String, Boolean> entry : strInfo.entrySet()) {
								if (!entry.getValue() && entry.getKey().startsWith("py.chromo.")) {
									lore.remove(i);
									i--;
								}
							}
						}
						lore.add(0, chromoLoreString);
						meta.setLore(lore);
						hand.setItemMeta(meta);

						player.sendMessage(Storage.logo + " Chromatic Armor created!");
						Bukkit.broadcastMessage(Utilities.fromInvisibleString(lore.get(0)).toString());
					}
				} else {
					player.sendMessage(Storage.logo + " You need to be holding a leather armor piece!");
				}
				break;
			}

			case "trl": {
				if (!sender.hasPermission("pyro.trail")) {
					sender.sendMessage(Storage.logo + " You do not have permission to do this!");
					return;
				}
				Player player = (Player) sender;
				Material mat = player.getInventory().getItemInMainHand().getType();
				if (args.length == 0 || "help".equalsIgnoreCase(args[0])) {
					player.sendMessage(Storage.logo + " For Normal Trails: /trl add <trail>");
					player.sendMessage(Storage.logo + " For Custom Trails: /trl add <Item ID:Data> ...");
					break;
				}
				if (mat == DIAMOND_CHESTPLATE || mat == IRON_CHESTPLATE || mat == CHAINMAIL_CHESTPLATE
					|| mat == GOLDEN_CHESTPLATE || mat == LEATHER_CHESTPLATE) {
					ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
					if ("add".equalsIgnoreCase(args[0]) || "del".equalsIgnoreCase(args[0]) && args.length > 1) {
						trl(player, args);
					} else if ("list".equalsIgnoreCase(args[0])) {
						player.sendMessage(Storage.logo + " Trails:\nFlower\nColor\nMineral\nCustom");
					}
				} else {
					player.sendMessage(Storage.logo + " You need to be holding a chestplate!");
				}
				break;
			}
			case "remote": {
				if (!sender.hasPermission("pyro.remote")) {
					sender.sendMessage(Storage.logo + " You do not have permission to do this!");
					return;
				}
				if (args.length < 2) {
					sender.sendMessage(Storage.logo + " /remote <add/del> <channel name>");
					return;
				}
				Player player = (Player) sender;
				ItemStack is = player.getInventory().getItemInMainHand();
				if (is.getType() != TNT && is.getType() != NAME_TAG && is.getType() != SIGN) {
					player.sendMessage(Storage.logo + " You need to be holding TNT, a Name Tag, or a Sign!");
					return;
				}
				ItemMeta meta = is.getItemMeta();
				List<String> lore;
				if (meta.hasLore()) {
					if (meta.getLore().get(0).contains(ChatColor.GREEN + "Remote")) {
						lore = meta.getLore();
						lore.remove(0);
					} else {
						lore = new LinkedList<>();
					}
				} else {
					lore = new LinkedList<>();
				}
				if (args[0].equalsIgnoreCase("add") && !lore.contains(ChatColor.GOLD + args[1])) {
					if (is.getType() == SIGN && lore.size() > 2) {
						lore.remove(2);
					}
					lore.add(0, ChatColor.GOLD + args[1]);
					player.sendMessage(Storage.logo + " Added remote channel!");
				} else if (args[0].equalsIgnoreCase("del") && lore.contains(ChatColor.GOLD + args[1])) {
					lore.remove(ChatColor.GOLD + args[1]);
					player.sendMessage(Storage.logo + " Removed remote channel!");
				}
				if (is.getType() == TNT) {
					lore.add(0, ChatColor.GREEN + "Remote Controlled");
				} else if (is.getType().equals(NAME_TAG)) {
					lore.add(0, ChatColor.GREEN + "Remote Detonator");
				} else if (is.getType() == SIGN) {
					lore.add(0, ChatColor.GREEN + "Remote Detonate Sign");
				}
				if (lore.size() > 1) {
					meta.setLore(lore);
				} else {
					meta.setLore(new LinkedList<>());
				}
				is.setItemMeta(meta);
				break;
			}
			case "py": {
				if (args.length > 0) {
					switch (args[0]) {
						case "egg":
						case "xmas": {
							if (!sender.hasPermission("pyro.egg")) {
								sender.sendMessage(Storage.logo + " You do not have permission to do this!");
								return;
							}
							Player player = (Player) sender;
							ItemStack items = player.getInventory().getItemInMainHand();
							if (items == null || items.getType() == Material.AIR) {
								sender.sendMessage(Storage.logo + " You must have something in your hand!");
								return;
							}
							player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
							ItemStack disguise;
							if (args[0].equals("egg")) {
								disguise = null;//new ItemStack(Material.MONSTER_EGG);
								SpawnEggMeta meta = (SpawnEggMeta) disguise.getItemMeta();
								meta.setSpawnedType(
									Storage.EASTER_EGG_TYPES[Storage.rnd.nextInt(Storage.EASTER_EGG_TYPES.length)]);
								meta.setDisplayName(ChatColor.MAGIC + "Transient");
								disguise.setItemMeta(meta);
							} else {
								disguise = new ItemStack(Material.CHEST);
								ItemMeta meta = disguise.getItemMeta();
								meta.setDisplayName(ChatColor.MAGIC + "Transient");
								disguise.setItemMeta(meta);
							}
							Item ent = (Item) player.getWorld().dropItem(player.getLocation().add(new Vector(0, 1, 0)),
								disguise);
							ent.setVelocity(player.getLocation().getDirection().multiply(0.5));
							Storage.eastereggs.put(ent, items);
							ent.setPickupDelay(128);
							break;
						}
						case "eggs": {
							if (!sender.hasPermission("pyro.egg")) {
								sender.sendMessage(Storage.logo + " You do not have permission to do this!");
								return;
							}
							sender.sendMessage(Storage.logo + " " + Storage.eastereggs.size() + " egg(s) remaining!");
							if (args.length > 1 && args[1].equals("-i")) {
								for (Item i : Storage.eastereggs.keySet()) {
									sender.sendMessage(
										"- World: " + ChatColor.UNDERLINE + i.getLocation().getWorld().getName() +
											ChatColor.RESET + " at location: " + i.getLocation().getBlockX() + ", " +
											i.getLocation().getBlockY() + ", " + i.getLocation().getBlockZ());
								}

							}
							break;
						}
						case "party": {
							if (args.length == 2) {
								if (args[1].equalsIgnoreCase("global")) {
									if (!sender.hasPermission("pyro.partyglobal")) {
										sender.sendMessage(Storage.logo + " You do not have permission to do this!");
										return;
									}
									if (Storage.globalparty) {
										Storage.globalparty = false;
										Storage.partyPlayers.removeAll(Bukkit.getOnlinePlayers());
										sender.sendMessage(Storage.logo + " Stopping the party, hiding the drugs.");
									} else {
										Storage.globalparty = true;
										Storage.partyPlayers.addAll(Bukkit.getOnlinePlayers());
										sender.sendMessage(Storage.logo + " Let's get this party started!");
									}
									return;
								}
							}
							if (!sender.hasPermission("pyro.party")) {
								sender.sendMessage(Storage.logo + " You do not have permission to do this!");
								return;
							}
							if (Storage.partyPlayers.contains((Player) sender)) {
								if (Storage.partyPlayers.size() == 1) {
									sender.sendMessage(Storage.logo + " Nope, one is not a party...");
								} else {
									sender.sendMessage(Storage.logo + " \"I have a thing to go do..\"");
								}
								Storage.partyPlayers.remove((Player) sender);
							} else {
								if (Storage.partyPlayers.isEmpty()) {
									sender.sendMessage(Storage.logo + " One's a party! Right..?");
								} else {
									sender.sendMessage(Storage.logo + " \"I was totally invited to this..\"");
								}
								Storage.partyPlayers.add((Player) sender);
							}
							break;
						}

						case "firework": {
							if (!sender.hasPermission("pyro.firework")) {
								sender.sendMessage(Storage.logo + " You do not have permission to do this!");
								return;
							}
							Storage.inventories.put((Player) sender, new CraftedFirework());
							//CraftingGUI.updatePage(1, (Player) sender);
							break;
						}
						case "help": {
							sender.sendMessage(
								Storage.logo + "\n- /py egg: Drops the held item, disguised as a random mob egg.");
							sender.sendMessage(ChatColor.YELLOW + "- /py eggs: Shows remaining eggs from /py egg.");
							sender.sendMessage(
								ChatColor.YELLOW + "- /py firework: Opens a GUI for crafting firework items.");
							sender.sendMessage(
								ChatColor.YELLOW + "- /py party <global?>: Starts a party of particles and fireworks"
									+ ".");
							break;
						}
						default: {
							sender.sendMessage(Storage.logo + " Unknown command. Try /py help");
						}
					}
				} else {
					sender.sendMessage(
						Storage.logo + "\n- /py egg: Drops the held item, disguised as a random mob egg.");
					sender.sendMessage(ChatColor.YELLOW + "- /py eggs: Shows remaining eggs from /py egg.");
					sender.sendMessage(ChatColor.YELLOW + "- /py firework: Opens a GUI for crafting firework items.");
					sender.sendMessage(
						ChatColor.YELLOW + "- /py party <global?>: Starts a party of particles and fireworks.");
				}
			}
			break;
		}
	}
}
