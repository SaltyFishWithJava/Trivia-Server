package Tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

public class DatabaseUtil {

	private Connection mConnection;

	/**
	 * ��ȡ���ݿ�����
	 * 
	 * @return Ψһ���ݿ�����
	 */
	public DatabaseUtil() {
		mConnection = null;
		getConnection();
	}

	private Connection getConnection() {
		if (mConnection == null) {
			String url = "jdbc:mysql://59b6aed4552fc.sh.cdb.myqcloud.com:5244/Trivia"; // ���ݿ��Url
			try {
				Class.forName("com.mysql.jdbc.Driver"); // java���䣬�̶�д��
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
	 * ��ѯ����
	 * 
	 * @param querySql
	 *            ��ѯ����SQL���
	 * @return ��ѯ
	 * @throws SQLException
	 */
	public ResultSet query(String querySql) throws SQLException {
		Statement stateMent = (Statement) mConnection.createStatement();
		return stateMent.executeQuery(querySql);
	}

	/**
	 * ���롢���¡�ɾ������
	 * 
	 * @param insertSql
	 *            ���������SQL���
	 * @return
	 * @throws SQLException
	 */
	public int update(String insertSql) throws SQLException {
		Statement stateMent = (Statement) mConnection.createStatement();
		return stateMent.executeUpdate(insertSql);
	}

	/**
	 * �ر����ݿ�����
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
