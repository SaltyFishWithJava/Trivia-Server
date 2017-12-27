package Tools;

public class DBQuery {
	static public String Login(String uN, String pW) {
		return String.format("SELECT * FROM User WHERE userName= '%s' and password = '%s'", uN, pW);
	}

	static public String FindUser(String uN) {
		return String.format("SELECT * FROM User WHERE userName= '%s'", uN);
	}

	static public String Reg(String uN, String pW) {
		return String.format("INSERT INTO User(userName,password,score,friendNumber) VALUES('%s','%s',0,0)", uN, pW);
	}

}
