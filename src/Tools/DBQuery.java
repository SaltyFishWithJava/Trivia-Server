package Tools;

import java.util.Random;

public class DBQuery {
	static Random random = new Random();

	static public String Login(String uN, String pW) {
		return String.format("SELECT * FROM User WHERE userName= '%s' and password = '%s'", uN, pW);
	}

	static public String FindUser(String uN) {
		return String.format("SELECT * FROM User WHERE userName= '%s'", uN);
	}

	static public String Reg(String uN, String pW) {
		return String.format("INSERT INTO User(userName,password,score,friendNumber,avatar) VALUES('%s','%s',0,0,%s)",
				uN, pW, random.nextInt(4) + 1);
	}

	static public String FindQuestion(int id) {
		return String.format(
				"SELECT * FROM questions_%s AS t1 JOIN (SELECT ROUND(RAND() * (SELECT MAX(id) FROM questions_%s)) AS id) AS t2 WHERE t1.id >= t2.id ORDER BY t1.id ASC LIMIT 1;",
				id + "", id + "");
	}

	static public String UpdateScore(String uN, int coin) {
		return String.format("UPDATE User SET score = score + %s WHERE userName = '%s'", coin + "", uN);
	}

}
