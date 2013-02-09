package com.dwarfholm.activitystats.braizhauler;


import org.bukkit.scheduler.BukkitScheduler;

import com.dwarfholm.activitystats.braizhauler.ASMySQL.TableType;

public class ASDatabase {
	private ActivityStats plugin;
	private BukkitScheduler scheduler;
	private ASMySQL mySQL;
	private ASRemoteMySQL remoteMySQL;
	
	public ASDatabase (ActivityStats plugin)	{
		this.plugin = plugin;
		this.scheduler = plugin.getServer().getScheduler();
		
		mySQL = new ASMySQL(plugin);
		remoteMySQL = new ASRemoteMySQL(plugin);
	}
	
	public void createTables()	{
		if (plugin.config().useMySQL)	{	
			scheduler.runTaskAsynchronously(plugin, new Runnable() {
				public void run() {
					mySQL.createTables();
				}
			});
		}
	}
	
	public void loadPlayer(final String player)	{
		if (plugin.config().useMySQL)	{	
			if(!mySQL.playerExists(player))	{
				mySQL.createPlayer(player);
			}
			mySQL.loadPlayer(player);
		}
	}
	
	public void updatePlayer(final ASPlayer player) 	{
		if (plugin.config().useMySQL)	{	
			scheduler.runTaskAsynchronously(plugin, new Runnable() {
				public void run() {
					mySQL.updatePlayer(player);
				}
			});
		}
	}
	
	public void rolloverDay()	{
		if (plugin.config().useMySQL)	{	
			scheduler.runTaskAsynchronously(plugin, new Runnable() {
				public void run() {
					mySQL.rollover(TableType.DAY);
				}
			});
		}
	}
	public void rolloverWeek()	{
		if (plugin.config().useMySQL)	{	
			scheduler.runTaskAsynchronously(plugin, new Runnable() {
				public void run() {
					mySQL.rollover(TableType.WEEK);
				}
			});
		}
	}
	public void rolloverMonth()	{
		if (plugin.config().useMySQL)	{	
			scheduler.runTaskAsynchronously(plugin, new Runnable() {
				public void run() {
					mySQL.rollover(TableType.MONTH);
				}
			});
		}
	}
	
	public void fetchRemotePlayerList()	{
		String[] playerList = remoteMySQL.retrieveSQL();
		for (String player:playerList)
			if (!mySQL.autoPromoterPlayerExists(player))
				mySQL.addAutoPromoteValue(player, false);
	}
	
	public String[] autoPromoterUnhandledList()	{
		return mySQL.autoPromoteList();
	}
	
	public void autoPromoterHandled (String player)	{
		mySQL.updateAutoPromoteValue(player, true);
	}
}