package com.dwarfholm.activitystats.braizhauler;

public class ASShortData {
	protected int activity;
	protected int online;
	
	public ASShortData()	{
		clear();
	}
	public void clear()	{
		activity = 0;
		online = 0;
	}
	public void setEqual(ASShortData value)	{
		activity = value.activity;
		online = value.online;
	}
	public int getActivity()	{	return activity;	}
	public int getOnline()		{	return online;	}
	
	public void add(ASShortData other)	{
		activity = activity + other.activity;
		online = online + other.online;
	}
}
