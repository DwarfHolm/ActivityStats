package com.dwarfholm.activitystats.braizhauler;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ASCommands implements CommandExecutor{

	private ActivityStats plugin;

	public ASCommands(ActivityStats plugin)	{
		this.plugin = plugin;
	}
	
	public void registerCommands() {
		plugin.getCommand("activitystats").setExecutor(this);	
	}

	
	public void unregisterCommands() {
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
		if (command.getName().equalsIgnoreCase("activitystats"))	{
			plugin.msg(sender, "Unimplemented");
			return true;
		}
		return false;
	}


}
