package com.dwarfholm.activitystats.braizhauler;

import org.bukkit.configuration.Configuration;

public class ASConfig {
	private ActivityStats plugin;
	private Configuration config;
	
	public String localeFileName;
	
	public int iInterval, iQuota, iBreakMax, iBreakMult, iPlaceMax, iPlaceMult, iTravelMax, iTravelMult, iChatMax, iChatMult,
			iAnimalMax, iAnimalMult, iMonsterMax, iMonsterMult, iPlayerMax, iPlayerMult;
	public boolean useMySQL;
	public String sqlHostname, sqlPort, sqlUsername, sqlPassword, sqlDatabase, sqlPrefix, sqlURI;
	
	public String ecoMode;
	public double ecoMin, ecoMax;
	
	public ASConfig(ActivityStats plugin)	{
		this.plugin = plugin;
	}
	public void onEnable() {
		reloadConfig();
	}

	public void reloadConfig()	{
		plugin.reloadConfig();
		config = plugin.getConfig().getRoot();
		loadConfig();
	}
	
	public void loadConfig()	{
		localeFileName = config.getString("locale.filename");
		
		iInterval = config.getInt("interval");
		iQuota = config.getInt("quota");
		iBreakMax = config.getInt("block-break.max");
		iBreakMult = config.getInt("block-break.multiplier");
		iPlaceMax = config.getInt("block-plack.max");
		iPlaceMult = config.getInt("block-place.multiplier");
		iTravelMax = config.getInt("blocks-traveled.max");
		iTravelMult = config.getInt("blocks-traveled.multiplier");
		iChatMax = config.getInt("chat-commands.max");
		iChatMult = config.getInt("chat-commands.multiplier");
		iAnimalMax = config.getInt("damage-animal.max");
		iAnimalMult = config.getInt("damage-animal.multiplier");
		iMonsterMax = config.getInt("damage-monster.max");
		iMonsterMult = config.getInt("damage-monster.multiplier");
		iPlayerMax = config.getInt("damage-player.max");
		iPlayerMult = config.getInt("damager-player.multiplier");
		
		useMySQL = config.getBoolean("database.use-mysql");

		sqlHostname = config.getString("database.mysql.hostname");
		sqlPort = config.getString("database.mysql.port");
		sqlUsername = config.getString("database.mysql.username");
		sqlPassword = config.getString("database.mysql.password");
		sqlDatabase = config.getString("database.mysql.database");
		sqlPrefix = config.getString("database.mysql.tableprefix");
		sqlURI = "jdbc:mysql://" + sqlHostname + ":" + sqlPort  + "/" + sqlDatabase;
		
		ecoMode = config.getString("economy.mode");
		ecoMin = config.getDouble("economy.min");
		ecoMax = config.getDouble("economy.max");
	}
	
	public boolean ecoModePercent()	{	return ecoMode.equalsIgnoreCase("PERCENT");	}
	public boolean ecoModeBoolean()	{	return ecoMode.equalsIgnoreCase("BOOLEAN");	}
}
