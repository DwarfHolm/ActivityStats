package com.dwarfholm.activitystats.braizhauler;

import org.bukkit.entity.Player;

import org.bukkit.scheduler.BukkitTask;

public class ASTravelTimer implements Runnable{
	private ActivityStats plugin;
	
	private long intervalInTicks;
	private BukkitTask task;
	
	public ASTravelTimer(ActivityStats parent)	{
		plugin = parent;
		intervalInTicks = 1200; //ticks per secound * secounds per minute
	}
	
	public void start()	{
		task = plugin.getServer().getScheduler().runTaskTimer(plugin, this, intervalInTicks, intervalInTicks);
	}
	
	public void stop()	{
		task.cancel();
	}
	
	public void run() {
		for (Player player: plugin.getServer().getOnlinePlayers() )
			plugin.getServer().getPluginManager().callEvent(new CalculateTravelEvent(player));
	}
}
