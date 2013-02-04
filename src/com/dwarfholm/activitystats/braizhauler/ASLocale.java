package com.dwarfholm.activitystats.braizhauler;

import java.io.File;
import java.io.InputStream;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class ASLocale {
	private ActivityStats plugin;
	private YamlConfiguration locale;
	private File localeFile = null;

	
	String msgPayment, msgActivityReport;
	
	public ASLocale(ActivityStats plugin){
		this.plugin = plugin;
	}

	public void onEnable() {
		reloadLocale();
	}
	
	public void reloadLocale() 	{
		 if (localeFile == null) {
			 localeFile = new File(plugin.getDataFolder(), plugin.config().localeFileName);
		 }
	    locale = YamlConfiguration.loadConfiguration(localeFile);
	 
	    // Look for defaults in the jar
	    InputStream defaultConfigStream = plugin.getResource("locale.yml");
	    if (defaultConfigStream != null) {
	        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defaultConfigStream);
	        locale.setDefaults(defaultConfig);
	    }
	    loadLocale();
	}
	


	private void loadLocale() {
		msgPayment = locale.getString("message.payment");
		msgActivityReport = locale.getString("message.activity-report");
	}

	public String getPaymentMessage(double amount) {
		return String.format(msgPayment, plugin.econ().format(amount));
	}

	public String getActivityReportMessage(Player target) {
		ASPlayer tardata = plugin.getASPlayer(target.getName());
		return String.format(msgActivityReport, plugin.getActivityPercent(tardata) );
	}
}
