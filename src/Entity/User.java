package Entity;

import java.util.HashMap;

import Tools.CommonResponse;

public class User {
	private String userName;
	private int score;
	private State gameState;

	public State getState() {
		return gameState;
	}

	synchronized public void setState(State gs) {
		gameState = gs;
	}

	public User(String uN, int sc) {
		userName = uN;
		score = sc;
		gameState = State.IDLE;
	}

	public void putInformation(HashMap<String, String> hm) {
		hm.put("uN", userName);
		hm.put("sc", score + "");
	}

	public void putInforAlone(CommonResponse res) {
		res.getProperty().put("uN", userName);
		res.getProperty().put("sc", score + "");
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

	public enum State {
		WAITING("WAITING", 0), PLAYING("PLAYING", 1), IDLE("FULL", 2);
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
