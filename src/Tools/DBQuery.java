package Tools;

import java.text.SimpleDateFormat;
import java.util.Date;
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

	static public String UpdateGame(String p1, String p2, String p3, String p4, String s1, String s2, String s3,
			String s4) {
		return String.format(
				"INSERT INTO Games(Time , Player1 ,Player2, Player3, Player4 , score1, score2 ,score3 ,score4) VALUES( '%s','%s','%s','%s','%s',%s,%s,%s,%s )",
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), p1, p2, p3, p4, s1, s2, s3, s4);
	}
}
