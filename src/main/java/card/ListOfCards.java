package card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ListOfCards {
    private ArrayList<Card> cardList=new ArrayList<>();
    private int size=0;

    public ListOfCards() {}

    public ListOfCards(ArrayList<Card> cardList) {
        this.cardList = new ArrayList<>(cardList);
        this.size = cardList.size();
    }

    public int getSize()
    {
        return size;
    }

    public ArrayList<Card> getCardList()
    {
        return cardList;
    }

    public String toString ()
    {
        StringBuilder sb = new StringBuilder();
        if (!cardList.isEmpty())
            sb.append("| ");
        for (Card card : cardList) {
            sb.append(card.toString());
            sb.append(" | ");
        }
        return sb.toString();
    }

    public void shuffle() {
        Collections.shuffle(cardList);
    }
    public void sortRankSuit() {
        cardList.sort(Comparator.comparing(Card::getRank).thenComparing(Card::getSuit));
    }

    public void sortSuitRank() {
        cardList.sort(Comparator.comparing(Card::getSuit).thenComparing(Card::getRank));
    }

    public void addCard(Card card) {
        cardList.add(card);
        size++;
    }

    public void removeCard(int index)
    {
        cardList.remove(index);
        size--;
    }

    public void removeCard(Card card) {
        boolean removed=cardList.remove(card);
        if (removed) {
            size--;
        }
    }

    public void addAll(ListOfCards cards) {
        cardList.addAll(cards.getCardList());
        size = cardList.size();
    }

    public void clear() {
        size = 0;
        cardList.clear();
    }

    public Card drawCard() {
        Card card=cardList.removeLast();
        size--;
        return card;
    }

    public ListOfCards drawCard(int numberOfCards) {
        ListOfCards cardsDrawn = new ListOfCards();
        while (numberOfCards-- > 0) {
            cardsDrawn.addCard(drawCard());
        }
        return cardsDrawn;
    }

    public Card getCardAt(int index) {
        if (index >= 0 && index < size) {
            return cardList.get(index);
        }
        return null;
    }

    public boolean contains(Card card) {
        for(Card card1: cardList) {
            if(card == card1) return true;
        }
        return false;
    }

    public ListOfCards cardsSelected() {
        ArrayList <Card> cardsSelected=new ArrayList<>();
        for (Card card : cardList) {
            if (card.isSelected()) {
                cardsSelected.add(card);
            }
        }
        return new ListOfCards(cardsSelected);
    }

    public ListOfCards cardsNotSelected() {
        ArrayList <Card> cardsNotSelected=new ArrayList<>();
        for (Card card : cardList) {
            if (!card.isSelected()) {
                cardsNotSelected.add(card);
            }
        }
        return new ListOfCards(cardsNotSelected);
    }

    public void replacedBy(ListOfCards newList) {
        cardList = newList.getCardList();
        size = newList.size;
    }

    public void initializeDeck(String gameType)
    {
        String[] rank= {"2","3","4","5","6","7","8","9","10","J","Q","K","A"};
        String[] suit ={"C","D","H","S"};
        for (int i=0;i<4;i++)
            for (int j=0;j<13;j++)
            {
                String imagePath = "/com/example/project_oop_20242/cards/" + rank[j] + "-" +suit[i] + ".png";
                cardList.add(new Card(rank[j],suit[i], gameType,imagePath));
            }
        size=cardList.size();
        shuffle();
    }

}