package card;

public class Card {
    String rank;
    private String suit;
    private boolean Selected=false;
    private String gameType;
    private String imagePath;

    public Card(String rank, String suit) {
        this.rank=rank;
        this.suit=suit;
    }

    public Card(String rank, String suit, String gameType,String imagePath) {
        this.rank=rank;
        this.suit=suit;
        this.gameType = gameType;
        this.imagePath=imagePath;
    }

    public boolean isSelected() {
        return Selected;
    }

    public void setSelected(boolean selected) {
        Selected = selected;
    }

    public String getImagePath() {
        return imagePath;
    }
    public int getRank() {
        switch (gameType) {
            case "Reddog": {
                return switch (rank) {
                    case "J" -> 11;
                    case "Q" -> 12;
                    case "K" -> 13;
                    case "A" -> 14;
                    default -> Integer.parseInt(rank);
                };
            }
            default: {
                return switch (rank) {
                    case "J" -> 11;
                    case "Q" -> 12;
                    case "K" -> 13;
                    case "A" -> 14;
                    case "2" -> 15;
                    default -> Integer.parseInt(rank);
                };
            }
        }
    }

    public boolean checkSameColour(Card card) {
        return (this.getSuit() >= 3 && card.getSuit() >= 3) || (this.getSuit() <= 2 && card.getSuit() <= 2);
    }

    public int getSuit() {
        return switch (suit) {
            case "H" -> 4;
            case "D" -> 3;
            case "C" -> 2;
            default -> 1;
        };
    }

    public int compareCard(Card card) {
        int res = this.getRank() - card.getRank();
        return res != 0 ? res : this.getSuit() - card.getSuit();
    }

    public String toString() {
        return rank + " - " + suit;
    }
}