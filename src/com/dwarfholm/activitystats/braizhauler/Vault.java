package com.dwarfholm.activitystats.braizhauler;

import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Vault {
	private ActivityStats plugin;
	
	public Economy econ = null;
	public Permission perms = null;
	
	public Vault (ActivityStats plugin)	{
		this.plugin = plugin;
	}

    public void connect() {
        if (!setupEconomy() ) {
            plugin.severe("[%s] - Disabled due to no Vault dependency found!");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }
        setupPermissions();
    }
    
    public void disconnect() {
    	econ = null;
    	perms = null;
    }

    private boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider == null) {
            return false;
        }
        econ = economyProvider.getProvider();
        return econ != null;
    }
    
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
}

