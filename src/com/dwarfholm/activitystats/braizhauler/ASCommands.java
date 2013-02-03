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
	}

	
	public void unregisterCommands() {
		plugin.getCommand("activitystats").setExecutor(null);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("activitystats"))	{
			if (args.length == 0)	{
				if (sender instanceof Player)	{
					return reportActivity(sender, (Player)sender);
				}	else	{
					plugin.msg(sender, "Must have Player argument");
				}
			} else	{
				plugin.msg(sender, "Unimplemented");
			}
			return true;
		}
		return false;
	}
	
	public boolean reportActivity(CommandSender sender, Player target)	{
		String message = plugin.getLocalization().getActivityReportMessage(target);
		plugin.msg(sender, message );
		return true;
	}


}
