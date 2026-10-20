package cards;

public class Card {

  private final int denomination;

  public Card(int denomination) {
    if (denomination < 0) {
      throw new IllegalArgumentException("Denomination must be positive.");
    }
    this.denomination = denomination;
  }

  public int getDenomination() {
    return denomination;
  }

  @Override
  public String toString() {
    return String.valueOf(denomination);
  }

}