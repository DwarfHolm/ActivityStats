package com.dwarfholm.activitystats.braizhauler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ASLongData extends ASShortData {
	private static YamlConfiguration config = null;
	private int blockBreak;
	private int blockPlace;
	private int traveled;
	private int chat;
	private int damAnimal;
	private int damMonster;
	private int damPlayer;
	private Location curLoc;

	public ASLongData()	{
		clear();
		curLoc = null; 
	}
	public void clear()	{
		blockBreak = 0;
		blockPlace = 0;
		traveled = 0;
		damAnimal = 0;
		damMonster = 0;
		damPlayer = 0;
		super.clear();
	}
	public static void setConfig(YamlConfiguration configuration){
		config = configuration;
	}
	public boolean configSet()	{
		if (config == null)	{
			Plugin ActStat = Bukkit.getPluginManager().getPlugin("ActivityStats");
			if( ActStat instanceof ActivityStats)	{
				((ActivityStats) ActStat).severe("Player Data Config Not Set");
			}
			return false;
		}
		return true;
	}
	public void setEqual(ASLongData value)	{
		blockBreak = value.blockBreak;
		blockPlace = value.blockPlace;
		traveled = value.traveled;
		chat = value.chat;
		damAnimal = value.damAnimal;
		damMonster = value.damMonster;
		damPlayer = value.damPlayer;
		super.setEqual(value);
	}	
	public void brokeBlock()	{
		if(configSet())	{
			blockBreak = Math.min(blockBreak + config.getInt("block-break.multiplier"), config.getInt("block-break.max"));
			calculateActivity();
		}
	}	

	public void placeBlock()	{
		if(configSet())	{
			blockPlace = Math.min(blockPlace + config.getInt("block-place.multiplier"), config.getInt("block-place.max"));;
			calculateActivity();
		}
	}	
	public void travel(int add)	{
		if(configSet())	{
			traveled = Math.min(traveled + add, config.getInt("blocks-traveled.max"));
			calculateActivity();
		}
	}
	public void chat()	{
		if(configSet())	{
			chat = Math.min(chat + config.getInt("chat-commands.multiplier"), config.getInt("chat-commands.max"));
			calculateActivity();
		}
	}	
	public void damageAnimal()	{
		if(configSet())	{
			damAnimal = Math.min(damAnimal + config.getInt("damage-animal.multiplier"), config.getInt("damage-animal.max"));
			calculateActivity();
		}
	}	
	public void damageMonster()	{
		if(configSet())	{
			damMonster = Math.min(damMonster + config.getInt("damage-monster.multiplier"), config.getInt("damage-monster.max"));
			calculateActivity();
		}
	}
	public void damagePlayer()	{
		if(configSet())	{
			damPlayer = Math.min(damPlayer + config.getInt("damage-player.multiplier"), config.getInt("damage-player.max"));
			calculateActivity();
		}
	}
	public void addOnline()	{
		online += 1;
	}	
	private void calculateActivity()	{
		activity = blockBreak + blockPlace + traveled + damAnimal + damMonster + damPlayer;
	}

	public void calculateTravel(Location newLoc) {
		calculateTravel(newLoc, newLoc);
	}
	public void calculateTravel(Location from, Location to) {
		double dist = curLoc.distance(from);
		travel((int)(dist * config.getInt("blocks-traveled.multiplier")));
		curLoc = to;
	}
}
