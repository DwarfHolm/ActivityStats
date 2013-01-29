package com.dwarfholm.activitystats.braizhauler;

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
}
