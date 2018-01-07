package Entity;

public class Player extends User {
	private int step;
	private int coin;
	private boolean win;
	private boolean jail;
	private boolean active;

	public Player(String uN, int sc, int i) {
		super(uN, sc, i);
		step = -1;
		coin = 5;
		win = false;
		active = true;
		jail = false;
	}

	public void online() {
		active = true;
	}

	public void offline() {
		active = false;
	}

	public boolean getJail() {
		return jail;
	}

	public boolean getActive() {
		if (active) {
			System.out.println("Online");
			return true;
		} else {
			System.out.println("Offline");
			return false;
		}
	}

	public void out() {
		jail = false;
	}

	public void in() {
		jail = true;
	}

	public int judge(int grid, int st) {
		if (jail) {
			if (st % 2 == 0) {
				out();
				return 1;
			}
			return 2;
		} else {
			go(grid, st);
			return 0;
		}
	}

	public void go(int grid, int st) {
		step = (step + st) % grid;
	}

	synchronized public void winCoin() {
		coin++;
		if (coin == 6) {
			win = true;
		}
	}

	public boolean win() {
		return win;
	}

	public int getCoin() {
		return coin;
	}

	public int getStep() {
		return step;
	}
}
