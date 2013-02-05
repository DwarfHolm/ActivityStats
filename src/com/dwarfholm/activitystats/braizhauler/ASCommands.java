package com.dwarfholm.activitystats.braizhauler;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ASCommands implements CommandExecutor{

	private ActivityStats plugin;

	public ASCommands(ActivityStats plugin)	{
		this.plugin = plugin;
	}
	
	public void registerCommands() {
		plugin.getCommand("activitystats").setExecutor(this);
		plugin.getCommand("activity").setExecutor(this);	
	}

	
	public void unregisterCommands() {
		plugin.getCommand("activitystats").setExecutor(null);
		plugin.getCommand("activity").setExecutor(null);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
		if (command.getName().equalsIgnoreCase("activity"))	{
			if (args.length == 0)	{
				if (plugin.perms().has(sender, "activitystats.view.self"))	{
					if (sender instanceof Player)	{
						return reportActivity(sender, (Player)sender);
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
	
	public boolean reportActivity(CommandSender sender, Player target)	{
		String message = plugin.getLocalization().getActivityReportMessage(plugin.getASPlayer(target.getName()));
		plugin.msg(sender, message );
		return true;
	}
	
	public boolean reportActivityLong(CommandSender sender, Player target)	{
		String[] messages = plugin.getLocalization().getLongActivityReportMessage(plugin.getASPlayer(target.getName()));
		for(String message:messages)
			plugin.msg(sender, message );
		return true;
	}


}
