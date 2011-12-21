package com.jugnoo.videos.db;

import java.sql.Connection;
import java.sql.SQLException;

import com.jbns.util.logging.Logger;
import com.jbns.util.logging.LoggerManager;
import com.jbns.util.sql.ConnectionManager;
import com.jbns.util.sql.DBConnection;
import com.jugnoo.videos.data.DatabaseInfo;
import com.jugnoo.videos.initwork.InitDatabase;

/**
 * get database connection from database pool
 * 
 * @author bwang
 * 
 */
public class DBConnectionManager {

	private static ConnectionManager connMgr = null;
	private static Logger logger = null;

	static {
		logger = LoggerManager.getLogger(DBConnectionManager.class);

		try {
			initDBConnectionManager();
		} catch (Exception e) {
			logger.fatal("can't create database connect failure!", e);
		}
	}

	private static void initDBConnectionManager() throws SQLException,
			ClassNotFoundException {

		DatabaseInfo dbInfo = InitDatabase.getDatabaseInfo();

		connMgr = ConnectionManager.createConnectionManager(dbInfo.getUrl(),
				dbInfo.getUser(), dbInfo.getPassword(), dbInfo.getPoolsize(),
				dbInfo.getType(), dbInfo.getRetrytime());
	}

	/**
	 * 获取数据库的连接
	 * 
	 * @return
	 * @throws SQLException
	 *             连接数据库异常
	 */
	public static Connection getConnection() throws SQLException {
		DBConnection conn = null;
		if (connMgr != null) {
			conn = connMgr.getConnection();
		}
		return conn;
	}

	/**
	 * 归还数据库连接
	 * 
	 * @param conn
	 *            Connection
	 */
	public static void returnConnection(DBConnection conn) {

		if (connMgr != null) {
			connMgr.returnConnection(conn);
		}
	}

}
