package Entity;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class Game {
	private int ID;
	private String name = null;
	private User host = null;
	private CopyOnWriteArraySet<User> players = null;
	private final int MAX_COUNT = 4;
	private GameState gs;
	private int Grid;

	public Game(int ID, int Gr, User ho, String na) {
		this.name = na;
		this.host = ho;
		this.Grid = Gr;
		this.gs = GameState.WAITING;
		this.ID = ID;
		players = new CopyOnWriteArraySet<User>();
		this.addPlayer(ho);
	}

	public int getID() {
		return ID;
	}

	public CopyOnWriteArraySet<User> getPlayers() {
		return players;
	}

	public void putInformation(HashMap<String, String> hm) {
		hm.put("ID", ID + "");
		hm.put("PlrCo", players.size() + "");
		hm.put("State", gs.toString());
		hm.put("host", host.getUserName());
		hm.put("name", name);
		hm.put("Grid", Grid + "");
	}

	synchronized boolean addPlayer(User user) {
		if (players.size() < MAX_COUNT) {
			players.add(user);
			if (players.size() == MAX_COUNT) {
				this.gs = GameState.FULL;
			}
			return true;
		}
		return false;
	}

	synchronized boolean removePlayer(User user) {
		if (players.size() > 0) {
			if (players.remove(user)) {
				if (players.size() == MAX_COUNT - 1) {
					this.gs = GameState.WAITING;
				} else if (players.size() == 0) {
					this.gs = GameState.DESTORY;
				}
				return true;
			}
		}
		return false;
	}

	enum GameState {
		WAITING("WAITING", 0), PLAYING("PLAYING", 1), FULL("FULL", 2), DESTORY("DESTORY", 3);
		private String _name;
		private int _id;

		GameState(String name, int id) {
			_name = name;
			_id = id;
		}

		public String toString() {
			return _name;
		}

		public boolean equals(GameState s) {
			return this._id == s._id;
		}

	}

}
