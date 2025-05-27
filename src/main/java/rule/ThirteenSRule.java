package rule;

import card.ListOfCards;

public class ThirteenSRule extends ThirteenRule {
    @Override
    public boolean checkDoubleSequence(ListOfCards cards) {
        if (cards.getSize() < 6 || cards.getSize() % 2 != 0) return false;

        // Check if we have pairs in sequence
        for (int i = 0; i < cards.getSize(); i += 2) {
            // Check if current pair is valid
            if (cards.getCardAt(i).getRank() != cards.getCardAt(i + 1).getRank()) {
                return false;
            }

            // Check sequence between pairs
            if (i > 0) {
                int prevRank = cards.getCardAt(i - 2).getRank();
                int currRank = cards.getCardAt(i).getRank();

                // Handle special sequences
                if (prevRank == 1 && currRank == 2) continue;
                if (prevRank == 2 && currRank == 15) continue;

                if (prevRank + 1 != currRank) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean checkSequence(ListOfCards cards) {
        if (cards.getSize() < 3) return false;
        // Miền Nam: Chỉ cần sảnh, KHÔNG cần cùng chất
        return checkContinuousRank(cards);
    }

    @Override
    public String handType(ListOfCards cards) {
        int size = cards.getSize();

        switch (size) {
            case 1:
                return "Single";

            case 2:
                if (checkTwoCardsSameRank(cards)) {
                    return "Pair";
                }
                break;

            case 3:
                if (checkThreeCardsSameRank(cards)) {
                    return "Triple";
                }
                // Miền Nam: Sảnh 3 lá không cần cùng chất
                if (checkSequence(cards)) {
                    return "Sequence";
                }
                break;

            case 4:
                if (checkFourCardsSameRank(cards)) {
                    return "Quad";
                }
                // Miền Nam: Sảnh 4 lá không cần cùng chất
                if (checkSequence(cards)) {
                    return "Sequence";
                }
                break;

            case 5:
                if (checkSequence(cards)) {
                    return "Sequence";
                }
                break;

            default:
                if (size >= 3 && checkSequence(cards)) {
                    return "Sequence";
                }
                if (size >= 6 && size % 2 == 0 && checkDoubleSequence(cards)) {
                    return "Double Sequence";
                }
                break;
        }

        return "Invalid";
    }
}