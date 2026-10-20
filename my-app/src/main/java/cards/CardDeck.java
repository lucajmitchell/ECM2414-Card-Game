package cards;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class CardDeck {

  private final int deckNumber;
  private final Queue<Card> cards;

  public CardDeck(int deckNumber) {
    if (deckNumber < 1) {
      throw new IllegalArgumentException("Deck number must be at least 1.");
    }
    this.deckNumber = deckNumber;
    this.cards = new ArrayDeque();
  }

  public synchronized int getDeckNumber() {
    return deckNumber;
  }

  public synchronized void addCard(Card card) {
    cards.add(card);
  }

  public synchronized Card removeCard() {
    Card card = cards.poll();
    return card;
  }

  public synchronized List<Card> getCards() {
    return new ArrayList(cards);
  }

  @Override
  public synchronized String toString() {
    StringBuilder result = new StringBuilder();

    for (Card card : cards) {
      if (result.length() > 0) {
        result.append(" ");
      }
      result.append(card.getDenomination());
    }

    return result.toString();
  }

}