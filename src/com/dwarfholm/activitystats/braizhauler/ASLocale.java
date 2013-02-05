package com.dwarfholm.activitystats.braizhauler;

import java.io.File;
import java.io.InputStream;

import org.bukkit.configuration.file.YamlConfiguration;

import com.dwarfholm.activitystats.braizhauler.ASLongData.ActivityType;

public class ASLocale {
	private ActivityStats plugin;
	private YamlConfiguration locale;
	private File localeFile = null;

	private String termBreak, termPlace, termTravel, termChat, termAnimal, termMonster, termPlayer, termActivity;
	
	private String msgPayment, msgActivityReport, msgActivityDetail, msgTimeToPay;
	private String errLackPermission;
	
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
		termBreak = locale.getString("term.break");
		termPlace = locale.getString("term.place");
		termTravel = locale.getString("term.travel");
		termChat = locale.getString("term.chat");
		termAnimal = locale.getString("term.animal");
		termMonster = locale.getString("term.monster");
		termPlayer = locale.getString("term.player");
		termActivity = locale.getString("term.activity");
		
		msgPayment = locale.getString("message.payment").replaceAll("%","%%").replaceAll("\\?", "%s");
		msgActivityReport = locale.getString("message.activity-report").replaceAll("%","%%").replaceFirst("\\?", "%.2f").replaceFirst("\\?", "%s");
		msgActivityDetail = locale.getString("activity-detail").replaceAll("%","%%").replaceFirst("\\?", "%.d").replaceFirst("\\?", "%s");
		msgTimeToPay = locale.getString("message.time-til-payment").replaceAll("%","%%").replaceFirst("\\?", "%d");
		errLackPermission = locale.getString("errors.lack-permission").replaceAll("%","%%").replaceFirst("\\?", "%s");
		
	}

	public String getPaymentMessage(double amount, double percent) {
		percent = (float)(100 * percent);
		return String.format(msgPayment, plugin.econ().format(amount),percent);
	}

	public String getActivityReportMessage(ASPlayer target) {
		float percent = (float)(100 * plugin.getActivityPercent(target));
		return String.format(msgActivityReport, percent);
	}
	
	public String[] getLongActivityReportMessage(ASPlayer target) {
		String[] report = new String[8];
		report[0] = String.format(msgActivityDetail, target.curPeriod.getActivity(ActivityType.BREAK), getTerm(ActivityType.BREAK));
		report[1] = String.format(msgActivityDetail, target.curPeriod.getActivity(ActivityType.PLACE), getTerm(ActivityType.PLACE));
		report[2] = String.format(msgActivityDetail, target.curPeriod.getActivity(ActivityType.TRAVEL), getTerm(ActivityType.TRAVEL));
		report[3] = String.format(msgActivityDetail, target.curPeriod.getActivity(ActivityType.CHAT), getTerm(ActivityType.CHAT));
		report[4] = String.format(msgActivityDetail, target.curPeriod.getActivity(ActivityType.ANIMAL), getTerm(ActivityType.ANIMAL));
		report[5] = String.format(msgActivityDetail, target.curPeriod.getActivity(ActivityType.MONSTER), getTerm(ActivityType.MONSTER));
		report[6] = String.format(msgActivityDetail, target.curPeriod.getActivity(ActivityType.PLAYER), getTerm(ActivityType.PLAYER));
		report[7] = getActivityReportMessage(target);
		return report;
	}
	
	public String getPayTimeMessage() {
		return String.format(msgTimeToPay, plugin.timeToPay());
	}

	public String getLackPermission(String permission) {
		return String.format(errLackPermission, permission);
	}
	
	private String getTerm(ActivityType activity)	{
		switch(activity)	{
			case BREAK:
				return termBreak;
			case PLACE:
				return termPlace;
			case TRAVEL:
				return termTravel;
			case CHAT:
				return termChat;
			case ANIMAL:
				return termAnimal;
			case MONSTER:
				return termMonster;
			case PLAYER:
				return termPlayer;
		}
		return termActivity;
	}
}
