package Entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import GameController.GameController;
import GameController.GameLogic;
import Tools.CommonResponse;

public class User {
	private String userName;
	private int score;
	private State gameState;
	private int avatar;
	private long lastReadTime;
	private boolean loginS;

	public State getState() {
		return gameState;
	}

	synchronized public void setState(State gs) {
		gameState = gs;
	}

	/*
	 * TestConsturctor
	 */
	public User(String uN, int sc, int i) {
		setLoginS(false);
		userName = uN;
		score = sc;
		gameState = State.IDLE;
		Random random = new Random();
		avatar = i;
		Date date = new Date();
		this.setLastReadTime();
	}

	synchronized public int getAvatar() {
		return avatar;
	}

	synchronized public boolean checkOnLine() {
		Date date = new Date();
		long diff = ((date.getTime() - lastReadTime) / 1000);
		// System.out.println("dif::" + diff);
		if ((loginS && diff < 10) || (!loginS && diff < 40)) {
			return true;
		} else {
			this.offLine();
			GameController.removePlayer(this);
			return false;
		}
	}

	synchronized public void offLine() {
		Player p = GameLogic.findPlayer(this);
		if (p != null) {
			p.offline();
		}
	}

	synchronized public long getLastReadTime() {
		return lastReadTime;
	}

	synchronized public void setLastReadTime() {
		Date d = new Date();
		lastReadTime = d.getTime();
	}

	public void putInformation(HashMap<String, String> hm) {
		hm.put("uN", userName);
		hm.put("sc", score + "");
		hm.put("avatar", avatar + "");
		hm.put("State", gameState.toString());
	}

	public void putInforAlone(CommonResponse res) {
		res.getProperty().put("uN", userName);
		res.getProperty().put("sc", score + "");
		res.getProperty().put("avatar", avatar + "");
	}

	@Override
	public boolean equals(Object player) {
		User user = (User) player;
		return user.userName.equals(this.userName);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public boolean getLoginS() {
		return loginS;
	}

	public void setLoginS(boolean loginS) {
		this.loginS = loginS;
	}

	public enum State {
		WAITING("WAITING", 0), PLAYING("PLAYING", 1), IDLE("IDLE", 2), READY("READY", 3);
		private String _name;
		private int _id;

		State(String name, int id) {
			_name = name;
			_id = id;
		}

		public String toString() {
			return _name;
		}

		public boolean equals(State s) {
			return this._id == s._id;
		}
	}
}
