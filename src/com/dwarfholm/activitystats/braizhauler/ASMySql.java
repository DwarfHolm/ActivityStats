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
	private final String PLAYER_TABLE_NAME = "Player";
	private final String DAY_TABLE_NAME = "Day";
	private final String WEEK_TABLE_NAME = "Week";
	private final String MONTH_TABLE_NAME = "Month";
	
	public ASMySql (ActivityStats plugin)	{
		this.plugin = plugin;
		updatePrefix();
	}
	
	private void updatePrefix()	{
		prefix = plugin.config().getString("database.mysql.tableprefix");
	}
	
	public Connection getSQLConnection() {
		try {
			Connection connection;
			
			if (plugin.config().getBoolean("database.use-mysql")) {
				connection = DriverManager.getConnection("jdbc:mysql://" + plugin.config().getString("database.mysql.hostname") + 
														":" + plugin.config().getString("database.mysql.port") +
														"/" + plugin.config().getString("database.mysql.database"),
														plugin.config().getString("database.mysql.username"),
														plugin.config().getString("database.mysql.password"));
			} else {
				connection = DriverManager.getConnection("jdbc:mysql://" + plugin.config().getString("database.mysql.hostname") + 
														":" + plugin.config().getString("database.mysql.port") +
														"/" + plugin.config().getString("database.mysql.database"));
			}

			return connection;
		} catch (SQLException e) {
			printStackError("MySQL Connection Error", e);
		}
		return null;
	}
	
	public void printStackError(String error, SQLException e)	{
		plugin.severe(error);
		for (StackTraceElement trace: e.getStackTrace())	{
			plugin.severe(trace.toString());
		}
	}
	
	public boolean tableExists(String tableName)	{
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
		createRecordTable(DAY_TABLE_NAME);
		createRecordTable(WEEK_TABLE_NAME);
		createRecordTable(MONTH_TABLE_NAME);
	}
	
	public void createPlayerTable()	{
		String query = "";
		Connection connection = getSQLConnection();
		PreparedStatement statement = null;
		try {
			if ( !tableExists(prefix + PLAYER_TABLE_NAME) ) {
				plugin.info("Creating DwarfHolmTax.Chests table.");
				query = "CREATE TABLE `" + prefix + PLAYER_TABLE_NAME + "`" +
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
	
	private void createRecordTable(String tableName)	{
		String query = "";
		Connection connection = getSQLConnection();
		PreparedStatement statement = null;
		try {
			if ( tableExists(prefix + tableName) ) {
				plugin.info("Creating ActivityStats.Activity table.");
				query = "CREATE TABLE `" + prefix + tableName + "`" +
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
	
	public boolean playerExists(String playerName)	{
		boolean exists = false;
		String query = "";
		Connection connection = getSQLConnection();
		PreparedStatement statement = null;
		
		if ( tableExists(prefix + PLAYER_TABLE_NAME) ) {
			query = "SELECT COUNT(*) AS `playercount` FROM `" + prefix + PLAYER_TABLE_NAME + "` WHERE `player` = ?;";
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
	
	public void createPlayer(ASPlayer player) {
		Connection connection = getSQLConnection();
		PreparedStatement statement = null;
		Timestamp curtime = getCurrentTime();
		if(playerExists(player.getName()))	{
			String playerQuery = "INSERT INTO `" + prefix + PLAYER_TABLE_NAME + "` " +
							"(`player`, `joined`, `lastonline`, `totalActivity`, `totalOnline`) " +
							" VALUES (?, ?, ?, ?, ?, ?);";
			String dayQuery = "INSERT INTO `" + prefix + DAY_TABLE_NAME + "` " +
							"(`player`, `curActivity`, `curOnline`, `lastActivity`, `lastOnline`) " +
							" VALUES (?, ?, ?, ?, ?, ?);";
			String weekQuery = "INSERT INTO `" + prefix + WEEK_TABLE_NAME + "` " +
							"(`player`, `curActivity`, `curOnline`, `lastActivity`, `lastOnline`) " +
							" VALUES (?, ?, ?, ?, ?, ?);";
			String monthQuery = "INSERT INTO `" + prefix + MONTH_TABLE_NAME + "` " +
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
	}
	
	public void updateRecordTable(ASPlayer player, String tableName) {
		Connection connection = getSQLConnection();
		PreparedStatement statement = null;
		if(playerExists(player.getName()))	{
			String query = "UPDATE `" + prefix + tableName + "` " +
			"SET `curActivity` = ?, `curOnline` = ?, `lastActivity` = ?, `lastOnline` = ? " +
			"WHERE `player` LIKE ?;";
			try {
				
				statement = connection.prepareStatement(query);
				
				if (tableName.equals(DAY_TABLE_NAME))	{
					statement.setInt(1, player.curDay.getActivity());
					statement.setInt(2, player.curDay.getOnline());
					statement.setInt(3, player.lastDay.getActivity());
					statement.setInt(4, player.lastDay.getOnline());	
				} else if (tableName.equals(WEEK_TABLE_NAME))	{
					statement.setInt(1, player.curWeek.getActivity());
					statement.setInt(2, player.curWeek.getOnline());
					statement.setInt(3, player.lastWeek.getActivity());
					statement.setInt(4, player.lastWeek.getOnline());
				} else if (tableName.equals(MONTH_TABLE_NAME))	{
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
	
	public void updateTotals(ASPlayer player) {
		Connection connection = getSQLConnection();
		PreparedStatement statement = null;
		Timestamp curtime = getCurrentTime();
		if(playerExists(player.getName()))	{
			String query = "UPDATE `" + prefix + PLAYER_TABLE_NAME + "` " +
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
	}
	
	public Timestamp getCurrentTime()	{
		return new Timestamp(System.currentTimeMillis());
	}
	
}
