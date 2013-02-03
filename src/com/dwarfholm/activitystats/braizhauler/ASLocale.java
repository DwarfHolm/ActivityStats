package com.dwarfholm.activitystats.braizhauler;

import java.io.File;
import java.util.Locale;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class ASLocale {
	private ActivityStats plugin;
	private YamlConfiguration locale;
	
	public ASLocale(ActivityStats plugin){
		this.plugin = plugin;
	}

	public ASLocale load()	{
		return load(new Locale("en", "US"));
	}
	
	public ASLocale load(Locale language)	{
		locale = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), language.getLanguage() + language.getCountry() + ".yml"));
		Configuration defaultLocale = YamlConfiguration.loadConfiguration(this.getClass().getResourceAsStream("/enUS.yml"));
		locale.setDefaults(defaultLocale);
		plugin.saveResource(language.getLanguage() + language.getCountry(), true);
		return this;
	}

	public void unload() {
		plugin = null;
		locale = null;
	}

	public String getPaymentMessage(double amount) {
		return String.format(locale.getString("message.payment"), plugin.econ().format(amount));
	}

	public String getActivityReportMessage(Player target) {
		ASPlayer tardata = plugin.getASPlayer(target.getName());
		return String.format(locale.getString("message.activity-report"), plugin.getActivityPercent(tardata) );
	}
}
