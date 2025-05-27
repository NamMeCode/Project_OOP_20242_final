package rule;

import card.ListOfCards;

abstract public class ThirteenRule extends GameRule {
    abstract public boolean checkDoubleSequence(ListOfCards cards);
    abstract public boolean checkSequence(ListOfCards cards);
    abstract public String handType(ListOfCards cards);
    public boolean checkWinCondition(ListOfCards cards) {
        return cards.getSize() == 0;
    }

    public boolean checkValidPlay(ListOfCards playCards, ListOfCards tableCards) {
        if (playCards.getSize() == 0) return false;

        // First play of the round
        if (tableCards.getSize() == 0) {
            playCards.sortRankSuit();
            String type = handType(playCards);
            System.out.println("First play - Hand type: " + type);
            return !type.equals("Invalid");
        }

        // Check for bomb plays (special combinations that can beat anything)
        if (playCards.getSize() != tableCards.getSize()) {
            return checkBombPlay(playCards, tableCards);
        }

        // Same size - normal comparison
        playCards.sortRankSuit();
        tableCards.sortRankSuit();
        String playType = handType(playCards);
        String tableType = handType(tableCards);

        System.out.println("Play type: " + playType + ", Table type: " + tableType);

        if (!playType.equals(tableType)) return false;
        if (playType.equals("Invalid")) return false;

        // Compare the highest cards
        return playCards.getCardAt(playCards.getSize() - 1)
                .compareCard(tableCards.getCardAt(tableCards.getSize() - 1)) > 0;
    }

    boolean checkBombPlay(ListOfCards playCards, ListOfCards tableCards) {
        playCards.sortRankSuit();

        // Four of a kind can beat any single, pair, or triple
        if (checkFourCardsSameRank(playCards)) {
            return tableCards.getSize() <= 3;
        }

        // Double sequence can beat smaller combinations
        if (checkDoubleSequence(playCards) && tableCards.getCardAt(0).getRank() == 15) {
            if (tableCards.getSize() == 1 && playCards.getSize() >= 6) return true;
            if (tableCards.getSize() == 2 && playCards.getSize() >= 8) return true;
            return tableCards.getSize() == 3 && playCards.getSize() >= 10;
        }

        return false;
    }
}