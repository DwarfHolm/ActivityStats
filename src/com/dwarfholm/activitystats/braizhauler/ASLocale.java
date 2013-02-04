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
		msgPayment = locale.getString("message.payment").replaceAll("%","%%").replaceAll("\\?", "%s");
		msgActivityReport = locale.getString("message.activity-report").replaceAll("%","%%").replaceFirst("\\?", "%.2f").replaceFirst("\\?", "%s");
	}

	public String getPaymentMessage(double amount, double percent) {
		percent = (float)(100 * percent);
		return String.format(msgPayment, plugin.econ().format(amount),percent);
	}

	public String getActivityReportMessage(Player target) {
		ASPlayer tardata = plugin.getASPlayer(target.getName());
		float percent = (float)(100 * plugin.getActivityPercent(tardata));
		return String.format(msgActivityReport, percent);
	}
}
