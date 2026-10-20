package cards;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CardGame {
  public static void main(String[] args) {
    int n = requestNumberOfPlayers();
    List<Card> pack = requestPack(n);
    CardGame game = new CardGame(n, pack);

    try {
      game.initialiseGame();
      game.startGame();
      game.writeDeckOutputFiles();
    } catch (IOException e) {
      System.err.println("Error: " + e.getMessage());
    }
  }

  private final int numberOfPlayers;
  private final List<Card> pack;
  private final List<Player> players;
  private final List<CardDeck> decks;
  private final GameState gameState;

  public CardGame(int numberOfPlayers, List<Card> pack) {
    this.numberOfPlayers = numberOfPlayers;
    this.pack = pack;
    this.players = new ArrayList<>();
    this.decks = new ArrayList<>();
    this.gameState = new GameState();
  }

  // Request valid number of players
  private static int requestNumberOfPlayers() {
    Scanner scanner = new Scanner(System.in);

    while (true) {
      // Get input
      System.out.print("Enter the number of players: ");
      String input = scanner.nextLine();

      // Return if valid, continue to next step if not
      try {
        int n = Integer.parseInt(input);
        if (n > 0) {
          return n;
        }
      } catch (NumberFormatException ignored) {}

      // Show try again message and request input again
      System.out.println(
        "Invalid number of players. Please try again."
      );
    }
  }

  // Request pack file
  private static List<Card> requestPack(int n) {
    Scanner scanner = new Scanner(System.in);

    while (true) {
      // Get input
      System.out.print("Enter the pack file path: ");
      String filename = scanner.nextLine().trim();

      // Try to load pack from file, request input again if fails
      try {
        return loadPack(filename, n);
      } catch (IOException | IllegalArgumentException e) {
        System.out.println(
          "Invalid pack: " + e.getMessage()
        );
      }
    }
  }

  // Create pack list from pack file
  private static List<Card> loadPack(String filename, int n) throws IOException {
    List<Card> cards = new ArrayList<>();
    int expectedCards = 8 * n;

    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
      String line;
      int lineNumber = 0;

      // While line is not null
      while ((line = reader.readLine()) != null) {
        lineNumber++;

        // Check for empty line
        if (line.trim().isEmpty()) {
          throw new IllegalArgumentException("Blank line at line " + lineNumber);
        }

        // Try convert line to integer
        try {
          int value = Integer.parseInt(line.trim());
          if (value < 0) {
            throw new IllegalArgumentException("Negative value at line " + lineNumber);
          }

          // Add card to deck if successful
          cards.add(new Card(value));

        } catch (NumberFormatException e) {
          throw new IllegalArgumentException(
            "Invalid integer at line " + lineNumber
          );
        }
      }
    }

    // Check if number of cards is valid (8n cards)
    if (cards.size() != expectedCards) {
        throw new IllegalArgumentException(
            "Expected " + expectedCards +
            " cards, but found " + cards.size()
        );
    }

    return cards;
  }

  // Initialise game
  // 1. Create decks
  // 2. Create Players
  // 3. Deal player hands
  // 4. Fill decks
  private void initialiseGame() {
    // Create decks
    for (int i = 1; i <= numberOfPlayers; i++) {
      decks.add(new CardDeck(i));
    }

    // Create players
    for (int i = 0; i < numberOfPlayers; i++) {
      CardDeck drawDeck = decks.get(i);
      CardDeck discardDeck = decks.get((i+1) % decks.size());
      Player player = new Player(i, drawDeck, discardDeck, gameState);
      players.add(player);
    }

    // Deal four rounds (1 card per player per round)
    int cardIndex = 0;
    for (int round = 0; round < 4; round++) {
      for (Player player : players) {
        Card dealtCard = pack.get(cardIndex++);
        player.addCardToHand(dealtCard);
      }
    }

    // Deal remaining cards to each player's deck in turn
    while (cardIndex < pack.size()) {
      for (int player = 0; player < numberOfPlayers; player++) {
        if (cardIndex >= pack.size()) {
          break;
        } else {
          Card card = pack.get(cardIndex);
          decks.get(player).addCard(card);
          cardIndex++;
        }
      }
    }
  }

  private void startGame() throws IOException {
    // Check for winner before starting game
    for (Player player : players) {
      if (player.hasWon()) {
        gameState.declareWinner(player.getPlayerNumber());
        break;
      }
    }

    // If there is an initial winner
    if (gameState.getWinner() != 0) {
      System.out.println("Player " + gameState.getWinner() + " wins");
      for (Player player : players) {
        player.writeInitialHand();
      }
      return;
    }

    // Start thread for each player
    List<Thread> threads = new ArrayList<>();
    for (Player player : players) {
      Thread thread = new Thread(
        player,
        "Player-" + player.getPlayerNumber()
      );
      threads.add(thread);
      thread.start();
    }

    // Wait for every player to finish
    for (Thread thread : threads) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        throw new IOException(e);
      }
    }
  }

  // Write final contents of each deck
  private void writeDeckOutputFiles() throws IOException {
    for (CardDeck deck : decks) {
      String filename = "deck" + deck.getDeckNumber() + "_output.txt";
      try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename))) {
        writer.write("deck" + deck.getDeckNumber() + " contents: " + deck);
        writer.newLine();
      }
    }
  }
}
