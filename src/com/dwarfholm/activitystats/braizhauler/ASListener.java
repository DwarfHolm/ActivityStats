package com.dwarfholm.activitystats.braizhauler;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.dthielke.herochat.ChannelChatEvent;

public class ASListener implements Listener {
	ActivityStats plugin;
	
	
	
	public ASListener(ActivityStats parent)	{
		plugin = parent;
	}
	
	public void register()	{
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public void unregister()	{
		HandlerList.unregisterAll(plugin);
	}
	
	 @EventHandler (priority =  EventPriority.MONITOR, ignoreCancelled = true)
    public void createPlayer(PlayerJoinEvent event) {
    	plugin.loadPlayer(event.getPlayer().getName());
    }
    
    @EventHandler (priority =  EventPriority.MONITOR, ignoreCancelled = true)
    public void playerTeleport(PlayerTeleportEvent event) {
    	Location to = event.getTo();
    	Location from = event.getFrom();
    	String name = event.getPlayer().getName();
    	try	{
    		plugin.getASPlayer(name).curPeriod.calculateTravel(from, to);
	    } catch (java.lang.IllegalArgumentException e)	{
			plugin.warning("Error while calculationg " + name + "'s travel.");
			plugin.warning(e.getMessage());
		}
    }

    @EventHandler (priority =  EventPriority.MONITOR, ignoreCancelled = true)
    public void playerRespawn(PlayerRespawnEvent event)	{
    	String name = event.getPlayer().getName();
    	Location deathLoc = event.getPlayer().getLocation();
    	Location respawnLoc = event.getRespawnLocation();
    	try	{
    		plugin.getASPlayer(name).calculateTravel(deathLoc, respawnLoc);
	    } catch (java.lang.IllegalArgumentException e)	{
			plugin.warning("Error while calculationg " + name + "'s travel.");
			plugin.warning(e.getMessage());
		}
    	event.getRespawnLocation();
    }
    
    @EventHandler (priority =  EventPriority.MONITOR, ignoreCancelled = true)
    public void blockBreak(BlockBreakEvent event)	{
    	plugin.getASPlayer(event.getPlayer().getName()).curPeriod.brokeBlock();
    }
    
    @EventHandler (priority =  EventPriority.MONITOR, ignoreCancelled = true)
    public void blockPlace(BlockPlaceEvent event)	{
    	plugin.getASPlayer(event.getPlayer().getName()).curPeriod.placeBlock();
    	
    }
    
    @EventHandler (priority =  EventPriority.MONITOR, ignoreCancelled = true)
    public void playerChat(AsyncPlayerChatEvent event)	{
    	plugin.getASPlayer(event.getPlayer().getName()).curPeriod.chat();  	
    }
    
    @EventHandler (priority =  EventPriority.MONITOR, ignoreCancelled = true)
    public void playerChat(PlayerCommandPreprocessEvent event)	{
    	plugin.getASPlayer(event.getPlayer().getName()).curPeriod.chat();  	
    }
    
    
    @EventHandler (priority =  EventPriority.MONITOR, ignoreCancelled = true)
    public void playerChat(ChannelChatEvent event)	{
    	plugin.getASPlayer(event.getSender().getName()).curPeriod.chat();  	
    }
    
    @EventHandler (priority =  EventPriority.MONITOR, ignoreCancelled = true)
    public void damageDealt(EntityDamageByEntityEvent event)	{
    	if(event.getDamager() instanceof Player)	{
    		Player damager = (Player) event.getDamager();
    		if (event.getEntity() instanceof Player)	{
    	    	plugin.getASPlayer(damager.getName()).curPeriod.damagePlayer();  
    		} else if (event.getEntity() instanceof Monster)	{
    	    	plugin.getASPlayer(damager.getName()).curPeriod.damageMonster();  
    		} else if (event.getEntity() instanceof Creature)	{
    	    	plugin.getASPlayer(damager.getName()).curPeriod.damageAnimal();  
    		}
    	}
    }

}
