package com.dwarfholm.activitystats.braizhauler;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ASCmdActivityStats implements CommandExecutor  {
	ActivityStats plugin;
	
	public ASCmdActivityStats(ActivityStats plugin)	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		plugin.info("Attempting to run " + label + " for " + sender.getName()); 
		if (command.getName().equalsIgnoreCase("activitystats"))	{
			if (args.length == 0)	{
				if (plugin.perms().has(sender, "activitystats.view.self"))	{
					if (sender instanceof Player)	{
						return reportActivityLong(sender, (Player)sender);
					}	else	{
						plugin.msg(sender, "Must have Player argument");
					}
				} else {
					plugin.msg(sender, plugin.getLocalization().getLackPermission("activitystats.view.self"));
				}
			} else	{
				plugin.msg(sender, "Unimplemented");
			}
			return true;
		}
		return false;
	}
	
	public boolean reportActivityLong(CommandSender sender, Player target)	{
		String[] messages = plugin.getLocalization().getLongActivityReportMessage(plugin.getASPlayer(target.getName()));
		for(String message:messages)
			plugin.msg(sender, message );
		return true;
	}
}
