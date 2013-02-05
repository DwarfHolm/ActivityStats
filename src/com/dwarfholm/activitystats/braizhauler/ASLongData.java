package com.dwarfholm.activitystats.braizhauler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

public class ASLongData extends ASShortData {
	private static ASConfig config = null;
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
	public static void setConfig(ASConfig configuration){
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
			blockBreak = Math.min(blockBreak + config.iBreakMult, config.iBreakMax);
			calculateActivity();
		}
	}	

	public void placeBlock()	{
		if(configSet())	{
			blockPlace = Math.min(blockPlace + config.iPlaceMult, config.iPlaceMax);;
			calculateActivity();
		}
	}	
	public void travel(int add)	{
		if(configSet())	{
			traveled = Math.min(traveled + add, config.iTravelMax);
			calculateActivity();
		}
	}
	public void chat()	{
		if(configSet())	{
			chat = Math.min(chat + config.iChatMult, config.iChatMax);
			calculateActivity();
		}
	}	
	public void damageAnimal()	{
		if(configSet())	{
			damAnimal = Math.min(damAnimal + config.iAnimalMult, config.iAnimalMax);
			calculateActivity();
		}
	}	
	public void damageMonster()	{
		if(configSet())	{
			damMonster = Math.min(damMonster + config.iMonsterMult, config.iMonsterMax);
			calculateActivity();
		}
	}
	public void damagePlayer()	{
		if(configSet())	{
			damPlayer = Math.min(damPlayer + config.iPlayerMult, config.iPlayerMax);
			calculateActivity();
		}
	}
	public void addOnline()	{
		online += 1;
	}	
	private void calculateActivity()	{
		activity = Math.max(config.iQuota, blockBreak + blockPlace + traveled + chat + damAnimal + damMonster + damPlayer);
	}

	public void calculateTravel(Location newLoc) {
		calculateTravel(newLoc, newLoc);
	}
	public void calculateTravel(Location from, Location to) {
		double dist = 0;
		if (curLoc!=null) 
			dist = curLoc.distance(from);
		travel((int)(dist / 10 * config.iTravelMult));
		curLoc = to;
	}
	
	public int getActivity()	{
		return activity;
	}
	public int getActivity(ActivityType type)	{
		switch(type)	{
			case BREAK:
				return blockBreak;
			case PLACE:
				return blockPlace;
			case TRAVEL:
				return traveled;
			case CHAT:
				return chat;
			case ANIMAL:
				return damAnimal;
			case MONSTER:
				return damMonster;
			case PLAYER:
				return damPlayer;
		}
		return activity;
	}
	
	public enum	ActivityType{
		BREAK, PLACE, TRAVEL, CHAT, ANIMAL, MONSTER, PLAYER
	}
}
