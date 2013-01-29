package com.dwarfholm.activitystats.braizhauler;


import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ActivityStats extends JavaPlugin {
	private YamlConfiguration config;
	private Vault vault;
	private ASLocale locale;
	private Logger log;
	private final String CONFIG_FILE_NAME = "config.yml";
	private final String LOG_NAME = "ActivityStats";
	private final String CONSOLE = "console";
	
	public ActivityStats()	{
		log = Logger.getLogger(LOG_NAME);
	}
	
	public void onEnable() {
		config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), CONFIG_FILE_NAME));
		Configuration defaultConfig = YamlConfiguration.loadConfiguration(getClass().getResourceAsStream("/" + CONFIG_FILE_NAME));
		config.setDefaults(defaultConfig);
		
		locale = new ASLocale(this);
		locale.load(Locale.US);
		
		vault.connect();
	}
	
	public void onDisable() {
		vault.disconnect();
		locale = null;
		config = null;
	}
	
	public void saveConfig()	{
		try {
			config.save(CONFIG_FILE_NAME);
		} catch (IOException e) {
			severe(e.getMessage());
		}
	}
	
	public YamlConfiguration config()	{	return config;	}
	public Economy econ()	{	return vault.econ;	}
	public Permission perms()	{	return vault.perms;	}
	
	
	public void msg(String sSender, String msg)	{
		CommandSender csSender;
		if (sSender.equalsIgnoreCase(CONSOLE))	{
			csSender = (CommandSender)getServer().getConsoleSender();
		} else	{
			csSender = getServer().getPlayer(sSender);
		}
		msg (csSender, msg);
	}
	
	public void msg(CommandSender sender, String msg)	{	sender.sendMessage(msg);	}
	
	public void severe(String msg)	{	log.severe(msg);	}
	public void warning(String msg)	{	log.warning(msg);	}
	public void info(String msg)	{	log.info(msg);		}
	public void fine(String msg)	{	log.fine(msg);		}
	public void finer(String msg)	{	log.finer(msg);		}
	public void finest(String msg)	{	log.finest(msg);	}
	
}