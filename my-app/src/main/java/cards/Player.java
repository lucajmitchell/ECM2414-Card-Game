package cards;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Player implements Runnable {

  private final int playerNumber;
  private final CardDeck drawDeck;
  private final CardDeck discardDeck;
  private final int preferredDenomination;
  private final ArrayList<Card> hand;
  private final GameState gameState;

  public Player(int playerNumber, CardDeck drawDeck, CardDeck discardDeck, GameState gameState) {
    this.playerNumber = playerNumber;
    this.drawDeck = drawDeck;
    this.discardDeck = discardDeck;
    this.preferredDenomination = playerNumber;
    this.hand = new ArrayList<>();
    this.gameState = gameState;
  }

  public int getPlayerNumber() {
    return playerNumber;
  }

  public ArrayList<Card> getHand() {
    return hand;
  }

  public void addCardToHand(Card card) {
    hand.add(card);
  }

  // Write initial hand to output file
  public void writeInitialHand() {
    String fileName = "player" + playerNumber + "_output.txt";

    try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
      writer.print("player " + playerNumber + " initial hand");
      for (Card card : hand) {
        writer.print(" " + card);
      }
      writer.println();
    } catch (IOException e) {
      System.out.println("Error writing player file: " + e.getMessage());
    }
  }

  // Write message to file
  public void logAction(String message) {
    String fileName = "player" + playerNumber + "_output.txt";

    try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
      writer.println(message);
    } catch (IOException e) {
      System.out.println("Error writing player log: " + e.getMessage());
    }
  }

  // Checks for win (all cards are same denomination)
  public boolean hasWon() {
    if (hand.size() != 4) {
      return false;
    }

    int firstDenomination = hand.get(0).getDenomination();
    for (Card card : hand) {
      if (card.getDenomination() != firstDenomination) {
        return false;
      }
    }

    return true;
  }

  // Get hand in string form (example result: 1 2 3 4)
  public String getHandAsString() {
    StringBuilder result = new StringBuilder();

    for (Card card : hand) {
      if (result.length() > 0) {
        result.append(" ");
      }
      result.append(card);
    }

    return result.toString();
  }

  // Removes drawn card from draw deck and adds card to hand
  public Card draw() {
    Card drawnCard = drawDeck.removeCard();
    addCardToHand(drawnCard);
    return drawnCard;
  }

  // Adds discarded card to discard deck and removes card from hand
  public Card discard() {
    Card cardToDiscard = null;
    int indexToDiscard = -1;

    for (int i=0; i<hand.size(); i++) {
      Card currentCard = hand.get(i);
      if (currentCard.getDenomination() != preferredDenomination) {
        cardToDiscard = currentCard;
        indexToDiscard = i;
        break;
      }
    }

    discardDeck.addCard(cardToDiscard);
    hand.remove(indexToDiscard);

    return cardToDiscard;
  }
  
  // Take turn:
  // 1. Draw card
  // 2. Discard card
  // 3. Log player's hand at end of turn
  public void takeTurn() {
    Card drawnCard = draw();
    logAction(
      "player " + playerNumber +
      " draws a " + drawnCard +
      " from deck " + drawDeck.getDeckNumber()
    );

    Card discardedCard = discard();
    logAction(
      "player " + playerNumber +
      " discards a " + discardedCard +
      " to deck " + discardDeck.getDeckNumber()
    );

    logAction(
      "player " + playerNumber +
      " current hand is " + getHandAsString()
    );
  }

  // Code thread will execute
  @Override
  public void run() {
    System.out.println("Player " + playerNumber + " started.");
    writeInitialHand();

    while (!gameState.isGameOver()) {
      // Check whether this player has won
      if (hasWon()) {
          gameState.declareWinner(playerNumber);
          break;
      }

      // Take a turn
      takeTurn();

      // Check whether this player has won after their turn
      if (hasWon()) {
          gameState.declareWinner(playerNumber);
          break;
      }
    }

    if (gameState.isGameOver()) {
      System.out.println("Player " + playerNumber + " sees that the game has ended.");
      logAction("player " + gameState.getWinner() + " wins");
      logAction("player " + playerNumber + " exits");
      logAction("player " + playerNumber + " final hand: " + getHandAsString());
    }

    System.out.println("Player " + playerNumber + " finished.");
  }
}