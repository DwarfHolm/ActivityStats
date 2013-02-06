package com.dwarfholm.activitystats.braizhauler;

import java.util.HashMap;

import org.bukkit.entity.Player;


public class ASData {
	private HashMap <String, ASPlayer> playerlist;
	private ASDatabase database;
	private ActivityStats plugin;
	
	public ASData(ActivityStats plugin)	{
		this.plugin = plugin;
		playerlist = new HashMap<String, ASPlayer>();
		database = new ASDatabase(plugin);
	}
	

	
	public void createDatabase() {
		database.createTables();
	}
	
	public void recordOnline() {
		plugin.info("recordOnline fired");//DEBUG
		if (playerlist!=null && playerlist.isEmpty())	{
			plugin.info("recordOnline has playerList");//DEBUG
			for (ASPlayer player:playerlist.values())
				if ( plugin.getServer().getPlayer(player.getName()) != null)
					player.curPeriod.addOnline();
			plugin.info("recordOnline looking for rollovers"); //DEBUG
			if( plugin.PeriodRolloverDue())	{
				plugin.info("Paying all Players");
				for (ASPlayer player:playerlist.values())	{
					plugin.info(player.getName());
					plugin.payPlayer(player);
				}
				rolloverPeriod();
				
				if( plugin.DayRolloverDue() || plugin.WeekRolloverDue() || plugin.MonthRolloverDue() )	{
					saveAll();
					if ( plugin.DayRolloverDue() )
						database.rolloverDay();
					if ( plugin.WeekRolloverDue() )
						database.rolloverWeek();
					if ( plugin.MonthRolloverDue() )
						database.rolloverMonth();
					loadOnlinePlayers();
				}
				Player pPlayer;
				for (ASPlayer player:playerlist.values())	{
					pPlayer = plugin.getServer().getPlayer(player.getName());
					if ( pPlayer == null )
						playerlist.remove(player.getName());
					else
						plugin.autoPromoterCheck(pPlayer);
				}
						
				saveAll();
			} else {
				plugin.info("recordOnline not rolling over");//DEBUG
			}
		} else	{
			plugin.info("recordOnline failed due to empty playerlist");//debug
		}
	}
	

	
	public void loadPlayer(String player)	{
		database.loadPlayer(player);
	}
	

	public void savePlayer(String name) {
		savePlayer(playerlist.get(name));
	}
	
	public void savePlayer(ASPlayer player) {
		database.updatePlayer(player);
	}
	
	
	public void setPlayer(ASPlayer player)	{
		playerlist.put(player.getName(), player);
	}
	

	
	public ASPlayer getPlayer(String name)	{
		return playerlist.get(name);
	}
	
	public void rolloverPeriod()	{
		for(String player: playerlist.keySet())	{
			playerlist.get(player).rolloverPeriod();
		}
		plugin.rolledoverPeriod();
	}

	public void loadOnlinePlayers() {
		for (Player player: plugin.getServer().getOnlinePlayers() )
			loadPlayer(player.getName());
	}
	public void saveAll()	{
		for (ASPlayer player:playerlist.values())
			database.updatePlayer(player);
	}
}
