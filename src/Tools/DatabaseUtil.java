package Tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

public class DatabaseUtil {

	private Connection mConnection;

	/**
	 * 获取数据库连接
	 * 
	 * @return 唯一数据库连接
	 */
	public DatabaseUtil() {
		mConnection = null;
		getConnection();
	}

	private Connection getConnection() {
		if (mConnection == null) {
			String url = "jdbc:mysql://59b6aed4552fc.sh.cdb.myqcloud.com:5244/Trivia"; // 数据库的Url
			try {
				Class.forName("com.mysql.jdbc.Driver"); // java反射，固定写法
				mConnection = (Connection) DriverManager.getConnection(url, "root", "RJCS123456");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
			}
		}
		return mConnection;
	}

	/**
	 * 查询操作
	 * 
	 * @param querySql
	 *            查询操作SQL语句
	 * @return 查询
	 * @throws SQLException
	 */
	public ResultSet query(String querySql) throws SQLException {
		Statement stateMent = (Statement) mConnection.createStatement();
		return stateMent.executeQuery(querySql);
	}

	/**
	 * 插入、更新、删除操作
	 * 
	 * @param insertSql
	 *            插入操作的SQL语句
	 * @return
	 * @throws SQLException
	 */
	public int update(String insertSql) throws SQLException {
		Statement stateMent = (Statement) mConnection.createStatement();
		return stateMent.executeUpdate(insertSql);
	}

	/**
	 * 关闭数据库连接
	 */
	public void closeConnection() {
		if (mConnection != null) {
			try {
				mConnection.close();
				mConnection = null;
			} catch (SQLException e) {
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
			}
		}
	}
}
