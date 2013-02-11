package com.dwarfholm.activitystats.braizhauler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;


public class ASMySQL {
	private ActivityStats plugin;
	private String prefix;
	
	enum TableType	{
		PLAYER, DAY, WEEK, MONTH, AUTOPROMOTE
	}
	public ASMySQL (ActivityStats plugin)	{
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
		} else if (table == TableType.AUTOPROMOTE) {
			tableName = "autopromote";
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
		createAutoPromoteTable();
	}
   
	private void createPlayerTable()	{
		String query = "";
		Connection connection = getSQLConnection();
		PreparedStatement statement = null;
		try {
			if ( !tableExists(TableType.PLAYER) ) {
				plugin.info("Creating ActivityStats.Player table.");
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
			if ( !tableExists(table) ) {
				plugin.info("Creating ActivityStats.Activity table.");
				query = "CREATE TABLE `" + TableName(table) + "`" +
						"(`id` INT NOT NULL PRIMARY KEY, " +
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
		String playerQuery = "Select * FROM `" + TableName(TableType.PLAYER) + "` WHERE `player` LIKE ?;";

		PreparedStatement statement = null;
		ResultSet result;
		Connection connection = getSQLConnection();
		try {
		//Player Table
			statement = connection.prepareStatement(playerQuery);
			statement.setString(1, player);
			result = statement.executeQuery();
			
			if (result.next())	{
				playerData.setDbID( result.getInt("id"));
				playerData.total.activity = result.getInt("totalActivity");
				playerData.total.online = result.getInt("totalOnline");
			}
			statement.close();
		} catch (SQLException e) {
			printStackError("MySQL load player error", e);
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
		String query    = "Select * FROM `" + TableName(table) + "` WHERE `id` = ?;";
		Connection connection = getSQLConnection();
		PreparedStatement statement = null;
		ResultSet result;
		try	{
			statement = connection.prepareStatement(query);
			statement.setInt(1, currentData.getDbID());
			result = statement.executeQuery();
			
			if (result.next())	{
				currentData = setCurActivity(currentData, table, result.getInt("curActivity"));
				currentData = setCurOnline(currentData, table, result.getInt("curOnline"));
				currentData = setLastActivity(currentData, table, result.getInt("lastActivity"));
				currentData = setLastOnline(currentData, table, result.getInt("lastOnline"));
			}
			statement.close();
		} catch (SQLException e) {
			printStackError("MySQL load Record table error", e);
		}
		return currentData;
	}
	

	
	public void updatePlayer(ASPlayer player) {
		Connection connection = getSQLConnection();
		PreparedStatement statement = null;
		Timestamp curtime = getCurrentTime();
		String query = "UPDATE `" + TableName(TableType.PLAYER) + "` " +
		"SET `lastonline` = ?, `totalActivity` = ?, `totalOnline` = ? " +
		"WHERE `id` = ?;";
		try {
			statement = connection.prepareStatement(query);
						
			statement.setTimestamp(1, curtime);
			statement.setInt(2, player.total.getActivity());
			statement.setInt(3, player.total.getOnline());
			statement.setInt(4, player.getDbID());
			
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			printStackError("MySQL update player error", e);
		}
		updateRecordTable(player, TableType.DAY);
		updateRecordTable(player, TableType.WEEK);
		updateRecordTable(player, TableType.MONTH);
	}
	
	private void updateRecordTable(ASPlayer player, TableType table) {
		Connection connection = getSQLConnection();
		PreparedStatement statement = null;
		if(playerExists(player.getName()))	{
			String query = "UPDATE `" + TableName(table) + "` " +
			"SET `curActivity` = ?, `curOnline` = ?, `lastActivity` = ?, `lastOnline` = ? " +
			"WHERE `id` = ?;";
			try {
				
				statement = connection.prepareStatement(query);
				statement.setInt(1, getCurActivity(player, table));
				statement.setInt(2, getCurOnline(player, table));
				statement.setInt(3, getLastActivity(player, table));
				statement.setInt(4, getLastOnline(player, table));		
				statement.setInt(5, player.getDbID());
				
				statement.executeUpdate();
				statement.close();
			} catch (SQLException e) {
				printStackError("MySQL update record error", e);
			}
		}
	}
	
	public void createPlayer(String playerName) {
		plugin.info("Adding " + playerName + " to database");
		ASPlayer player = new ASPlayer(playerName);
		Connection connection = getSQLConnection();
		PreparedStatement statement = null;
		Timestamp curtime = getCurrentTime();
	
		String query = "INSERT INTO `" + TableName(TableType.PLAYER) + "` " +
						"(`player`, `joined`, `lastonline`, `totalActivity`, `totalOnline`) " +
						" VALUES (?, ?, ?, ?, ?);";
		try {
		//Player Table
			statement = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
			
			statement.setString(1, player.getName());
			statement.setTimestamp(2, curtime);
			statement.setTimestamp(3, curtime);
			statement.setInt(4, player.total.getActivity());
			statement.setInt(5, player.total.getOnline());
			
			statement.executeUpdate();
			ResultSet rs = statement.getGeneratedKeys();
		    if (rs.next())	{
		    	player.setDbID( rs.getInt(1));
				statement.close();
			//Day record
				createPlayerRecord(connection, TableType.DAY, player);
			//Week record
				createPlayerRecord(connection, TableType.WEEK, player);
			//Month record
				createPlayerRecord(connection, TableType.MONTH, player);
		    }
		} catch (SQLException e) {
			printStackError("MySQL create player error", e);
		}
	}	
	private void createPlayerRecord(Connection connection, TableType table, ASPlayer player) throws SQLException	{	
		String query = "INSERT INTO `" + TableName(table) + "` " +
				"(`id`, `curActivity`, `curOnline`, `lastActivity`, `lastOnline`) " +
				" VALUES (?, ?, ?, ?, ?);";
		
		PreparedStatement statement = connection.prepareStatement(query);
		
		statement.setInt(1, player.getDbID());
		statement.setInt(2, getCurActivity(player, table));
		statement.setInt(3, getCurOnline(player, table));
		statement.setInt(4, getLastActivity(player, table));
		statement.setInt(5, getLastOnline(player, table));
		
		statement.executeUpdate();
		statement.close();
	}
		
	private Timestamp getCurrentTime()	{
		return new Timestamp(System.currentTimeMillis());
	}

	public void rollover(TableType table) {
		String queryLastActivity = "UPDATE `" + TableName(table) + "` SET `lastActivity` = `curActivity`;";
		String queryLastOnline = "UPDATE `" + TableName(table) + "` SET `lastOnline` = `curOnline`;";
		String queryCurActivity = "UPDATE `" + TableName(table) + "` SET `curActivity` = 0;";
		String queryCurOnline = "UPDATE `" + TableName(table) + "` SET `curOnline` = 0;";
		
		Connection connection = getSQLConnection();
		PreparedStatement statement = null;
		
		try {
			statement = connection.prepareStatement(queryLastActivity);
			statement.executeUpdate();
			statement = connection.prepareStatement(queryLastOnline);
			statement.executeUpdate();
			statement = connection.prepareStatement(queryCurActivity);
			statement.executeUpdate();
			statement = connection.prepareStatement(queryCurOnline);
			statement.executeUpdate();
		} catch (SQLException e) {
			printStackError("SQL rollover error with table " + TableName(table), e);
		}
	}
	
	private int getCurActivity(ASPlayer data, TableType table)	{
		switch(table)	{
		case DAY:	return data.curDay.activity;
		case WEEK:	return data.curWeek.activity;
		case MONTH:	return data.curMonth.activity;
		default:	plugin.severe("Invalid Type in getCurActivity");
		}
		return 0;
	}
	
	private int getCurOnline(ASPlayer data, TableType table)	{
		switch(table)	{
		case DAY:	return data.curDay.online;
		case WEEK:	return data.curWeek.online;
		case MONTH:	return data.curMonth.online;
		default:	plugin.severe("Invalid Type in getCurOnline");
		}
		return 0;
	}
	
	private int getLastActivity(ASPlayer data, TableType table)	{
		switch(table)	{
		case DAY:	return data.lastDay.activity;
		case WEEK:	return data.lastWeek.activity;
		case MONTH:	return data.lastMonth.activity;
		default:	plugin.severe("Invalid Type in getLastActivity");
		}
		return 0;
	}
	private int getLastOnline(ASPlayer data, TableType table)	{
		switch(table)	{
		case DAY:	return data.lastDay.online;
		case WEEK:	return data.lastWeek.online;
		case MONTH:	return data.lastMonth.online;
		default:	plugin.severe("Invalid Type in getLastOnline");
		}
		return 0;
	}
	
	private ASPlayer setCurActivity(ASPlayer data, TableType table, int value)	{
		switch(table)	{
		case DAY:	data.curDay.activity = value;
			break;
		case WEEK:	data.curWeek.activity = value;
			break;
		case MONTH:	data.curMonth.activity = value;
			break;
		default:	plugin.severe("Invalid Type in setCurActivity");
		}
		return data;
	}
	private ASPlayer setCurOnline(ASPlayer data, TableType table, int value)	{
		switch(table)	{
		case DAY:	data.curDay.online = value;
			break;
		case WEEK:	data.curWeek.online = value;
			break;
		case MONTH:	data.curMonth.online = value;
			break;
		default:	plugin.severe("Invalid Type in setCurOnline");
		}
		return data;
	}
	private ASPlayer setLastActivity(ASPlayer data, TableType table, int value)	{
		switch(table)	{
		case DAY:	data.lastDay.activity = value;
			break;
		case WEEK:	data.lastWeek.activity = value;
			break;
		case MONTH:	data.lastMonth.activity = value;
			break;
		default:	plugin.severe("Invalid Type in setLastActivity");
		}
		return data;
	}
	private ASPlayer setLastOnline(ASPlayer data, TableType table, int value)	{
		switch(table)	{
		case DAY:	data.lastDay.online = value;
			break;
		case WEEK:	data.lastWeek.online = value;
			break;
		case MONTH:	data.lastMonth.online = value;
			break;
		default:	plugin.severe("Invalid Type in setLastOnline");
		}
		return data;
	}
	
	public int autoPromoteUnhandledCount()	{
		int count = 0;
		String query = "SELECT COUNT(*) AS `playercount` FROM `" + TableName(TableType.AUTOPROMOTE) + "` WHERE `handled` = ?;";
		PreparedStatement statement = null;

		Connection connection = getSQLConnection();
		if ( tableExists(TableType.AUTOPROMOTE) ) {
			try	{
				statement = connection.prepareStatement(query);
				statement.setBoolean(1, false);
				
				ResultSet result = statement.executeQuery();
				
				if (result.next())
					count = result.getInt("playercount");
				statement.close();
			connection.close();
			} catch (SQLException e) {
				printStackError("MySQL unhandled count error", e);
			}
		}
		return count;
	}
	
	public String[] autoPromoteList()	{
		String playerQuery = "Select * FROM `" + TableName(TableType.AUTOPROMOTE) + "` WHERE `handled` = ?;";
		String playerlist[] = new String[autoPromoteUnhandledCount()];
		PreparedStatement statement = null;
		ResultSet result;
		Connection connection = getSQLConnection();
		try {
		//Player Table
			statement = connection.prepareStatement(playerQuery);
			statement.setBoolean(1, false);
			result = statement.executeQuery();
			
			for(int count=0;result.next();count++)	{
				playerlist[count] = result.getString("player");
			}
			statement.close();
		} catch (SQLException e) {
			printStackError("MySQL autoPromote list error", e);
		}
		return playerlist;
	}
	
	public void createAutoPromoteTable()	{
		String query = "";
		Connection connection = getSQLConnection();
		PreparedStatement statement = null;
		try {
			if ( !tableExists(TableType.AUTOPROMOTE) ) {
				plugin.info("Creating ActivityStats.AutoPromoter table.");
				query = "CREATE TABLE `" + TableName(TableType.AUTOPROMOTE) + "`" +
					"(`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
					"`player` VARCHAR(32) NOT NULL, `handled` BIT(1)" +
					") ENGINE = InnoDB;";
				statement = connection.prepareStatement(query);
				statement.executeUpdate();
				statement.close();
			}
			connection.close();
		} catch (SQLException e) {
			printStackError("MySQL autopromoter creation error", e);
		}
	}
	public boolean addAutoPromoteValue(String name, boolean handled)	{
		boolean success = false;
		String query = "";	
		PreparedStatement statement = null;
		query = "INSERT INTO `" + TableName(TableType.AUTOPROMOTE) + "`" +
				"(`player`, `handled`) " +
				" VALUES (?, ?);";
		Connection connection = getSQLConnection();
		try {
			statement = connection.prepareStatement(query);
			
			statement.setString(1, name);
			statement.setBoolean(2, handled);
			
			if (statement.executeUpdate()==1)	{
				success=true;
			}
			statement.close();
			connection.close();
		} catch (SQLException e) {
			printStackError("MySQL autopromoter creation error", e);
		}
		return success;
	}
	
	
	public boolean autoPromoterPlayerExists(String playerName)	{
		boolean exists = false;
		String query = "";
		Connection connection = getSQLConnection();
		PreparedStatement statement = null;
		
		if ( tableExists(TableType.PLAYER) ) {
			query = "SELECT COUNT(*) AS `playercount` FROM `" + TableName(TableType.AUTOPROMOTE) + "` WHERE `player` = ?;";
			try	{
				statement = connection.prepareStatement(query);
				statement.setString(1, playerName);
				
				ResultSet result = statement.executeQuery();
				
				if (result.next())
					exists = (result.getInt("playercount") > 0);
				statement.close();
			connection.close();
			} catch (SQLException e) {
				printStackError("MySQL autopromoter player exist check error", e);
			}
		}
		return exists;
	}

	public boolean updateAutoPromoteValue(String name, boolean handled) {
		boolean success = false;
		String query = "UPDATE `" + TableName(TableType.AUTOPROMOTE) + "`" +
				"SET `handled`=? WHERE `player` = ?;";;
		Connection connection = getSQLConnection();
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(query);
			
			statement.setBoolean(1, handled);
			statement.setString(2, name);
			if (statement.executeUpdate()==1)	{
				success=true;
			}
			statement.close();
			connection.close();
		} catch (SQLException e) {
			printStackError("MySQL autopromoter update error", e);
		}
		return success;
	}
}