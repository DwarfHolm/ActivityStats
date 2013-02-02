package com.dwarfholm.activitystats.braizhauler;

import java.util.HashMap;


public class ASData {
	private HashMap <String, ASPlayer> playerlist;
	private ASDatabase database;
	private ActivityStats plugin;
	
	public ASData(ActivityStats plugin)	{
		this.plugin = plugin;
		database = new ASDatabase(plugin);
	}
	
	public void saveAll()	{
		for (ASPlayer player:playerlist.values())
			database.updatePlayer(player);
	}
	
	public void payAll() {
		plugin.info("Paying all Players");
		for (ASPlayer player:playerlist.values())	{
			plugin.info(player.getName());
			plugin.payPlayer(player);
		}
		if( plugin.DayRolloverDue())
			rolloverDay();
		if( plugin.WeekRolloverDue())
			rolloverWeek();
		if( plugin.MonthRolloverDue())
			rolloverMonth();
		rolloverPeriod();
		saveAll();
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
		plugin.rolledoverSave();
	}
	public void rolloverDay()	{
		for(String player: playerlist.keySet())
			playerlist.get(player).rolloverDay();
		plugin.rolledoverDay();
	}
	public void rolloverWeek()	{
		for(String player: playerlist.keySet())
			playerlist.get(player).rolloverWeek();
		plugin.rolledoverWeek();
	}
	public void rolloverMonth()	{
		for(String player: playerlist.keySet())
			playerlist.get(player).rolloverMonth();
		plugin.rolledoverMonth();
	}



}
