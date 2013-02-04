package com.dwarfholm.activitystats.braizhauler;


import org.bukkit.scheduler.BukkitScheduler;

public class ASDatabase {
	private ActivityStats plugin;
	private BukkitScheduler scheduler;
	private ASMySql mySQL;
	
	public ASDatabase (ActivityStats plugin)	{
		this.plugin = plugin;
		this.scheduler = plugin.getServer().getScheduler();
		
		mySQL = new ASMySql(plugin);
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
}