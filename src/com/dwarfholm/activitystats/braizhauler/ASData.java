package com.dwarfholm.activitystats.braizhauler;

import java.util.HashMap;

public class ASData {
	private HashMap <String, ASPlayer> playerlist;
	public ASPlayer getPlayer(String name)	{
		return playerlist.get(name);
	}
	
	public void rolloverPeriod()	{
		for(String player: playerlist.keySet())	{
			playerlist.get(player).rolloverPeriod();
		}
	}
	public void rolloverDay()	{
		for(String player: playerlist.keySet())	{
			playerlist.get(player).rolloverDay();
		}
	}
	public void rolloverWeek()	{
		for(String player: playerlist.keySet())	{
			playerlist.get(player).rolloverWeek();
		}
	}
	public void rolloverMonth()	{
		for(String player: playerlist.keySet())	{
			playerlist.get(player).rolloverMonth();
		}
	}
}
