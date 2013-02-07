package com.dwarfholm.activitystats.braizhauler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ASRemoteMySQL {
	private ActivityStats plugin;
	private String tableName;

	
	public ASRemoteMySQL(ActivityStats plugin)	{
		this.plugin = plugin;

		getTableName();
	}
	
	private String getTableName()	{
		tableName = plugin.config().remotesqlTable;
		return tableName;
	}

	private Connection getRemoteSQLConnection() {
		try {
			Connection connection;
			
			if (plugin.config().useremoteMySQL) {
				connection = DriverManager.getConnection(plugin.config().remotesqlURI, plugin.config().remotesqlUsername, plugin.config().remotesqlPassword);
			} else {
				connection = DriverManager.getConnection(plugin.config().remotesqlURI);
			}
			return connection;
		} catch (SQLException e) {
			printStackError("Remote MySQL Connection Error", e);
		}
		return null;
	}
	
	private void printStackError(String error, SQLException e)	{
		plugin.severe(error);
		for (StackTraceElement trace: e.getStackTrace())
			plugin.severe(trace.toString());
	}
	
	public String[] retrieveSQL()	{
		String column = plugin.config().remotesqlColumn;
		int rowCount = 0;
		String[] playerlist = null;
		String countQuery = "SELECT COUNT(*) AS `playercount` FROM `" + tableName + "` WHERE " + plugin.config().remotesqlCondition + ";";
		String listQuery = "SELECT `" + column +"` FROM `" + tableName + "` WHERE " + plugin.config().remotesqlCondition + ";";
		PreparedStatement statement = null;
		ResultSet result = null;
		Connection connection = getRemoteSQLConnection();
		try {
			statement = connection.prepareStatement(countQuery);
			result = statement.executeQuery();
			if (result.next())
				rowCount = result.getInt("playercount");
			if (rowCount > 0)	{
				statement = connection.prepareStatement(listQuery);
				result = statement.executeQuery();
				playerlist = new String [rowCount];
			}
			while(result.next())	{
				playerlist[--rowCount] = result.getString(column);
			}
					
			result.close();
			statement.close();
			connection.close();

		} catch (SQLException e) {
			printStackError("MySQL Table Error Error", e);
		}
		return playerlist;
	}
}