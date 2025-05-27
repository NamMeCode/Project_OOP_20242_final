//package rule;
//
//import card.ListOfCards;
//import card.Card;
//
//public class ThirteenSRule extends ThirteenRule {
//    public boolean checkSequence(ListOfCards cards) {
//        for(int i = 0; i < cards.getSize() - 1; i++) {
//            if(cards.getCardAt(i).getRank() == 15) return false;
//            if(cards.getCardAt(i).getRank() != cards.getCardAt(i+1).getRank() + 1)
//                return false;
//        }
//        return true;
//    }
//
//    public boolean checkDoubleSequence(ListOfCards cards) {
//        if(cards.getSize() % 2 == 1) return false;
//        for(int i = 0; i < cards.getSize() - 1; i++) {
//            if(i % 2 == 0) {
//                if(cards.getCardAt(i).getRank() != cards.getCardAt(i+1).getRank())
//                    return false;
//            }
//            else if(cards.getCardAt(i).getRank() != (cards.getCardAt(i+1).getRank() + 1))
//                return false;
//        }
//        return true;
//    }
//
//    public String handType(ListOfCards cards) {
//        switch (cards.getSize()) {
//            case 1: return "Single";
//            case 2: if(checkTwoCardsSameRank(cards)) return "Pair";
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
//        if(handCards.getCardList().isEmpty()) return true;
//
//        if(handCards.getSize() != 13) return false;
//        // win with four 2's
//        if(handCards.getCardAt(handCards.getSize()).getRank() == 15) {
//            ListOfCards tempCards = new ListOfCards();
//            for(int i=1; i<=4; i++) {
//                tempCards.addCard(handCards.getCardAt(handCards.getSize() - i));
//            }
//            if(checkFourCardsSameRank(tempCards)) return true;
//        }
//
//        //win with dragon sequence
//        int rank = 3, duplicateCardNum = 0;
//        for(int i=0; i<handCards.getSize(); i++) {
//            if(rank == 14) return true;
//            if(duplicateCardNum > 1) break;
//            Card card = handCards.getCardAt(i);
//            if(card.getRank() == rank) rank++;
//            else if(card.equals(handCards.getCardAt(i-1))) duplicateCardNum++;
//            else break;
//        }
//        return false;
//    }
//}
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
                // Miền Nam: Sảnh đồng chất mạnh hơn sảnh thường nhưng không bắt buộc
                if (checkFlush(cards) && checkSequence(cards)) {
                    return "Straight Flush";
                }
                if (checkSequence(cards)) {
                    return "Sequence";
                }
                break;

            default:
                // Miền Nam: Ưu tiên sảnh đồng chất nếu có
                if (size >= 5 && checkFlush(cards) && checkSequence(cards)) {
                    return "Straight Flush";
                }
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

    @Override
    public boolean checkWinCondition(ListOfCards cards) {
        return cards.getSize() == 0;
    }

    // QUAN TRỌNG: Override checkValidPlay cho miền Nam
    @Override
    public boolean checkValidPlay(ListOfCards playCards, ListOfCards tableCards) {
        if (playCards.getSize() == 0) return false;

        // First play của round
        if (tableCards.getSize() == 0) {
            playCards.sortRankSuit();
            String type = handType(playCards);
            System.out.println("ThirteenS - First play - Hand type: " + type);
            return !type.equals("Invalid");
        }

        // Check bomb plays
        if (playCards.getSize() != tableCards.getSize()) {
            return checkBombPlay(playCards, tableCards);
        }

        // Same size - logic miền Nam (KHÔNG yêu cầu cùng chất)
        playCards.sortRankSuit();
        tableCards.sortRankSuit();
        String playType = handType(playCards);
        String tableType = handType(tableCards);

        System.out.println("ThirteenS - Play type: " + playType + ", Table type: " + tableType);

        if (!playType.equals(tableType)) return false;
        if (playType.equals("Invalid")) return false;

        // MIỀN NAM: KHÔNG kiểm tra cùng chất cho đơn và đôi
        // Chỉ so sánh rank và suit theo thứ tự ưu tiên

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

        // Double sequence can beat smaller combinations (miền Nam không yêu cầu cùng chất)
        if (checkDoubleSequence(playCards)) {
            if (tableCards.getSize() == 1 && playCards.getSize() >= 6) return true;
            if (tableCards.getSize() == 2 && playCards.getSize() >= 8) return true;
            if (tableCards.getSize() == 3 && playCards.getSize() >= 10) return true;
        }

        return false;
    }
}