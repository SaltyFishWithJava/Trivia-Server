package GameController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArraySet;

import Entity.Game;
import Entity.Player;
import Entity.User;
import Tools.CommonResponse;
import Tools.DBQuery;
import Tools.DatabaseUtil;

public class GameLogic {
	private Game game;
	private Random random;
	private int[] Grid;
	final private int GRID_COUNT;
	private int now_player;
	private Player[] player;
	private DatabaseUtil db;
	private LogicState ls;
	private Question question;
	static private CopyOnWriteArraySet<Player> player_ing = new CopyOnWriteArraySet<Player>();

	public GameLogic(Game ga) {
		db = new DatabaseUtil();
		this.now_player = 0;
		this.game = ga;
		this.random = new Random();
		this.GRID_COUNT = 12;
		this.Grid = new int[this.GRID_COUNT];
		CreateGrid();
		question = new Question();
		this.ls = LogicState.GOING;
	}

	static public void addPlayingUser(Player pa) {
		player_ing.add(pa);
	}

	static public void removePlayingUser(User pa) {
		player_ing.remove(pa);
	}

	public void putGameInfor(CommonResponse res, boolean isGrid) {
		res.setResMsg("GameInfor");
		res.getProperty().put("activePlayer", player[now_player].getUserName());
		res.getProperty().put("GameState", ls.toString());
		if (isGrid) {
			for (int i = 0; i < GRID_COUNT; i++) {
				res.getProperty().put("Grid" + i, Grid[i] + "");
			}
		}
		for (Player pl : player) {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("uN", pl.getUserName());
			hm.put("coin", pl.getCoin() + "");
			hm.put("jail", pl.getJail() ? "1" : "0");
			hm.put("active", pl.getActive() ? "1" : "0");
			hm.put("step", (pl.getStep() + 1) + "");
			res.addListItem(hm);
		}
		res.setResCode(player.length + "");
	}

	public void setPlayer(Player[] pa) {
		player = pa;
	}

	public Game getGame() {
		return game;
	}

	public int Roll(int bound) {
		int ran = random.nextInt(bound) + 1;
		System.out.println("Roll:" + ran);
		return ran;
	}

	public void CreateGrid() {
		int MAX = 3;
		int[] tg = { 0, 0, 0, 0 };
		for (int i = 0; i < 12; i++) {
			int temp = Roll(4) - 1;
			while (tg[temp] >= 3) {
				temp = Roll(4) - 1;
			}
			tg[temp]++;
			Grid[i] = temp + 1;
		}
	}

	public int nextIndex() {
		now_player = (now_player + 1) % player.length;
		return now_player;
	}

	public void PrintGrid() {
		for (int i = 0; i < 12; i++) {
			System.out.print(Grid[i] + " ");
		}
		System.out.println("");
	}

	public boolean isRight(String answer) {
		return question.checkAnswer(answer);
	}

	public int go(int st) {
		return player[now_player].judge(GRID_COUNT, st);
	}

	public int getPosition(int user) {
		return (player[user].getStep() + 1);
	}

	synchronized public void getQuestion(boolean isOk, CommonResponse response) {
		if (isOk) {
			try {
				int step = player[now_player].getStep();
				ResultSet res = db.query(DBQuery.FindQuestion(Grid[step]));
				if (res.next()) {
					System.out.println(res.getString("ID") + ":" + res.getString("description"));
					System.out.println("A:" + res.getString("A"));
					System.out.println("B:" + res.getString("B"));
					System.out.println("C:" + res.getString("C"));
					System.out.println("D:" + res.getString("D"));
					question.setDescription(res.getString("description"));
					question.setA(res.getString("A"));
					question.setB(res.getString("B"));
					question.setC(res.getString("C"));
					question.setD(res.getString("D"));
					question.setAnswer(res.getString("answer"));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			question.setDescription("");
			question.setA("");
			question.setB("");
			question.setC("");
			question.setD("");
			question.setAnswer("");
		}
		putGameInfor(response, false);
		question.setQuestion(response);
	}

	public boolean stopGame(CommonResponse res) {
		for (int i = 0; i < player.length; i++) {
			if (player[i].win()) {

				stopInfor(res);
				db.closeConnection();
				return true;
			}
		}
		res.setResMsg("GO");
		return false;
	}

	public void rollAndUpdate(CommonResponse res) {
		int roll = Roll(6);
		int result = go(roll);

		res.getProperty().put("Roll", roll + "");
		if (result == 0) {
			res.getProperty().put("behavior", "Go");
			this.ls = LogicState.QUESTION;
			getQuestion(true, res);

		} else if (result == 1) {
			res.getProperty().put("behavior", "Out");
			nextIndex();
			getQuestion(false, res);
		} else if (result == 2) {
			res.getProperty().put("behavior", "In");
			nextIndex();
			getQuestion(false, res);
		}
		res.setResMsg("Roll");
	}

	synchronized public void Check(CommonResponse res, String ans) {
		/*
		 * Test:ans = A
		 */
		this.ls = LogicState.GOING;
		if (question.checkAnswer(ans)) {
			res.getProperty().put("answerRes", "right");
			player[now_player].winCoin();
			System.out.println("check");
			if (stopGame(res)) {
				return;
			}
		} else {
			res.getProperty().put("answerRes", "wrong");
			player[now_player].in();
		}
		nextIndex();
		putGameInfor(res, false);
	}

	public void stopInfor(CommonResponse res) {
		res.setResMsg("End");
		Arrays.sort(player, new PlayerComparator());
		for (int i = 0; i < player.length; i++) {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put(player[i].getUserName(), player[i].getCoin() + "");
			res.addListItem(hm);
			int score_ = player[i].getCoin() * (4 - i);
			player[i].setScore(player[i].getScore() + score_);
			try {
				db.update(DBQuery.UpdateScore(player[i].getUserName(), score_));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	static public Player findPlayer(User user) {
		for (Player p : player_ing) {
			if (p.equals(user)) {
				return p;
			}
		}
		return null;
	}

	class Question {
		private String description;
		private String A;
		private String B;
		private String C;
		private String D;
		private String answer;

		public boolean checkAnswer(String ans) {
			return ans.equals(/*
								 * Test:ans = A
								 */"A");
		}

		public String getDescription() {
			return this.description;
		}

		public String getA() {
			return this.A;
		}

		public String getB() {
			return this.B;
		}

		public String getC() {
			return this.C;
		}

		public String getD() {
			return this.D;
		}

		public void setA(String a) {
			this.A = a;
		}

		public void setB(String b) {
			this.B = b;
		}

		public void setC(String c) {
			this.C = c;
		}

		public void setD(String d) {
			this.D = d;
		}

		public void setDescription(String des) {
			this.description = des;
		}

		public void setAnswer(String ans) {
			this.answer = ans;
		}

		synchronized public void setQuestion(CommonResponse res) {
			res.getProperty().put("description", this.description);
			res.getProperty().put("A", this.A);
			res.getProperty().put("B", this.B);
			res.getProperty().put("C", this.C);
			res.getProperty().put("D", this.D);
			res.getProperty().put("answer", this.answer);
		}
	}

	public static void main(String[] args) {
		/*
		 * public Game(int ID, int Gr, User ho, String na) {
		 */
		/*
		 * User user1 = new User("XiaoMing", 0, 0); User user2 = new
		 * User("XiaoHong", 100, 0); User user3 = new User("XiaoGang", -5, 0);
		 * User user4 = new User("Harry", 20, 0); Game game = new Game(1, 12,
		 * user1, "Test1"); game.addPlayer(user2); game.addPlayer(user3);
		 * game.addPlayer(user4); GameLogic gl = game.startGame();
		 * System.out.println("TestMain"); if (gl == null) {
		 * System.out.println("NULL"); }
		 * 
		 * user2.offLine(); for (Player pa : player_ing) { pa.getActive(); }
		 * 
		 * game.stopGame(); for (Player pa : player_ing) {
		 * System.out.println(pa.getUserName()); }
		 */
		/*
		 * Scanner in = new Scanner(System.in); System.out.println("Grid:");
		 * gl.PrintGrid(); while (!gl.stopGame()) { for (int i = 0; i <
		 * gl.player.length; i++) { System.out.println("Player" + i + ":" +
		 * gl.player[i].getUserName()); System.out.println("Coin:" + i + ":" +
		 * gl.player[i].getCoin()); } System.out.println("NOW:" + (gl.now_player
		 * + 1)); int result = gl.go(gl.now_player, gl.Roll(6)); if (result ==
		 * 1) { System.out.println("OUT"); } else if (result == 2) {
		 * System.out.println("Sad"); } else { int pos =
		 * gl.getPosition(gl.now_player); System.out.println("GO TO:" + pos);
		 * gl.getQuestion(pos - 1); String ans = in.next(); if (gl.isRight(ans))
		 * { System.out.println("Y"); gl.player[gl.now_player].winCoin(); } else
		 * { System.out.println("N"); gl.player[gl.now_player].in();
		 * System.out.println("IN"); } } gl.nextIndex(); }
		 */
	}

	enum LogicState {
		GOING("GOING", 0), ROLL("ROLL", 1), QUESTION("QUESTION", 2);
		private String _name;
		private int _id;

		LogicState(String name, int id) {
			_name = name;
			_id = id;
		}

		public String toString() {
			return _name;
		}

		public boolean equals(LogicState s) {
			return this._id == s._id;
		}
	}

	class PlayerComparator implements Comparator {
		public int compare(Object arg0, Object arg1) {
			Player p0 = (Player) arg0;
			Player p1 = (Player) arg1;
			return p0.getCoin() < p1.getCoin() ? 1 : -1;
		}
	}
}
