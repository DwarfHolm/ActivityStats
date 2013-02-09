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
		if (playerlist!=null && playerlist.isEmpty())	{
			for (ASPlayer player:playerlist.values())
				if ( plugin.getServer().getPlayer(player.getName()) != null)
					player.curPeriod.addOnline();
		}
	}
	
	public void checkRollovers() {
		if( plugin.PeriodRolloverDue())	{
			for (ASPlayer player:playerlist.values())	{
				plugin.info(player.getName());
				plugin.payPlayer(player);
			}
			rolloverPeriod();
			saveAll();
			if( plugin.DayRolloverDue() || plugin.WeekRolloverDue() || plugin.MonthRolloverDue() )	{
				if ( plugin.DayRolloverDue() )
					database.rolloverDay();
				if ( plugin.WeekRolloverDue() )
					database.rolloverWeek();
				if ( plugin.MonthRolloverDue() )
					database.rolloverMonth();
				loadOnlinePlayers();
				saveAll();
			}
			Player pPlayer;
			for (ASPlayer player:playerlist.values())	{
				pPlayer = plugin.getServer().getPlayer(player.getName());
				if ( pPlayer == null )	{
					plugin.info("removing " + player.getName());
					playerlist.remove(player.getName());
				} else
					plugin.autoPromoterCheck(pPlayer);
			}
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

	public void fetchRemotePlayerlist() {
		database.fetchRemotePlayerList();
	}
}
