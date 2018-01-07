package Entity;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.Session;

import Entity.User.State;
import GameController.GameController;
import GameController.GameLogic;
import Tools.CommonResponse;

public class Game {
	private int ID;
	private String name = null;
	private User host = null;
	private CopyOnWriteArraySet<User> players = null;
	private final int MAX_COUNT = 4;
	private GameState gs;
	private int Grid;
	private int NOW_COUNT;
	private ConcurrentHashMap<String, Session> SessionMap = new ConcurrentHashMap<String, Session>();
	private int avatar;

	public Game(int ID, int Gr, User ho, String na) {
		this.NOW_COUNT = 1;
		this.name = na;
		this.host = ho;
		this.Grid = Gr;
		this.gs = GameState.WAITING;
		this.ID = ID;
		players = new CopyOnWriteArraySet<User>();
		this.addPlayer(ho);
		this.avatar = this.host.getAvatar();

	}

	public void removeSession(String uN) {
		SessionMap.remove(SessionMap.get("uN"));
	}

	public ConcurrentHashMap<String, Session> getSessionMap() {
		return SessionMap;
	}

	public Session getSession(String uN) {
		return SessionMap.get("uN");
	}

	public void addSession(String uN, Session session) {
		SessionMap.put(uN, session);
	}

	public String getName() {
		return name;
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
		hm.put("Avatar", avatar + "");
	}

	public void putInformation_(CommonResponse res) {
		res.getProperty().put("PlrCo", players.size() + "");
		res.getProperty().put("host", host.getUserName());
		res.getProperty().put("name", name);
		for (User user : players) {
			HashMap<String, String> hm = new HashMap<String, String>();
			user.putInformation(hm);
			res.addListItem(hm);
		}
	}

	synchronized public int addPlayer(User user) {
		if (gs.equals(Game.GameState.WAITING)) {
			if (players.add(user)) {
				if (players.size() == MAX_COUNT) {
					this.gs = GameState.FULL;
				}
				user.setState(State.WAITING);
				NOW_COUNT = players.size();
				return 0;// 加入成功
			}
			return 1;// 不能重复加入房间
		}
		return 2;// 房间已满或开始
	}

	public User findPlayer(String na) {
		for (User user : players) {
			if (user.getUserName().equals(na)) {
				return user;
			}
		}
		return null;
	}

	synchronized boolean removePlayer(User user) {
		if (players.size() > 0) {
			if (players.remove(user)) {
				if (user.equals(host)) {
					host = players.iterator().next();
				}
				if (players.size() < MAX_COUNT) {
					this.gs = GameState.WAITING;
				} else if (players.size() == 0) {
					this.gs = GameState.DESTORY;
				}
				return true;
			}
		}
		return false;
	}

	public Player[] getMyplayer() {
		Player[] sp = new Player[players.size()];
		int i = 0;
		for (User user : players) {
			Player myp = new Player(user.getUserName(), user.getScore(), user.getAvatar());
			sp[i++] = myp;
			GameLogic.addPlayingUser(myp);
		}
		return sp;
	}

	synchronized public GameLogic startGame(String uN) {
		if (!uN.equals(this.host.getUserName())) {
			System.out.println("1");
			return null;
		}
		this.host.setState(State.READY);
		for (User user : players) {
			if (!user.getState().equals(State.READY)) {
				this.host.setState(State.WAITING);
				return null;
			}
		}
		for (User user : players) {
			user.setState(State.PLAYING);
		}
		gs = GameState.PLAYING;
		GameLogic gamelogic = new GameLogic(this);
		gamelogic.setPlayer(getMyplayer());
		GameController.addPlayingGame(gamelogic);
		return gamelogic;
	}

	synchronized public void stopGame() {
		if (NOW_COUNT < MAX_COUNT) {
			gs = GameState.WAITING;
		} else {
			gs = GameState.FULL;
		}
		for (User user : players) {
			User us = GameController.findPlayer(user.getUserName());
			if (us != null) {
				us.setScore(user.getScore());
				us.setState(State.WAITING);
				GameLogic.removePlayingUser(user);
			}
		}
		GameController.removePlayingGame(name);
		players.clear();
		SessionMap.clear();
		this.host = null;
	}

	public User getHost() {
		return host;
	}

	public void setHost(User user) {
		this.host = user;
		players.add(user);
		this.host.setState(State.WAITING);
	}

	@Override
	public boolean equals(Object game) {
		Game ga = (Game) game;
		return ga.name.equals(this.name);
	}

	synchronized public int getNowCount() {
		return NOW_COUNT;
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
