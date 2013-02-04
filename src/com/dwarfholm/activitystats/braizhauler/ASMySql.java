package com.dwarfholm.activitystats.braizhauler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ASMySql {
	private ActivityStats plugin;
	private String prefix;

	public ASMySql (ActivityStats plugin)	{
		this.plugin = plugin;

		getPrefix();
	}
	
	private String getPrefix()	{
		prefix = plugin.config().sqlPrefix;
		return prefix;
	}
	
	private String TableName(TableType table)	{
		String tableName;
		if(table == TableType.DAY)	{
			tableName = "day";
		} else if(table == TableType.WEEK)	{
			tableName = "week";
		} else if(table == TableType.MONTH)	{
			tableName = "month";
		} else { //TableType = Player
			tableName = "player";
		}
		return getPrefix() + tableName;
	}

	private Connection getSQLConnection() {
		try {
			Connection connection;
			
			if (plugin.config().useMySQL) {
				connection = DriverManager.getConnection(plugin.config().sqlURI, plugin.config().sqlUsername, plugin.config().sqlPassword);
			} else {
				connection = DriverManager.getConnection(plugin.config().sqlURI);
			}
			return connection;
		} catch (SQLException e) {
			printStackError("MySQL Connection Error", e);
		}
		return null;
	}
	
	private void printStackError(String error, SQLException e)	{
		plugin.severe(error);
		for (StackTraceElement trace: e.getStackTrace())
			plugin.severe(trace.toString());
	}
	
	private boolean tableExists(TableType table)	{
		 return tableExists(TableName(table));
	}
	
	private boolean tableExists(String tableName)	{
		boolean exists = false;
		try {
			Connection connection = getSQLConnection();

			PreparedStatement statement = connection.prepareStatement("show tables like '" + tableName + "'");
			ResultSet result = statement.executeQuery();

			result.last();
			if (result.getRow() != 0) 
				exists = true;			
			result.close();
			statement.close();
			connection.close();

		} catch (SQLException e) {
			printStackError("MySQL Table Error Error", e);
		}
		return exists;
	}
   
	public void createTables() {
		createPlayerTable();
		createRecordTable(TableType.DAY);
		createRecordTable(TableType.WEEK);
		createRecordTable(TableType.MONTH);
	}
   
	private void createPlayerTable()	{
		String query = "";
		Connection connection = getSQLConnection();
		PreparedStatement statement = null;
		try {
			if ( !tableExists(TableType.PLAYER) ) {
				plugin.info("Creating DwarfHolmTax.Chests table.");
				query = "CREATE TABLE `" + TableName(TableType.PLAYER) + "`" +
					"(`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
					"`player` VARCHAR(32) NOT NULL, `joined` DATETIME, `lastonline` DATETIME, " +
					"`totalActivity` INT, `totalOnline` INT" +
					") ENGINE = InnoDB;";
				statement = connection.prepareStatement(query);
				statement.executeUpdate();
				statement.close();
			}
			connection.close();
		} catch (SQLException e) {
			printStackError("MySQL player creation error", e);
		}		
	}
	
	private void createRecordTable(TableType table)	{
		String query = "";
		Connection connection = getSQLConnection();
		PreparedStatement statement = null;
		try {
			if ( tableExists(table) ) {
				plugin.info("Creating ActivityStats.Activity table.");
				query = "CREATE TABLE `" + TableName(table) + "`" +
						"(`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
						"`player` VARCHAR(32) NOT NULL, " +
						"`curActivity` INT, `curOnline` INT," +
						"`lastActivity` INT, `lastOnline` INT" +
						") ENGINE = InnoDB;";
				statement = connection.prepareStatement(query);
				statement.executeUpdate();
				statement.close();
			}
			connection.close();
		} catch (SQLException e) {
			printStackError("MySQL activity table creation Error", e);
		}		
	}
	
	private boolean playerExists(String playerName)	{
		boolean exists = false;
		String query = "";
		Connection connection = getSQLConnection();
		PreparedStatement statement = null;
		
		if ( tableExists(TableType.PLAYER) ) {
			query = "SELECT COUNT(*) AS `playercount` FROM `" + TableName(TableType.PLAYER) + "` WHERE `player` = ?;";
			try	{
				statement = connection.prepareStatement(query);
				statement.setString(1, playerName);
				
				ResultSet result = statement.executeQuery();
				
				if (result.next())
					exists = (result.getInt("playercount") > 0);
				statement.close();
			connection.close();
			} catch (SQLException e) {
				printStackError("MySQL player exist check error", e);
			}
		}
		return exists;
	}
	
	public void loadPlayer(String player)	{
		ASPlayer playerData = new ASPlayer(player);
		if(!playerExists(player))	{
			createPlayer(playerData);
		}
		
		String playerQuery = "Select * FROM `" + TableName(TableType.PLAYER) + "` WHERE `player` LIKE ?;";

		Connection connection = getSQLConnection();
		PreparedStatement statement = null;
		ResultSet result;
		try {
		//Player Table
			statement = connection.prepareStatement(playerQuery);
			statement.setString(1, player);
			result = statement.executeQuery();
			
			if (result.next())	{
				playerData.total.activity = result.getInt("totalActivity");
				playerData.total.online = result.getInt("totalOnline");
			}
			statement.close();
		} catch (SQLException e) {
			printStackError("MySQL create player error", e);
		}

		//Day Record Table
		loadRecordTable(TableType.DAY, playerData);
		//Week Record Table
		loadRecordTable(TableType.WEEK, playerData);
		//Month Record Table
		loadRecordTable(TableType.MONTH, playerData);
		
		
		plugin.getASPlayersList().setPlayer(playerData);
	}
	
	private ASPlayer loadRecordTable (TableType table, ASPlayer currentData)	{
		String query    = "Select * FROM `" + TableName(table) + "` WHERE `player` LIKE ?;";
		Connection connection = getSQLConnection();
		PreparedStatement statement = null;
		ResultSet result;
		try	{
			statement = connection.prepareStatement(query);
			statement.setString(1, currentData.getName());
			result = statement.executeQuery();
			
			if (result.next())	{
				if ( table == TableType.DAY)	{
					currentData.curDay.activity = result.getInt("curActivity");
					currentData.curDay.online = result.getInt("curOnline");
					currentData.lastDay.activity = result.getInt("lastActivity");
					currentData.lastDay.online = result.getInt("lastOnline");
				} else if ( table == TableType.DAY)	{
					currentData.curWeek.activity = result.getInt("curActivity");
					currentData.curWeek.online = result.getInt("curOnline");
					currentData.lastWeek.activity = result.getInt("lastActivity");
					currentData.lastWeek.online = result.getInt("lastOnline");
				} else if ( table == TableType.DAY)	{
					currentData.curMonth.activity = result.getInt("curActivity");
					currentData.curMonth.online = result.getInt("curOnline");
					currentData.lastMonth.activity = result.getInt("lastActivity");
					currentData.lastMonth.online = result.getInt("lastOnline");
				}
			}
			statement.close();
		} catch (SQLException e) {
			printStackError("MySQL create player error", e);
		}
		return currentData;
	}
	
	private void createPlayer(ASPlayer player) {
		Connection connection = getSQLConnection();
		PreparedStatement statement = null;
		Timestamp curtime = getCurrentTime();
	
		String playerQuery = "INSERT INTO `" + TableName(TableType.PLAYER) + "` " +
						"(`player`, `joined`, `lastonline`, `totalActivity`, `totalOnline`) " +
						" VALUES (?, ?, ?, ?, ?, ?);";
		String dayQuery = "INSERT INTO `" + TableName(TableType.DAY) + "` " +
						"(`player`, `curActivity`, `curOnline`, `lastActivity`, `lastOnline`) " +
						" VALUES (?, ?, ?, ?, ?, ?);";
		String weekQuery = "INSERT INTO `" + TableName(TableType.WEEK) + "` " +
						"(`player`, `curActivity`, `curOnline`, `lastActivity`, `lastOnline`) " +
						" VALUES (?, ?, ?, ?, ?, ?);";
		String monthQuery = "INSERT INTO `" + TableName(TableType.MONTH) + "` " +
						"(`player`, `curActivity`, `curOnline`, `lastActivity`, `lastOnline`) " +
						" VALUES (?, ?, ?, ?, ?, ?);";
		try {
		//Player Table
			statement = connection.prepareStatement(playerQuery);
			
			statement.setString(1, player.getName());
			statement.setTimestamp(2, curtime);
			statement.setTimestamp(3, curtime);
			statement.setInt(4, player.total.getActivity());
			statement.setInt(5, player.total.getOnline());
			
			statement.executeUpdate();
			statement.close();
		//Day Record Table
			statement = connection.prepareStatement(dayQuery);
			
			statement.setString(1, player.getName());
			statement.setInt(2, player.curDay.getActivity());
			statement.setInt(3, player.curDay.getOnline());
			statement.setInt(4, player.lastDay.getActivity());
			statement.setInt(5, player.lastDay.getOnline());
			
			statement.executeUpdate();
			
			statement.close();
		//Week record
			statement = connection.prepareStatement(weekQuery);
			
			statement.setString(1, player.getName());
			statement.setInt(2, player.curWeek.getActivity());
			statement.setInt(3, player.curWeek.getOnline());
			statement.setInt(4, player.lastWeek.getActivity());
			statement.setInt(5, player.lastWeek.getOnline());
			
			statement.executeUpdate();
			
			statement.close();
		//Month record
			statement = connection.prepareStatement(monthQuery);
			
			statement.setString(1, player.getName());
			statement.setInt(2, player.curMonth.getActivity());
			statement.setInt(3, player.curMonth.getOnline());
			statement.setInt(4, player.lastMonth.getActivity());
			statement.setInt(5, player.lastMonth.getOnline());
			
			statement.executeUpdate();
			
			statement.close();
		} catch (SQLException e) {
			printStackError("MySQL create player error", e);
		}
	}
	
	private void updateRecordTable(ASPlayer player, TableType table) {
		Connection connection = getSQLConnection();
		PreparedStatement statement = null;
		if(playerExists(player.getName()))	{
			String query = "UPDATE `" + TableName(table) + "` " +
			"SET `curActivity` = ?, `curOnline` = ?, `lastActivity` = ?, `lastOnline` = ? " +
			"WHERE `player` LIKE ?;";
			try {
				
				statement = connection.prepareStatement(query);
				
				if (table == TableType.DAY)	{
					statement.setInt(1, player.curDay.getActivity());
					statement.setInt(2, player.curDay.getOnline());
					statement.setInt(3, player.lastDay.getActivity());
					statement.setInt(4, player.lastDay.getOnline());	
				} else if (table == TableType.WEEK)	{
					statement.setInt(1, player.curWeek.getActivity());
					statement.setInt(2, player.curWeek.getOnline());
					statement.setInt(3, player.lastWeek.getActivity());
					statement.setInt(4, player.lastWeek.getOnline());
				} else if (table == TableType.MONTH)	{
					statement.setInt(1, player.curMonth.getActivity());
					statement.setInt(2, player.curMonth.getOnline());
					statement.setInt(3, player.lastMonth.getActivity());
					statement.setInt(4, player.lastMonth.getOnline());
				}
				
				statement.setString(5, player.getName());
				
				statement.executeUpdate();
				statement.close();
			} catch (SQLException e) {
				printStackError("MySQL create player error", e);
			}
		}
	}
	
	public void updatePlayer(ASPlayer player) {
		Connection connection = getSQLConnection();
		PreparedStatement statement = null;
		Timestamp curtime = getCurrentTime();
		if(playerExists(player.getName()))	{
			String query = "UPDATE `" + TableName(TableType.PLAYER) + "` " +
			"SET `lastonline` = ?, `totalActivity` = ?, `totalOnline` = ?) " +
			"WHERE `player` LIKE ?;";
			try {
				statement = connection.prepareStatement(query);
				
				statement.setTimestamp(1, curtime);
				statement.setInt(2, player.total.activity);
				statement.setInt(3, player.total.online);
				statement.setString(4, player.getName());
				
				statement.executeUpdate();
				statement.close();
			} catch (SQLException e) {
				printStackError("MySQL create player error", e);
			}
		}
		updateRecordTable(player, TableType.DAY);
		updateRecordTable(player, TableType.WEEK);
		updateRecordTable(player, TableType.MONTH);
	}
	
	private Timestamp getCurrentTime()	{
		return new Timestamp(System.currentTimeMillis());
	}
	
}
