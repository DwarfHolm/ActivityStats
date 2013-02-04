package com.dwarfholm.activitystats.braizhauler;

import org.bukkit.scheduler.BukkitTask;

public class ASPaymentTimer implements Runnable{
	ActivityStats plugin;
	private long intervalInTicks;
	private BukkitTask task;
	
	public ASPaymentTimer(ActivityStats parent)	{
		plugin = parent;
		intervalInTicks = 20 * 60 * plugin.config().iInterval; //Ticks per Second * Seconds Per minute * Minutes 
	}
	
	public void start()	{
		task = plugin.getServer().getScheduler().runTaskTimer(plugin, this, intervalInTicks, intervalInTicks);
	}
	
	public void stop()	{
		task.cancel();
	}
	
	public void run() {
		plugin.getASPlayersList().recordOnline();
	}
}
