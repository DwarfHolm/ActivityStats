package com.dwarfholm.activitystats.braizhauler;

public class ASLongData extends ASShortData {
	private int blockBreak;
	private int blockPlace;
	private int traveled;
	private int chat;
	private int damAnimal;
	private int damMonster;
	private int damPlayer;
	

	public ASLongData()	{
		clear();
	}
	
	public void clear()	{
		blockBreak = 0;
		blockPlace = 0;
		traveled = 0;
		damAnimal = 0;
		damMonster = 0;
		damPlayer = 0;
		super.clear();
	}
	public void setEqual(ASLongData value)	{
		blockBreak = value.blockBreak;
		blockPlace = value.blockPlace;
		traveled = value.traveled;
		chat = value.chat;
		damAnimal = value.damAnimal;
		damMonster = value.damMonster;
		damPlayer = value.damPlayer;
		super.setEqual(value);
	}	
	public void brokeBlock(int add, int max)	{
		blockBreak = Math.min(blockBreak + add, max);
		calculateActivity();
	}	
	public void placeBlock(int add, int max)	{
		blockPlace = Math.min(blockPlace + add, max);
		calculateActivity();
	}	
	public void travel(int add, int max)	{
		traveled = Math.min(traveled + add, max);
		calculateActivity();
	}
	public void chat(int add, int max)	{
		chat = Math.min(chat + add, max);
		calculateActivity();
	}	
	public void damageAnimal(int add, int max)	{
		damAnimal = Math.min(damAnimal + add, max);
		calculateActivity();
	}	
	public void damageMonster(int add, int max)	{
		damMonster = Math.min(damMonster + add, max);
		calculateActivity();
	}
	public void damagePlayer(int add, int max)	{
		damPlayer = Math.min(damPlayer + add, max);
		calculateActivity();
	}
	public void addOnline(int max)	{
		online += 1;
	}	
	private void calculateActivity()	{
		activity = blockBreak + blockPlace + traveled + damAnimal + damMonster + damPlayer;
	}
}
