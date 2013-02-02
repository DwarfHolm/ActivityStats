package com.dwarfholm.activitystats.braizhauler;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;

public class CalculateTravelEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	
    public CalculateTravelEvent(Player player)	{	this.player = player;	}
    public Player getPlayer() {	return player;}
    public HandlerList getHandlers()	{	return handlers;	}
    public static HandlerList getHandlerList()	{	return handlers;	}
}
