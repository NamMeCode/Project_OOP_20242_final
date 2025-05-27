//package rule;
//
//import card.Card;
//import card.ListOfCards;
//
//public class ThirteenNRule extends ThirteenRule {
//    public boolean checkPair(ListOfCards cards) {
//        return cards.getCardAt(0).equals(cards.getCardAt(1)) &&
//                cards.getCardAt(0).checkSameColour(cards.getCardAt(1));
//    }
//
//    public boolean checkSequence(ListOfCards cards) {
//        for(int i = 0; i < cards.getSize() - 1; i++) {
//            if(cards.getCardAt(i).getRank() == 15) return false;
//            Card card1 = cards.getCardAt(i), card2 = cards.getCardAt(i + 1);
//            if((card1.getRank() != card2.getRank() + 1) || (!card1.checkSameColour(card2)))
//                return false;
//        }
//        return true;
//    }
//
//    public boolean checkDoubleSequence(ListOfCards cards) {
//        if(cards.getSize() % 2 == 1) return false;
//        for(int i = 0; i < cards.getSize() - 1; i++) {
//            Card card1 = cards.getCardAt(i), card2 = cards.getCardAt(i + 1);
//            if(i % 2 == 0) {
//                if((card1.getRank() != card2.getRank()) || (!card1.checkSameColour(card2)))
//                    return false;
//            }
//            else if((card1.getRank() != (card2.getRank() + 1)) || (!card1.checkSameColour(card2)))
//                return false;
//        }
//        return true;
//    }
//
//    public String handType(ListOfCards cards) {
//        switch (cards.getSize()) {
//            case 1: return "Single";
//            case 2: if(checkPair(cards)) return "Pair";
//            case 3:
//                if(checkThreeCardsSameRank(cards)) return "ThreeOfAKind";
//                else if(checkSequence(cards)) return "Sequence";
//                else return "Invalid";
//            case 4:
//                if(checkFourCardsSameRank(cards)) return "FourOfAKind";
//                else if(checkSequence(cards)) return "Sequence";
//                else return "Invalid";
//            case 5:
//                if(checkSequence(cards)) return "Sequence";
//                else return "Invalid";
//            default:
//                if(checkSequence(cards)) return "Sequence";
//                else if(checkDoubleSequence(cards)) return "DoubleSequence";
//                else return "Invalid";
//        }
//    }
//
//    public boolean checkWinCondition(ListOfCards handCards) {
//        // win with no card on hand
//        return handCards.getCardList().isEmpty();
//    }
//}
package rule;

import card.ListOfCards;

public class ThirteenNRule extends ThirteenRule {

    @Override
    public boolean checkDoubleSequence(ListOfCards cards) {
        if (cards.getSize() < 6 || cards.getSize() % 2 != 0) return false;

        // Miền Bắc: Đôi thông phải cùng chất
        if (!checkFlush(cards)) return false;

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
        // Miền Bắc: Sảnh PHẢI cùng chất mới hợp lệ
        return checkContinuousRank(cards) && checkFlush(cards);
    }
    public boolean checkPair(ListOfCards cards) {
        if (cards.getSize() != 2) return false;
        return checkTwoCardsSameRank(cards) && cards.getCardAt(0).checkSameColour(cards.getCardAt(1));
    }

    @Override
    public String handType(ListOfCards cards) {
        int size = cards.getSize();

        switch (size) {
            case 1:
                return "Single";

            case 2:
                if (checkPair(cards)) {
                    return "Pair";
                }
                break;

            case 3:
                if (checkThreeCardsSameRank(cards)) {
                    return "Triple";
                }
                // Miền Bắc: Sảnh 3 lá PHẢI cùng chất
                if (checkSequence(cards)) {
                    return "Straight Flush";  // Luôn là straight flush vì phải cùng chất
                }
                break;

            case 4:
                if (checkFourCardsSameRank(cards)) {
                    return "Quad";
                }
                // Miền Bắc: Sảnh 4 lá PHẢI cùng chất
                if (checkSequence(cards)) {
                    return "Straight Flush";  // Luôn là straight flush vì phải cùng chất
                }
                break;

            case 5:
                // Miền Bắc: Chỉ có thùng phá sảnh vì sảnh phải cùng chất
                if (checkSequence(cards)) {
                    return "Straight Flush";
                }
                // Thùng đơn thuần (5 lá cùng chất nhưng không liên tiếp)
                if (checkFlush(cards)) {
                    return "Flush";
                }
                break;

            default:
                if (size >= 3 && checkSequence(cards)) {
                    return "Straight Flush";  // Luôn là straight flush vì phải cùng chất
                }
                if (size >= 5 && checkFlush(cards)) {
                    return "Flush";
                }
                if (size >= 6 && size % 2 == 0 && checkDoubleSequence(cards)) {
                    return "Double Sequence";  // Đã check flush trong method
                }
                break;
        }

        return "Invalid";
    }

    @Override
    public boolean checkWinCondition(ListOfCards cards) {
        return cards.getSize() == 0;
    }

    // Override lại method checkValidPlay cho miền Bắc
    @Override
    public boolean checkValidPlay(ListOfCards playCards, ListOfCards tableCards) {
        if (playCards.getSize() == 0) return false;

        // First play của round
        if (tableCards.getSize() == 0) {
            playCards.sortRankSuit();
            String type = handType(playCards);
            System.out.println("First play - Hand type: " + type);
            return !type.equals("Invalid");
        }

        // Check bomb plays
        if (playCards.getSize() != tableCards.getSize()) {
            return checkBombPlay(playCards, tableCards);
        }

        // Same size - kiểm tra miền Bắc
        playCards.sortRankSuit();
        tableCards.sortRankSuit();
        String playType = handType(playCards);
        String tableType = handType(tableCards);

        System.out.println("Play type: " + playType + ", Table type: " + tableType);

        if (!playType.equals(tableType)) return false;
        if (playType.equals("Invalid")) return false;

        // MIỀN BẮC: Kiểm tra cùng chất cho đơn và đôi
        if (playType.equals("Single")) {
            // Đánh đơn phải cùng chất với lá trên bàn
            if (playCards.getCardAt(0).getSuit() != tableCards.getCardAt(0).getSuit()) {
                return false;
            }
        } else if (playType.equals("Pair")) {
            // MIỀN BẮC: Đánh đôi phải cùng màu với đôi trên bàn
            if (!playCards.getCardAt(0).checkSameColour(tableCards.getCardAt(0)) ||
                    !playCards.getCardAt(1).checkSameColour(tableCards.getCardAt(1))) {
                return false;
            }
        }

        // Compare highest cards
        return playCards.getCardAt(playCards.getSize() - 1)
                .compareCard(tableCards.getCardAt(tableCards.getSize() - 1)) > 0;
    }

    private boolean checkBombPlay(ListOfCards playCards, ListOfCards tableCards) {
        playCards.sortRankSuit();

        // Four of a kind can beat any single, pair, or triple
        if (checkFourCardsSameRank(playCards)) {
            return tableCards.getSize() <= 3;
        }

        // Double sequence can beat smaller combinations (miền Bắc cần cùng chất)
        if (checkDoubleSequence(playCards)) {
            if (tableCards.getSize() == 1 && playCards.getSize() >= 6) return true;
            if (tableCards.getSize() == 2 && playCards.getSize() >= 8) return true;
            if (tableCards.getSize() == 3 && playCards.getSize() >= 10) return true;
        }

        return false;
    }
}