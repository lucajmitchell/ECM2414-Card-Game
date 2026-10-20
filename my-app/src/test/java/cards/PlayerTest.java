package cards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;

class PlayerTest {

  @Test
  void playerDiscardsNonPreferredCard() {
    // Setup decks
    CardDeck drawDeck = new CardDeck(1);
    CardDeck discardDeck = new CardDeck(2);
    drawDeck.addCard(new Card(5));

    // Setup player
    GameState gameState = new GameState();
    Player player = new Player(1, drawDeck, discardDeck, gameState);
    player.addCardToHand(new Card(1));
    player.addCardToHand(new Card(1));
    player.addCardToHand(new Card(2));
    player.addCardToHand(new Card(3));

    // Test
    player.takeTurn();
    Card discardedCard = discardDeck.removeCard();
    assertNotEquals(1, discardedCard.getDenomination());
  }

  @Test
  void playerAlwaysHasFourCardsAfterTurn() {
    // Setup decks
    CardDeck drawDeck = new CardDeck(1);
    CardDeck discardDeck = new CardDeck(2);
    drawDeck.addCard(new Card(5));

    // Setup player
    GameState gameState = new GameState();
    Player player = new Player(1, drawDeck, discardDeck, gameState);
    player.addCardToHand(new Card(1));
    player.addCardToHand(new Card(2));
    player.addCardToHand(new Card(3));
    player.addCardToHand(new Card(4));

    // Test
    assertEquals(4, player.getHand().size());
    player.takeTurn();
    assertEquals(4, player.getHand().size());
  }
}