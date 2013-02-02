package com.dwarfholm.activitystats.braizhauler;

import org.bukkit.scheduler.BukkitTask;

public class ASPaymentTimer implements Runnable{
	ActivityStats plugin;
	private long intervalInTicks;
	private BukkitTask task;
	
	public ASPaymentTimer(ActivityStats parent)	{
		intervalInTicks = 20 * 60 * plugin.config().getInt("interval"); //Ticks per Second * Seconds Per minute * Minutes 
		plugin = parent;
	}
	
	public void start()	{
		task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this, intervalInTicks, intervalInTicks);
	}
	
	public void stop()	{
		task.cancel();
	}
	
	public void run() {
		plugin.getASPlayersList().payAll();
	}
}
