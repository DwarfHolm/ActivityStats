package com.dwarfholm.activitystats.braizhauler;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class ASPaymentTimer implements Runnable{
	ActivityStats plugin;
	private long intervalInTicks;
	private BukkitTask task;
	
	public ASPaymentTimer(ActivityStats parent)	{
		plugin = parent;
		intervalInTicks = 20 * 60; //Ticks per Second * Seconds Per minute
	}
	
	public void start()	{
		task = plugin.getServer().getScheduler().runTaskTimer(plugin, this, intervalInTicks, intervalInTicks);
	}
	
	public void stop()	{
		task.cancel();
	}
	
	public void run() {
		for (Player player: plugin.getServer().getOnlinePlayers() )	{
	    	try {
	    		plugin.getASPlayer(player.getName()).calculateTravel(player.getLocation());
	    	} catch (java.lang.IllegalArgumentException e)	{
	    		plugin.warning("Error while calculationg " +player.getName() + "'s travel.");
	    		plugin.warning(e.getMessage());
	    	}
		}
		plugin.getASPlayersList().recordOnline();
		plugin.getASPlayersList().checkRollovers();
	}
}
