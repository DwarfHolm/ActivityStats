package com.dwarfholm.activitystats.braizhauler;

import org.bukkit.entity.Player;

import org.bukkit.scheduler.BukkitTask;

public class ASTravelTimer implements Runnable{
	private ActivityStats plugin;
	
	private long intervalInTicks;
	private BukkitTask task;
	
	public ASTravelTimer(ActivityStats parent)	{
		intervalInTicks = 1200; //ticks per secound * secounds per minute
		plugin = parent;
	}
	
	public void start()	{
		task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this, intervalInTicks, intervalInTicks);
	}
	
	public void stop()	{
		task.cancel();
	}

	
	public void run() {
		for (Player player: plugin.getServer().getOnlinePlayers() )
			plugin.getServer().getPluginManager().callEvent(new CalculateTravelEvent(player));
	}
}