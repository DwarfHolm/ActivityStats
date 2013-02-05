package com.dwarfholm.activitystats.braizhauler;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ASCmdNextPayDay implements CommandExecutor {
	ActivityStats plugin;
	
	public ASCmdNextPayDay (ActivityStats plugin)	{
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ( plugin.perms().has(sender, "activitystats.view.self") )
				return reportPayDay(sender);
		return false;
	}
	
	public boolean reportPayDay(CommandSender sender)	{
		plugin.msg(sender, plugin.getLocalization().getPayTimeMessage());
		return true;
	}

}
