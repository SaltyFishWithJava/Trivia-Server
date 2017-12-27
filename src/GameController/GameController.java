package GameController;

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

	public GameController() {
	}

	/*
	 * Game(int ID,int Gr,User ho,String na) public User(String uN, int sc)
	 */
	synchronized static public boolean addGame(JSONObject js) {
		User user = new User(js.getString("uN"), js.getInt("sc"));
		return game.add(new Game(++RoomCount, js.getInt("mGr"), user, js.getString("mrn")));
	}

	static public Game findGame(int id) {
		for (Game ga : game) {
			if (ga.getID() == id) {
				return ga;
			}
		}
		return null;
	}

	synchronized static public void addPlayer(User pl) {
		if (findPlayer(pl.getUserName()) == null) {
			player.add(pl);
		}
	}

	static public User findPlayer(String pl) {
		for (User user : player) {
			if (user.getUserName().equals(pl)) {
				return user;
			}
		}
		return null;
	}

	static public void getGameList(CommonResponse res) {
		int count = 0;
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
		for (User user : player) {
			HashMap<String, String> gm = new HashMap<String, String>();
			count++;
			user.putInformation(gm);
			gm.put("isFriend", "false");
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
 * TBC.. �ͻ���ÿ��ѯ�ʵ�ʱ��remove��ʱ����ң�����������Ϸ�в��ᱻ�Ƴ����ǻᱻ��ǣ���ʱʱ�䣿 User�����������ʱ������
 */
