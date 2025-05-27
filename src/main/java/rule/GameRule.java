//package rule;
//
//import card.ListOfCards;
//
//public abstract class GameRule {
//    public static boolean checkTwoCardsSameRank(ListOfCards cards) {
//        return cards.getCardAt(0).equals(cards.getCardAt(1));
//    }
//
//    public static boolean checkThreeCardsSameRank(ListOfCards cards) {
//        return cards.getCardAt(0).equals(cards.getCardAt(1)) &&
//                cards.getCardAt(1).equals(cards.getCardAt(2));
//    }
//
//    public static boolean checkFourCardsSameRank(ListOfCards cards) {
//        return cards.getCardAt(0).equals(cards.getCardAt(1)) &&
//                cards.getCardAt(1).equals(cards.getCardAt(2)) &&
//                cards.getCardAt(2).equals(cards.getCardAt(3));
//    }
//
//    public static boolean checkContinuousRank(ListOfCards cards) {
//        for(int i = 0; i < cards.getSize() - 1; i++) {
//            if(cards.getCardAt(i).getRank() + 1 != cards.getCardAt(i+1).getRank())
//                return false;
//        }
//        return true;
//    }
//
//    public static boolean checkFlush(ListOfCards cards) {
//        for(int i = 0; i<cards.getSize() - 1; i++) {
//            if(cards.getCardAt(i).getSuit() != cards.getCardAt(i+1).getSuit())
//                return false;
//        }
//        return true;
//    }
//
//}
package rule;

import card.ListOfCards;
import card.Card;

public abstract class GameRule {
    abstract public boolean checkValidPlay(ListOfCards cardsOnHand, ListOfCards cardsOnTable);

    public static boolean checkTwoCardsSameRank(ListOfCards cards) {
        if (cards.getSize() != 2) return false;
        return cards.getCardAt(0).getRank() == cards.getCardAt(1).getRank();
    }

    public static boolean checkThreeCardsSameRank(ListOfCards cards) {
        if (cards.getSize() != 3) return false;
        return cards.getCardAt(0).getRank() == cards.getCardAt(1).getRank() &&
                cards.getCardAt(1).getRank() == cards.getCardAt(2).getRank();
    }

    public static boolean checkFourCardsSameRank(ListOfCards cards) {
        if (cards.getSize() != 4) return false;
        return cards.getCardAt(0).getRank() == cards.getCardAt(1).getRank() &&
                cards.getCardAt(1).getRank() == cards.getCardAt(2).getRank() &&
                cards.getCardAt(2).getRank() == cards.getCardAt(3).getRank();
    }

    public static boolean checkContinuousRank(ListOfCards cards) {
        if (cards.getSize() < 3) return false;

        for(int i = 0; i < cards.getSize() - 1; i++) {
            int currentRank = cards.getCardAt(i).getRank();
            int nextRank = cards.getCardAt(i+1).getRank();

            // Handle special case: A, 2, 3 sequence
            if (currentRank == 1 && nextRank == 2) continue;
            if (currentRank == 2 && nextRank == 15) continue; // 2 to 3 (3 has rank 15)

            // Normal sequence check
            if (currentRank + 1 != nextRank) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkFlush(ListOfCards cards) {
        if (cards.getSize() < 2) return true;

        for(int i = 0; i < cards.getSize() - 1; i++) {
            if(cards.getCardAt(i).getSuit() != cards.getCardAt(i+1).getSuit())
                return false;
        }
        return true;
    }
}