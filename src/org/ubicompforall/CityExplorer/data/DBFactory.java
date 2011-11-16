package org.ubicompforall.CityExplorer.data;

import android.content.Context;

/**
 * A factory for creating DB objects.
 *
 * @author Christian Skjetne
 */
public class DBFactory
{
	
	/**
	 * The Enum DBType.
	 */
	public enum DBType
	{
		SQLITE;
	}
	
	/** The DataBase connector instance. */
	private static DatabaseInterface dbConnectorInstance;
	
	/** The database type. */
	private static DBType databaseType = DBType.SQLITE; //change this to change the database type
	
	/**
	 * Gets the single instance of DBFactory.
	 *
	 * @param context The context
	 * @return Single instance of DBFactory
	 */
	public static DatabaseInterface getInstance(Context context){
		if(dbConnectorInstance == null || dbConnectorInstance.isOpen() == false){
			if(databaseType == DBType.SQLITE){
				dbConnectorInstance = new SQLiteConnector(context);
			}
			dbConnectorInstance.open();
		}
		dbConnectorInstance.setContext(context);
		return dbConnectorInstance;
	}//getInstance
}//class DBFactory
