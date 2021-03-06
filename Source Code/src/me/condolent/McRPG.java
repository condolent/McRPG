package me.condolent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;

import net.gravitydevelopment.updater.Updater;
import net.gravitydevelopment.updater.Updater.UpdateResult;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class McRPG extends JavaPlugin implements Listener {
	
	public Permission admin = new Permission("mcrpg.admin");
	public Permission moderator = new Permission("mcrpg.moderator");

	private static Plugin plugin;
	public static Plugin getPlugin() {
		return plugin;
	}
	
	Events events;
	
	PluginDescriptionFile pdf = getDescription();
	
	private Scoreboard board;
	private Objective o;
	private Score onlineTitle;
	private Score players;
	private Score space;
	private Score space2;
	private Score space3;
	private Score space4;
	private Score FirstTitle;
	private Score FirstText;
	private Score SecondTitle;
	private Score SecondText;
	
	@SuppressWarnings("deprecation")
	public void onEnable() {
		getLogger().info("McRPG successfully enabled! :)");
		this.getServer().getPluginManager().registerEvents(new Events(this), this);
		this.getServer().getPluginManager().registerEvents(new Achievement(this), this);
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getPluginManager().registerEvents(new Chat(this), this);
		
		board = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
		
		o = board.registerNewObjective("steps", "dummy");
		o.setDisplayName(ChatColor.AQUA  + "       �lStats      ");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		onlineTitle = o.getScore(Bukkit.getServer().getOfflinePlayer(ChatColor.GREEN + "�lOnline:"));
		space = o.getScore(Bukkit.getServer().getOfflinePlayer(""));
		space2 = o.getScore(Bukkit.getServer().getOfflinePlayer(" "));
		space3 = o.getScore(Bukkit.getServer().getOfflinePlayer("  "));
		space4 = o.getScore(Bukkit.getServer().getOfflinePlayer("   "));
		FirstTitle = o.getScore(Bukkit.getServer().getOfflinePlayer("" + getConfig().getString("first_title").replaceAll("&", "�")));
		FirstText = o.getScore(Bukkit.getServer().getOfflinePlayer("" + getConfig().getString("first_text").replaceAll("&", "�")));
		SecondTitle = o.getScore(Bukkit.getServer().getOfflinePlayer("" + getConfig().getString("second_title").replaceAll("&", "�")));
		SecondText = o.getScore(Bukkit.getServer().getOfflinePlayer("" + getConfig().getString("second_text").replaceAll("&", "�")));
		
		
		
		getCommand("supply").setExecutor(new SupplyItems(this));
		getCommand("rpg").setExecutor(new RpgCmd(this));
		getCommand("w").setExecutor(new Whisper(this));
		getCommand("y").setExecutor(new Yell(this));
		getCommand("heal").setExecutor(new SimpleCommands(this));
		getCommand("ci").setExecutor(new SimpleCommands(this));
		getCommand("setspawn").setExecutor(new SimpleCommands(this));
		getCommand("spawn").setExecutor(new SimpleCommands(this));
		getCommand("vanish").setExecutor(new SimpleCommands(this));
		getCommand("class").setExecutor(new Classes(this));
		getCommand("gm").setExecutor(new GameMode(this));
		getCommand("faction").setExecutor(new Factions(this));
		getCommand("balance").setExecutor(new Currency(this));
		getCommand("sell").setExecutor(new Currency(this));
		getCommand("achievement").setExecutor(new Achievement(this));
		plugin = this;
		
		getConfig().addDefault("stat_monitor", true);
		
		getConfig().options().copyDefaults();
		saveDefaultConfig();
		saveDefaultPlayerLogging();
		saveDefaultPlayerFactions();
		saveDefaultPlayerCurrency();
		saveDefaultPlayerAchievements();

		if(getConfig().getString("auto_update", "notify").equalsIgnoreCase("notify")) {
			Updater updater = new Updater(this, 75582, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, true);
			if(updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
				getLogger().info("********* McRPG Auto-Updater *********");
				getLogger().info("*      A new version of McRPG is     *");
				getLogger().info("*       available for download!      *");
				getLogger().info("*             " + updater.getLatestName() + "             *");
				getLogger().info("**************************************");
			}
		} else if(getConfig().getString("auto_update", "auto").equalsIgnoreCase("auto")) {
			@SuppressWarnings("unused")
			Updater updater = new Updater(this, 75582, this.getFile(), Updater.UpdateType.DEFAULT, true);
		} else if(getConfig().getString("auto_update", "disable").equalsIgnoreCase("disable")) {
			
		}
		
		// Sword of a Thousand Truths
		ItemStack stt = new ItemStack(Material.DIAMOND_SWORD, 1);
		stt.addEnchantment(Enchantment.DAMAGE_ALL, 3);
		ItemMeta mstt = stt.getItemMeta();
		mstt.setDisplayName(ChatColor.DARK_PURPLE + "Sword of a Thousand Truths");
		mstt.setLore(Arrays.asList("Foretold by Saltzman."));
		stt.setItemMeta(mstt);
		
		ShapedRecipe rstt = new ShapedRecipe(stt);
		rstt.shape(" # ", "!#!", " ! ").setIngredient('#', Material.DIAMOND_BLOCK).setIngredient('!', Material.STICK);
		
		// Stick of truth
		ItemStack st = new ItemStack(Material.STICK, 1);
		ItemMeta mst = st.getItemMeta();
		mst.setDisplayName(ChatColor.DARK_PURPLE + "Stick of Truth");
		mst.setLore(Arrays.asList("The one who possesses the stick of truth", "has the ability to control", "the universe!"));
		st.setItemMeta(mst);
		
		ShapedRecipe rst = new ShapedRecipe(st);
		rst.shape(" ! ", "���", " ! ").setIngredient('�', Material.DIAMOND_BLOCK).setIngredient('!', Material.STICK);
		
		// Malevolent Mail PVP gear
		// Head
		ItemStack mailpvphead = new ItemStack(Material.CHAINMAIL_HELMET, 1);
		mailpvphead.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		mailpvphead.addEnchantment(Enchantment.OXYGEN, 1);
		mailpvphead.addEnchantment(Enchantment.THORNS, 1);
		ItemMeta mpvphead = mailpvphead.getItemMeta();
		mpvphead.setDisplayName(ChatColor.DARK_PURPLE + "Malevolent Gladiator's Chain Helm");
		mpvphead.setLore(Arrays.asList("Season 1", "+43 PvP Power", "+48 Mastery"));
		mailpvphead.setItemMeta(mpvphead);
			
		ShapedRecipe rmailpvphead = new ShapedRecipe(mailpvphead);
		rmailpvphead.shape("###", "#�#", "   ").setIngredient('#', Material.IRON_INGOT).setIngredient('�', Material.DIAMOND);
		
		// Chest
		ItemStack mailpvpchest = new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1);
		mailpvpchest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
		mailpvpchest.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1);
		mailpvpchest.addEnchantment(Enchantment.THORNS, 2);
		ItemMeta mpvpchest = mailpvpchest.getItemMeta();
		mpvpchest.setDisplayName(ChatColor.DARK_PURPLE + "Malevolent Gladiator's Chain Armor");
		mpvpchest.setLore(Arrays.asList("Season 1", "+43 PvP Power", "+72 Haste"));
		mailpvpchest.setItemMeta(mpvpchest);
		
		ShapedRecipe rmailpvpchest = new ShapedRecipe(mailpvpchest);
		rmailpvpchest.shape("#�#", "###", "###").setIngredient('#', Material.IRON_INGOT).setIngredient('�', Material.DIAMOND);
		
		// Legs
		ItemStack mailpvplegs = new ItemStack(Material.CHAINMAIL_LEGGINGS, 1);
		mailpvplegs.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		mailpvplegs.addEnchantment(Enchantment.PROTECTION_FIRE, 2);
		ItemMeta mpvplegs = mailpvplegs.getItemMeta();
		mpvplegs.setDisplayName(ChatColor.DARK_PURPLE + "Malevolent Gladiator's Chain Leggings");
		mpvplegs.setLore(Arrays.asList("Season 1", "+43 PvP Power"));
		mailpvplegs.setItemMeta(mpvplegs);
		
		ShapedRecipe rmailpvplegs = new ShapedRecipe(mailpvplegs);
		rmailpvplegs.shape("###", "#�#", "#�#").setIngredient('#', Material.IRON_INGOT).setIngredient('�', Material.DIAMOND);
		
		// Feet
		ItemStack mailpvpfeet = new ItemStack(Material.CHAINMAIL_BOOTS, 1);
		mailpvpfeet.addEnchantment(Enchantment.PROTECTION_FALL, 2);
		mailpvpfeet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		mailpvpfeet.addEnchantment(Enchantment.PROTECTION_FIRE, 2);
		ItemMeta mpvpfeet = mailpvpfeet.getItemMeta();
		mpvpfeet.setDisplayName(ChatColor.DARK_PURPLE + "Malevolent Gladiator's Chain Boots");
		mpvpfeet.setLore(Arrays.asList("Season 1", "+43 PvP Power"));
		mailpvpfeet.setItemMeta(mpvpfeet);
		
		ShapedRecipe rmailpvpfeet = new ShapedRecipe(mailpvpfeet);
		rmailpvpfeet.shape("#�#", "#�#", "   ").setIngredient('#', Material.IRON_INGOT).setIngredient('�', Material.DIAMOND);
		
		// Plate PVE gear
		// Head
		ItemStack platepvehead = new ItemStack(Material.IRON_HELMET, 1);
		platepvehead.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		ItemMeta mplatepvehead = platepvehead.getItemMeta();
		mplatepvehead.setDisplayName(ChatColor.DARK_PURPLE + "Greathelm of the Warchief");
		mplatepvehead.setLore(Arrays.asList("Season 1", "Still has some stains of orc-blood."));
		platepvehead.setItemMeta(mplatepvehead);
		
		ShapedRecipe rplatepvehead = new ShapedRecipe(platepvehead);
		rplatepvehead.shape("!!!", "!#!", "�#%").setIngredient('!', Material.IRON_INGOT).setIngredient('#', Material.OBSIDIAN).setIngredient('�', Material.BONE).setIngredient('%', Material.ROTTEN_FLESH);
		
		// Chest
		ItemStack platepvechest = new ItemStack(Material.IRON_CHESTPLATE, 1);
		ItemMeta mplatepvechest = platepvechest.getItemMeta();
		mplatepvechest.setDisplayName(ChatColor.DARK_PURPLE + "Icy Blood Chestplate");
		mplatepvechest.setLore(Arrays.asList("Season 1", "+13 Mastery"));
		platepvechest.setItemMeta(mplatepvechest);
		
		ShapedRecipe rplatepvechest = new ShapedRecipe(platepvechest);
		rplatepvechest.shape("   ", " # ", "   ").setIngredient('#', Material.DIAMOND_CHESTPLATE);
		
		
		
		// Add recipes
		getServer().addRecipe(rplatepvechest);
		getServer().addRecipe(rplatepvehead);
		getServer().addRecipe(rstt);
		getServer().addRecipe(rmailpvpchest);
		getServer().addRecipe(rst);
		getServer().addRecipe(rmailpvphead);
		getServer().addRecipe(rmailpvplegs);
		getServer().addRecipe(rmailpvpfeet);
	}
	
	public void onDisable() {
		getLogger().info("Disabling McRPG...");
		getServer().clearRecipes();
		saveDefaultConfig();
		saveDefaultPlayerLogging();
		saveDefaultPlayerFactions();
		saveDefaultPlayerCurrency();
		saveDefaultPlayerAchievements();
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		
		if(p.isOp()) {
			if(getConfig().getString("auto_update", "notify").equalsIgnoreCase("notify")) {
				Updater updater = new Updater(this, 75582, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, true);
				if(updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
					p.sendMessage("�l[" + ChatColor.RED + "�lWARNING" + ChatColor.WHITE + "�l] " + ChatColor.LIGHT_PURPLE + "You're running an outdated version of �lMcRPG�r" + ChatColor.LIGHT_PURPLE + ". " + updater.getLatestName() + " is available for download at: " + ChatColor.AQUA + updater.getLatestFileLink());
				}
			} else if(getConfig().getString("auto_update", "auto").equalsIgnoreCase("auto")) {
				
			} else if(getConfig().getString("auto_update", "disable").equalsIgnoreCase("disable")) {
				
			}
		}
		
		if(getConfig().getBoolean("stat_monitor")) {
			players = o.getScore(Bukkit.getServer().getOfflinePlayer("" + Bukkit.getServer().getOnlinePlayers().size()));
			
			p.setScoreboard(board);
			
			space2.setScore(10);
			onlineTitle.setScore(9);
			players.setScore(8);
			space.setScore(7);
			FirstTitle.setScore(6);
			FirstText.setScore(5);
			space3.setScore(4);
			SecondTitle.setScore(3);
			SecondText.setScore(2);
			space4.setScore(1);
			
		} else if(!getConfig().getBoolean("stat_monitor")) {

		}
	}
	
	
	// 	PlayerClasses.YML
	private FileConfiguration PlayerLogging = null;
	private File PlayerLoggingFile = null;
	
	public void reloadPlayerLogging() {
	    if (PlayerLoggingFile == null) {
	    	PlayerLoggingFile = new File(getDataFolder(), "PlayerClasses.yml");
	    }
	    PlayerLogging = YamlConfiguration.loadConfiguration(PlayerLoggingFile);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = this.getResource("PlayerClasses.yml");
	    if (defConfigStream != null) {
	        @SuppressWarnings("deprecation")
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        PlayerLogging.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getPlayerLogging() {
	    if (PlayerLogging == null) {
	        reloadPlayerLogging();
	    }
	    return PlayerLogging;
	}
	
	public void savePlayerLogging() {
	    if (PlayerLogging == null || PlayerLoggingFile == null) {
	        return;
	    }
	    try {
	        getPlayerLogging().save(PlayerLoggingFile);
	    } catch (IOException ex) {
	        getLogger().log(Level.SEVERE, "Could not save config to " + PlayerLoggingFile, ex);
	    }
	}
	
	public void saveDefaultPlayerLogging() {
	    if (PlayerLoggingFile == null) {
	    	PlayerLoggingFile = new File(getDataFolder(), "PlayerClasses.yml");
	    }
	    if (!PlayerLoggingFile.exists()) {            
	         plugin.saveResource("PlayerClasses.yml", false);
	     }
	}
	
// 	PlayerFactions.YML
	private FileConfiguration PlayerFactions = null;
	private File PlayerFactionsFile = null;
	
	public void reloadPlayerFactions() {
	    if (PlayerFactionsFile == null) {
	    	PlayerFactionsFile = new File(getDataFolder(), "PlayerFactions.yml");
	    }
	    PlayerFactions = YamlConfiguration.loadConfiguration(PlayerFactionsFile);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = this.getResource("PlayerFactions.yml");
	    if (defConfigStream != null) {
	        @SuppressWarnings("deprecation")
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        PlayerFactions.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getPlayerFactions() {
	    if (PlayerFactions == null) {
	        reloadPlayerFactions();
	    }
	    return PlayerFactions;
	}
	
	public void savePlayerFactions() {
	    if (PlayerFactions == null || PlayerFactionsFile == null) {
	        return;
	    }
	    try {
	        getPlayerFactions().save(PlayerFactionsFile);
	    } catch (IOException ex) {
	        getLogger().log(Level.SEVERE, "Could not save config to " + PlayerFactionsFile, ex);
	    }
	}
	
	public void saveDefaultPlayerFactions() {
	    if (PlayerFactionsFile == null) {
	    	PlayerFactionsFile = new File(getDataFolder(), "PlayerFactions.yml");
	    }
	    if (!PlayerFactionsFile.exists()) {
	         plugin.saveResource("PlayerFactions.yml", false);
	     }
	}
	
// 	PlayerCurrency.YML
	private FileConfiguration PlayerCurrency = null;
	private File PlayerCurrencyFile = null;
	
	public void reloadPlayerCurrency() {
	    if (PlayerCurrencyFile == null) {
	    	PlayerCurrencyFile = new File(getDataFolder(), "PlayerCurrency.yml");
	    }
	    PlayerCurrency = YamlConfiguration.loadConfiguration(PlayerCurrencyFile);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = this.getResource("PlayerCurrency.yml");
	    if (defConfigStream != null) {
	        @SuppressWarnings("deprecation")
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        PlayerCurrency.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getPlayerCurrency() {
	    if (PlayerCurrency == null) {
	        reloadPlayerCurrency();
	    }
	    return PlayerCurrency;
	}
	
	public void savePlayerCurrency() {
	    if (PlayerCurrency == null || PlayerCurrencyFile == null) {
	        return;
	    }
	    try {
	        getPlayerCurrency().save(PlayerCurrencyFile);
	    } catch (IOException ex) {
	        getLogger().log(Level.SEVERE, "Could not save config to " + PlayerCurrencyFile, ex);
	    }
	}
	
	public void saveDefaultPlayerCurrency() {
	    if (PlayerCurrencyFile == null) {
	    	PlayerCurrencyFile = new File(getDataFolder(), "PlayerCurrency.yml");
	    }
	    if (!PlayerCurrencyFile.exists()) {
	         plugin.saveResource("PlayerCurrency.yml", false);
	     }
	}
	
// 	PlayerAchievements.YML
	private FileConfiguration PlayerAchievements = null;
	private File PlayerAchievementsFile = null;
	
	public void reloadPlayerAchievements() {
	    if (PlayerAchievementsFile == null) {
	    	PlayerAchievementsFile = new File(getDataFolder(), "PlayerAchievements.yml");
	    }
	    PlayerAchievements = YamlConfiguration.loadConfiguration(PlayerAchievementsFile);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = this.getResource("PlayerAchievements.yml");
	    if (defConfigStream != null) {
	        @SuppressWarnings("deprecation")
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        PlayerAchievements.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getPlayerAchievements() {
	    if (PlayerAchievements == null) {
	        reloadPlayerAchievements();
	    }
	    return PlayerAchievements;
	}
	
	public void savePlayerAchievements() {
	    if (PlayerAchievements == null || PlayerAchievementsFile == null) {
	        return;
	    }
	    try {
	        getPlayerAchievements().save(PlayerAchievementsFile);
	    } catch (IOException ex) {
	        getLogger().log(Level.SEVERE, "Could not save config to " + PlayerAchievementsFile, ex);
	    }
	}
	
	public void saveDefaultPlayerAchievements() {
	    if (PlayerAchievementsFile == null) {
	    	PlayerAchievementsFile = new File(getDataFolder(), "PlayerAchievements.yml");
	    }
	    if (!PlayerAchievementsFile.exists()) {
	         plugin.saveResource("PlayerAchievements.yml", false);
	     }
	}
	
}