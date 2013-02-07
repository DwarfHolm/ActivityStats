package com.dwarfholm.activitystats.braizhauler;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ASCmdActivity implements CommandExecutor {
	ActivityStats plugin;
	
	public ASCmdActivity (ActivityStats plugin)	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		plugin.info("Attempting to run " + label + " for " + sender.getName()); 
		if (args.length == 0)	{
			if (plugin.perms().has(sender, "activitystats.view.self"))	{
				if (sender instanceof Player)	{
					return reportActivity(sender, (Player)sender);
				}	else	{
					plugin.msg(sender, "Must have Player argument");
				}
			} else {  // no - view self permission
				plugin.msg(sender, plugin.getLocalization().getLackPermission("activitystats.view.self"));
			}
		} else	{ // 1 or more args
			plugin.msg(sender, "Unimplemented");
		}
		return false;
	}	
	public boolean reportActivity(CommandSender sender, Player target)	{
		String message = plugin.getLocalization().getActivityReportMessage(plugin.getASPlayer(target.getName()));
		plugin.msg(sender, message );
		return true;
	}
}
