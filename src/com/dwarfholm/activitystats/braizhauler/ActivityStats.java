package com.dwarfholm.activitystats.braizhauler;


import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ActivityStats extends JavaPlugin {
	private YamlConfiguration config;
	private Vault vault;
	private ASLocale locale;
	private Logger log;
	private ASData players;
	private ASCommands commands;

	YamlConfiguration rolloverdata;

	private Date lastPeriodRollover;
	private Date lastDayRollover;
	private Date lastWeekRollover;
	private Date lastMonthRollover;
	
	private final long MILLIS_PER_MINUTE = 60000;
	private final long MILLIS_PER_DAY = 86400000;
	private final long MILLIS_PER_WEEK = 7 * MILLIS_PER_DAY;
	private final long MILLIS_PER_MONTH = 30 * MILLIS_PER_DAY;
	
	private ASTravelTimer travelTimer;
	private ASPaymentTimer payTimer;
	
	private final String CONFIG_FILE_NAME = "config.yml";
	private final String ROLLOVER_FILE_NAME = "data.yml";
	private final String LOG_NAME = "ActivityStats";
	private final String CONSOLE = "console";
	
	public ActivityStats()	{
		log = Logger.getLogger(LOG_NAME);
	}
	
	public void onEnable() {
		loadConfiguation();
		loadRollovers();

		locale = new ASLocale(this);
		locale.load(Locale.US);
		vault = new Vault(this);
		vault.connect();
		
		players = new ASData(this);
		ASLongData.setConfig(config);
		
		commands = new ASCommands(this);
		commands.registerCommands();
		
		travelTimer = new ASTravelTimer(this);
		payTimer = new ASPaymentTimer(this);
		
		travelTimer.start();
		payTimer.start();
	}
	
	public void onDisable() {
		commands.unregisterCommands();
		commands = null;

		payTimer.stop();
		travelTimer.stop();
		payTimer = null;
		travelTimer = null;
		players = null;
		
		vault.disconnect();
		locale = null;
		config = null;
	}
	
	public void loadConfiguation()	{
		config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), CONFIG_FILE_NAME));
		Configuration defaultConfig = YamlConfiguration.loadConfiguration( getClass().getResourceAsStream("/" + CONFIG_FILE_NAME) );
		config.setDefaults(defaultConfig);
		saveConfiguation();
	}
	private void saveConfiguation()	{
		saveResource(CONFIG_FILE_NAME, true);
	}
	
	public void loadPlayer(String name)	{	players.loadPlayer(name);	}
	public void savePlayer(String name)	{	players.savePlayer(name);	}
	public ASData getASPlayersList()	{	return players;	}
	public ASPlayer getASPlayer(String name)	{
		return players.getPlayer(name);
	}
	
	public ASLocale getLocalization()	{
		return locale;
	}
	
	public YamlConfiguration config()	{	return config;	}
	public Economy econ()	{	return vault.econ;	}
	public Permission perms()	{	return vault.perms;	}
	
	
	public void msg(String sSender, String msg)	{
		CommandSender csSender;
		if (sSender.equalsIgnoreCase(CONSOLE))	{
			csSender = (CommandSender)getServer().getConsoleSender();
		} else	{
			csSender = getServer().getPlayer(sSender);
		}
		msg (csSender, msg);
	}
	

	public boolean PeriodRolloverDue()	{
		return ( System.currentTimeMillis() - lastPeriodRollover.getTime() ) > config.getInt("interval") * MILLIS_PER_MINUTE;
	}
	public boolean DayRolloverDue()	{
		return ( System.currentTimeMillis() - lastDayRollover.getTime() ) > MILLIS_PER_DAY;
	}
	public boolean WeekRolloverDue()	{
		return ( System.currentTimeMillis() - lastWeekRollover.getTime() ) > MILLIS_PER_WEEK;
	}
	public boolean MonthRolloverDue()	{
		return ( System.currentTimeMillis() - lastMonthRollover.getTime() ) > MILLIS_PER_MONTH;
	}
	
	public Date getLastPeriodRollover()	{	return lastPeriodRollover;	}
	public Date getLastDayRollover()	{	return lastDayRollover;	}
	public Date getLastWeekRollover()	{	return lastWeekRollover;	}
	public Date getLastMonthRollover()	{	return lastMonthRollover;	}
	
	public void rolledoverPeriod()	{
		lastPeriodRollover.setTime(System.currentTimeMillis());
		saveRollovers();
	}
	public void rolledoverDay()	{
		lastDayRollover.setTime(System.currentTimeMillis());
		saveRollovers();
	}
	public void rolledoverWeek()	{
		lastWeekRollover.setTime(System.currentTimeMillis());
		saveRollovers();
	}
	public void rolledoverMonth()	{
		lastMonthRollover.setTime(System.currentTimeMillis());
		saveRollovers();
	}
	
	private void loadRollovers()	{
		rolloverdata = YamlConfiguration.loadConfiguration(new File(getDataFolder(), ROLLOVER_FILE_NAME));
		lastPeriodRollover = dateStringToDate(rolloverdata.getString("lastrollover.period", "October 30, 1982 10:48:00pm"));
		lastDayRollover = dateStringToDate(rolloverdata.getString("lastrollover.day", "October 30, 1982 10:48:00pm"));
		lastWeekRollover = dateStringToDate(rolloverdata.getString("lastrollover.week", "October 30, 1982 10:48:00pm"));
		lastMonthRollover = dateStringToDate(rolloverdata.getString("lastrollover.month", "October 30, 1982 10:48:00pm"));
		saveRollovers();
	}
	
	private void saveRollovers()	{
		rolloverdata.set("lastrollover.period", dateDateToString(lastPeriodRollover));
		rolloverdata.set("lastrollover.day", dateDateToString(lastDayRollover));
		rolloverdata.set("lastrollover.week", dateDateToString(lastWeekRollover));
		rolloverdata.set("lastrollover.month", dateDateToString(lastMonthRollover));
		saveResource(ROLLOVER_FILE_NAME, true);
	}
	
	private String dateDateToString(Date time)	{
		return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(time);
	}
	
	private Date dateStringToDate(String date)	{
		Date time = null;
		try {
			time = DateFormat.getDateInstance().parse(date);
		} catch (ParseException e) {
			severe("Date Parse Error");
		}
		return time;
	}
	
	public void payPlayer(ASPlayer player)	{
		double amount;
		String payMode = config.getString("economy.mode");
		if (payMode.equalsIgnoreCase("Percent"))	{
			amount = getPercentPayment(player);
		} else if (payMode.equalsIgnoreCase("Boolean"))	{
			amount = getBooleanPayment(player);
		} else	{ 
			return;
		}
		msg(getServer().getPlayer(player.getName()), locale.getPaymentMessage(amount));
		vault.econ.depositPlayer(player.getName() , amount);
	}
	
	public double getPercentPayment(ASPlayer player)	{
		return config.getDouble("economy.min") + ( config.getDouble("economy.max") - config.getDouble("economy.min") )* getActivityPercent(player) ;
	}
	
	public double getActivityPercent (ASPlayer player)	{
		return Math.max( 1.0, (double)player.getActivity() / config.getDouble("quota"));
	}
	public double getBooleanPayment(ASPlayer player)	{
		if((double)player.curPeriod.getActivity() > config.getDouble("quota"))
			return config.getDouble("economy.max");
		else
			return config.getDouble("economy.min");
	}
	
	public void msg(CommandSender sender, String msg)	{	sender.sendMessage(msg);	}
	
	public void severe(String msg)	{	log.severe(msg);	}
	public void warning(String msg)	{	log.warning(msg);	}
	public void info(String msg)	{	log.info(msg);		}
	public void fine(String msg)	{	log.fine(msg);		}
	public void finer(String msg)	{	log.finer(msg);		}
	public void finest(String msg)	{	log.finest(msg);	}
	
}