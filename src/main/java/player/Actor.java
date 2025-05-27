package player;
import card.Card;
import card.ListOfCards;
import rule.*;

public abstract class Actor {
    ListOfCards cardsOnHand = new ListOfCards();
    String gameType;
    GameRule rule;
    public int position;
    public int getPosition(){
        return position;
    }
    private static int idGenerator = 0;
    private final int id;

    public Actor(String gameType) {
        this.id = ++idGenerator;
        setGameType(gameType);
        setRule(gameType); // Khởi tạo rule ngay khi tạo Actor
    }

    public void setRule(String gameType) {
        switch (gameType) {
            case "ThirteenS": {
                this.rule = new ThirteenSRule();
                break;
            }
            case "ThirteenN": {
                this.rule = new ThirteenNRule();
                break;
            }
            default: {
                this.rule = new ThirteenSRule(); // Default rule
                break;
            }
        }
    }

    public int getId() {
        return id;
    }

    public void setCardsOnHand(ListOfCards cardsOnHand) {
        this.cardsOnHand = cardsOnHand;
        cardsOnHand.sortRankSuit();
    }

    public ListOfCards getCardsOnHand() {
        return cardsOnHand;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
        setRule(gameType); // Cập nhật rule khi thay đổi gameType
    }

    public void selectCard(int index) {
        Card cardChosen = cardsOnHand.getCardAt(index);
        if (cardChosen != null) {
            cardChosen.setSelected(true);
        }
    }

    private boolean checkValidPlay(ListOfCards cardsPlayed, ListOfCards cardsOnTable) {
        if (rule == null) {
            setRule(gameType); // Fallback nếu rule chưa được khởi tạo
        }
        return rule.checkValidPlay(cardsPlayed, cardsOnTable);
    }

    public boolean playCards(ListOfCards cardsOnTable) {
        ListOfCards cardsPlayed = cardsOnHand.cardsSelected();

        if (cardsPlayed.getSize() == 0) return false;
        if (checkValidPlay(cardsPlayed, cardsOnTable)) {
            cardsOnTable.replacedBy(cardsPlayed);
            cardsOnHand.replacedBy(cardsOnHand.cardsNotSelected());
            return true;
        } else {
            return false;
        }
    }

    public boolean isWin() {
        if (rule == null) {
            setRule(gameType); // Fallback nếu rule chưa được khởi tạo
        }

        switch (gameType) {
            case "ThirteenN":
                return ((ThirteenNRule) rule).checkWinCondition(cardsOnHand);
            case "ThirteenS":
                return ((ThirteenSRule) rule).checkWinCondition(cardsOnHand);
//            case "Reddog":
//                return ((ReddogRule) rule).checkWinCondition(cardsOnHand);
        }
        return false;
    }

    public void clearSelected() {
        for (int i = 0; i < cardsOnHand.getSize(); i++) {
            Card card = cardsOnHand.getCardAt(i);
            if (card != null) {
                card.setSelected(false);
            }
        }
    }

    public ListOfCards getCardsSelected() {
        ListOfCards selectedCards = new ListOfCards();
        for (int i = 0; i < cardsOnHand.getSize(); i++) {
            Card card = cardsOnHand.getCardAt(i);
            if (card.isSelected()) {
                selectedCards.addCard(card);
            }
        }
        return selectedCards;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Actor other)) return false;
        return this.getId() == other.getId(); // hoặc bất kỳ thuộc tính định danh nào
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.getId());
    }

}