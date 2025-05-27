package player;

import card.Card;
import card.ListOfCards;
import rule.ThirteenSRule;

public class ThirteenBot extends Actor {
    public ThirteenBot(String gameType) {

        super(gameType);
    }


    public boolean autoSelectCards(ListOfCards cardsOnTable) {
        clearSelected();
        ListOfCards autoWindowSelectCards = new ListOfCards();
        if (cardsOnHand.getSize()==0) {
            return false;
        }
        if (cardsOnHand.getSize()<cardsOnTable.getSize()) {
            return false;
        }
        if (cardsOnTable.getSize()==0) {

            cardsOnHand.getCardAt(0).setSelected(true);

            return true;
        }
        int length=cardsOnTable.getSize();



        cardsOnTable.sortRankSuit();
        cardsOnHand.sortRankSuit();

        for (int i = 0; i < length; i++) {
            autoWindowSelectCards.addCard(cardsOnHand.getCardAt(i));
        }
        int last_index=length-1;

        outerwhile: while (last_index<cardsOnHand.getSize()) {
            System.out.println(autoWindowSelectCards.toString());
            System.out.println(cardsOnTable.toString());
            if (rule.checkValidPlay(autoWindowSelectCards,cardsOnTable))
            {
                for (int i = 0; i < length; i++) {
                    autoWindowSelectCards.getCardAt(i).setSelected(true);
                }
                System.out.println("Bot has selected a valid card");
                return true;
            }



            last_index++;
            if (last_index<cardsOnHand.getSize()) {

                autoWindowSelectCards.removeCard(0);
                autoWindowSelectCards.addCard(cardsOnHand.getCardAt(last_index));
            }



        }
        clearSelected();
        return false;

    }
}

