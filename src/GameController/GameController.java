package GameController;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import Entity.Game;
import Entity.User;
import Tools.CommonResponse;
import net.sf.json.JSONObject;

public class GameController {
	static CopyOnWriteArraySet<User> player = new CopyOnWriteArraySet<User>();

	static int RoomCount = 0;
	static CopyOnWriteArraySet<Game> game = new CopyOnWriteArraySet<Game>();

	static CopyOnWriteArraySet<GameLogic> playing = new CopyOnWriteArraySet<GameLogic>();

	public GameController() {
	}

	/*
	 * hh Game(int ID,int Gr,User ho,String na) public User(String uN, int sc)
	 */
	synchronized static public boolean addGame(JSONObject js, User user) {
		if (game.add(new Game(RoomCount + 1, 12, user, js.getString("mrn")))) {
			RoomCount++;
			return true;
		}
		return false;
	}

	synchronized static public boolean addGame_(Game ga) {
		if (game.add(ga)) {
			RoomCount++;
			return true;
		}
		return false;
	}

	synchronized static public boolean removePlayer(User user) {
		return player.remove(user);
	}

	static public boolean addPlayingGame(GameLogic game) {
		return playing.add(game);
	}

	static public boolean removePlayingGame(String game) {
		for (GameLogic gl : playing) {
			if (gl.getGame().getName().equals(game)) {
				return playing.remove(gl);
			}
		}
		return false;
	}

	static public GameLogic findPlayingGame(String mga) {
		for (GameLogic ga : playing) {
			if (ga.getGame().getName().equals(mga)) {
				return ga;
			}
		}
		return null;
	}

	synchronized static public int joinGame(JSONObject js, User user) {
		Game mg = findGame(js.getString("gN"));
		return mg.addPlayer(user);
	}

	static public Game findGame(String mga) {
		for (Game ga : game) {
			if (ga.getName().equals(mga)) {
				return ga;
			}
		}
		return null;
	}

	static public boolean addPlayer(User pl) {
		return player.add(pl);
	}

	static public User findPlayer(String na) {
		for (User user : player) {
			if (user.getUserName().equals(na)) {
				return user;
			}
		}
		return null;
	}

	static public void getGameList(CommonResponse res) {
		int count = 0;
		/*
		 * TestGame
		 */
		/*
		 * for (int i = 0; game.size() < 20 && i < 20; i++) { Game e = new
		 * Game(i, 12, new User("test" + i, 0, (i % 4) + 1), "t" + i);
		 * game.add(e); }
		 */
		for (Game ga : game) {
			HashMap<String, String> gm = new HashMap<String, String>();
			count++;
			ga.putInformation(gm);
			res.addListItem(gm);
		}
		res.setResCode(count + "");
		res.setResMsg("RoomList");
	}

	static public void getPlayerList(String userName, CommonResponse res) {
		int count = 0;
		Date date = new Date();
		long nowdate = date.getTime();
		// while (player.size() < 20) {
		// player.add(new User("player" + player.size(), 0, (player.size() % 4)
		// + 1));
		// }
		for (User user : player) {
			if (!user.checkOnLine()) {
				continue;
			}
			HashMap<String, String> gm = new HashMap<String, String>();
			count++;
			user.putInformation(gm);
			res.addListItem(gm);
		}
		res.setResCode(count + "");
		res.setResMsg("PlayerList");
	}

	static public CopyOnWriteArraySet<User> getPlayers() {
		return player;
	}

}

/*
 * TBC.. 客户端每次询问的时候remove超时的玩家，如果玩家在游戏中不会被移除但是会被标记，超时时间？ User类添加最后访问时间属性
 */
