package control;

import card.ListOfCards;
import player.Actor;
import player.ThirteenPlayer;
import player.ThirteenBot;
import java.util.ArrayList;

public class ThirteenRound {
    private final ArrayList<Actor> playersInRound = new ArrayList<>();
    private ArrayList<Actor> playersInGame;
    private final ArrayList<Actor> playersWinGame;
    private Actor playerLastInRound;
    private ListOfCards cardsOnTable = new ListOfCards();
    private int currentPlayerIndex;
    private Actor currentPlayer;
    private boolean roundActive = true;

    public ThirteenRound(ArrayList<Actor> playersInGame, ArrayList<Actor> playersWinGame, Actor playerStartRound) {
        this.playersInGame = playersInGame;
        this.playersWinGame = playersWinGame;
        initializePlayersInRound(playersInGame);
        this.currentPlayerIndex = playersInRound.indexOf(playerStartRound);
        this.currentPlayer = playerStartRound;
    }

    private void initializePlayersInRound(ArrayList<Actor> playersInGame) {
        playersInRound.clear();
        playersInRound.addAll(playersInGame);
    }

    public Actor getCurrentPlayer() {
        return currentPlayer;
    }

    public ListOfCards getCardsOnTable() {
        return cardsOnTable;
    }

    public boolean isRoundActive() {
        return roundActive && playersInRound.size() > 1;
    }

    public Actor getPlayerLastInRound() {
        return playerLastInRound;
    }

    // Chuyển sang lượt tiếp theo
    public void nextTurn() {
        if (playersInRound.size() <= 1) {
            roundActive = false;
            playerLastInRound = playersInRound.isEmpty() ? null : playersInRound.get(0);
            currentPlayer = null;
            return;
        }
        currentPlayerIndex = (currentPlayerIndex + 1) % playersInRound.size();
        currentPlayer = playersInRound.get(currentPlayerIndex);
    }

    // Xử lý lệnh Play - chung cho cả player và bot
    public boolean play() {
        if (currentPlayer == null) return false;

        if (currentPlayer.playCards(cardsOnTable)) {

            if (currentPlayer.isWin()) {

                playersWinGame.add(currentPlayer);
                playersInGame.remove(currentPlayer);
                playersInRound.remove(currentPlayer);

                currentPlayerIndex--;

                if (playersInRound.isEmpty()) {
                    currentPlayer = null;
                    roundActive = false;
                } else if(currentPlayerIndex >= playersInRound.size()){
                    // Điều chỉnh index để nextTurn() chuyển đúng
                    currentPlayerIndex = playersInRound.size() - 1;
                }
            }
            return true;
        }
        return false;
    }

    // Xử lý lệnh Select
    public void select(int cardIndex) {
        currentPlayer.selectCard(cardIndex);
    }

    // Xử lý lệnh Pass - chung cho cả player và bot
    public void pass() {
        if (currentPlayer == null || playersInRound.size() <= 1) {
            roundActive = false;
            return;
        }

        // Lưu lại index hiện tại trước khi xóa
        int removedIndex = currentPlayerIndex;

        // Xóa player khỏi round
        playersInRound.remove(currentPlayer);

        // Kiểm tra nếu chỉ còn 1 player hoặc không còn ai
        if (playersInRound.isEmpty()) {
            currentPlayer = null;
            roundActive = false;
            return;
        }

        if (playersInRound.size() == 1) {
            playerLastInRound = playersInRound.get(0);
            currentPlayer = null;
            roundActive = false;
            return;
        }

        // Điều chỉnh currentPlayerIndex sau khi xóa player
        if (removedIndex >= playersInRound.size()) {
            currentPlayerIndex = 0;
        } else {
            currentPlayerIndex = removedIndex;
        }

        // Cập nhật currentPlayer
        currentPlayer = playersInRound.get(currentPlayerIndex);
    }

    // Kiểm tra xem player hiện tại có phải là bot không
    public boolean isCurrentPlayerBot() {
        return currentPlayer instanceof ThirteenBot;
    }

    // Kiểm tra xem player hiện tại có phải là human player không
    public boolean isCurrentPlayerHuman() {
        return currentPlayer instanceof ThirteenPlayer;
    }
}