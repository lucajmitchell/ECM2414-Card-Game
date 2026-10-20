package cards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class GameTest {

  @Test
  void playerWithInitialWinningHandWinsImmediately() {
    // Setup decks
    CardDeck drawDeck = new CardDeck(1);
    CardDeck discardDeck = new CardDeck(2);

    // Setup player with initial win
    GameState gameState = new GameState();
    Player player = new Player(1, drawDeck, discardDeck, gameState);
    player.addCardToHand(new Card(5));
    player.addCardToHand(new Card(5));
    player.addCardToHand(new Card(5));
    player.addCardToHand(new Card(5));
    gameState.declareWinner(1);

    // Test
    assertTrue(player.hasWon());
    assertEquals(1, gameState.getWinner());
  }

  @Test
  void allPlayerThreadsTerminateAfterWinnerIsDeclared() throws InterruptedException {
    // Setup decks
    CardDeck deck1 = new CardDeck(1);
    CardDeck deck2 = new CardDeck(2);

    // Setup players
    GameState gameState = new GameState();
    Player player1 = new Player(1, deck1, deck2, gameState);
    Player player2 = new Player(2, deck2, deck1, gameState);
    Thread thread1 = new Thread(player1);
    Thread thread2 = new Thread(player2);

    // Start player threads
    thread1.start();
    thread2.start();

    // Finish player threads
    gameState.declareWinner(1);
    thread1.join();
    thread2.join();

    assertFalse(thread1.isAlive());
    assertFalse(thread2.isAlive());
  }
}