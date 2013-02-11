package com.dwarfholm.activitystats.braizhauler;

import org.bukkit.Location;

public class ASPlayer {
	private String name;
	public ASLongData curPeriod;
	public ASLongData lastPeriod;
	public ASShortData curDay;
	public ASShortData lastDay;
	public ASShortData curWeek;
	public ASShortData lastWeek;
	public ASShortData curMonth;
	public ASShortData lastMonth;
	public ASShortData total;
	private int dbID;
	
	public ASPlayer(String name)	{
		this.name=name;
		setDbID(-1);
		curPeriod = new ASLongData();
		lastPeriod = new ASLongData();
		curDay = new ASShortData();
		lastDay = new ASShortData();
		curWeek = new ASShortData();
		lastWeek = new ASShortData();
		curMonth = new ASShortData();
		lastMonth = new ASShortData();
		total = new ASShortData();
	}
	
	public void rolloverPeriod()	{
		curDay.add(lastPeriod);
		curWeek.add(lastPeriod);
		curMonth.add(lastPeriod);
		total.add(lastPeriod);
		lastPeriod.setEqual(curPeriod);
		curPeriod.clear();
	}
	public void rolloverDay()	{
		lastDay.setEqual(curDay);
		curDay.clear();
	}
	public void rolloverWeek()	{
		lastWeek.setEqual(curWeek);
		curWeek.clear();
	}
	public void rolloverMonth()	{
		lastMonth.setEqual(curMonth);
		curMonth.clear();
	}
	public void setName(String name)	{
		this.name=name;
	}
	public String getName()	{
		return name;
	}
	public int getActivity()	{
		return curPeriod.getActivity();
	}
	public void calculateTravel(Location newLoc) throws java.lang.IllegalArgumentException {
		curPeriod.calculateTravel(newLoc);
	}
	public void calculateTravel(Location from, Location to) throws java.lang.IllegalArgumentException {
		curPeriod.calculateTravel(from, to);
	}
	public void setLocation	(Location loc) {
		curPeriod.setLocation(loc);
	}
	public int getDbID()	{	return dbID;	}
	public void setDbID(int dbID)	{	this.dbID = dbID;	}
}
