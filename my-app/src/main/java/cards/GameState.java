package cards;

import java.util.concurrent.atomic.AtomicInteger;

public class GameState {
	private final AtomicInteger winner = new AtomicInteger(0);

	public void declareWinner(int playerNumber) {
		winner.set(playerNumber);
	}

	public boolean isGameOver() {
		return winner.get() != 0;
	}

	public int getWinner() {
		return winner.get();
	}
}
