package cards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class CardTest {

  @Test
  void deckFollowsFifoOrder() {
    // Setup deck
    CardDeck deck = new CardDeck(1);
    deck.addCard(new Card(3));
    deck.addCard(new Card(5));
    deck.addCard(new Card(7));

    // Test
    assertEquals(3, deck.removeCard().getDenomination());
    assertEquals(5, deck.removeCard().getDenomination());
    assertEquals(7, deck.removeCard().getDenomination());
  }

  @Test
  void cardsAreConservedAfterPlayerTurn() {
    // Setup decks
    CardDeck drawDeck = new CardDeck(1);
    CardDeck discardDeck = new CardDeck(2);

    drawDeck.addCard(new Card(5));
    drawDeck.addCard(new Card(6));

    // Setup player
    GameState gameState = new GameState();
    Player player = new Player(1, drawDeck, discardDeck, gameState);
    player.addCardToHand(new Card(1));
    player.addCardToHand(new Card(2));
    player.addCardToHand(new Card(3));
    player.addCardToHand(new Card(4));

    // Count cards before and after turn
    int cardsBefore = player.getHand().size()
                    + drawDeck.getCards().size()
                    + discardDeck.getCards().size();
    player.takeTurn();
    int cardsAfter = player.getHand().size()
                    + drawDeck.getCards().size()
                    + discardDeck.getCards().size();

    assertEquals(cardsBefore, cardsAfter);
  }
}