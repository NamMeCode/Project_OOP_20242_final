
package com.example.project_oop_20242;

import card.Card;
import card.ListOfCards;
import control.ThirteenRound;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import player.Actor;
import player.ThirteenPlayer;
import player.ThirteenBot;
import rule.ThirteenSRule;
import rule.ThirteenNRule;
import rule.GameRule;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ThirteenController implements Initializable {
    private boolean isTextMode ;
    private final List<Node> deck = new ArrayList<>(); // Changed to List<Node> for consistency
    @FXML private StackPane deckPane;
    @FXML private StackPane northPane;
    @FXML private StackPane southPane;
    @FXML private StackPane eastPane;
    @FXML private StackPane westPane;
    @FXML private StackPane centerPane;
    @FXML private Button playButton;
    @FXML private Button passButton;

    private Image cardBack;
    private int playerCount;
    private final Map<Actor, List<Node>> playerCardViews = new HashMap<>();
    private final List<Node> selectedCards = new ArrayList<>();
    private final Map<Node, Boolean> cardSelectionState = new HashMap<>();
    private ArrayList<Actor> players = new ArrayList<>();
    private ArrayList<Actor> playersWinGame = new ArrayList<>();
    private GameRule rule;
    private final Map<Actor, Integer> playerOriginalIndex = new HashMap<>();
    private ThirteenRound currentRound;
    private Actor playerWinLastRound;
    private boolean playWithBots;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize any FXML components if needed
    }

    public void setUpGame(int playerCount, String gameType, boolean playWithBots, boolean textMode) {
        clearDeckPane();
        this.playerCount = playerCount;
        this.playWithBots = playWithBots;
        this.isTextMode = textMode;

        if (gameType.equals("ThirteenS")) {
            rule = new ThirteenSRule();
        } else {
            rule = new ThirteenNRule();
        }

        initializePlayers(playerCount, gameType);
        playerWinLastRound = players.get(0);
        cardBack = isTextMode ? null : new Image(getClass().getResource("/com/example/project_oop_20242/cards/BACK.png").toExternalForm());
        createPlayerCardViews();
        animateDealFromLogic();
    }

    private void initializePlayers(int playerCount, String gameType) {
        players.clear();
        playersWinGame.clear();
        selectedCards.clear();
        cardSelectionState.clear();
        playerOriginalIndex.clear();

        ListOfCards deck = new ListOfCards();
        deck.initializeDeck(gameType);

        for (int i = 0; i < playerCount; i++) {
            Actor player;
            if (playWithBots && i != 0) {
                player = new ThirteenBot(gameType);
            } else {
                player = new ThirteenPlayer(gameType);
            }
            player.position = i;
            players.add(player);
            playerOriginalIndex.put(player, i);
        }

        for (Actor player : players) {
            player.setCardsOnHand(deck.drawCard(13));
        }
    }

    private void createPlayerCardViews() {
        playerCardViews.clear();
        for (int i = 0; i < playerCount; i++) {
            Actor player = players.get(i);
            ListOfCards hand = player.getCardsOnHand();
            List<Node> views = new ArrayList<>();
            for (Card c : hand.getCardList()) {
                Node cardView;
                if (isTextMode) {
                    Label cardLabel = new Label(c.toString());
                    cardLabel.setMinSize(50, 90);
                    cardLabel.setPrefSize(50, 90);
                    cardLabel.setStyle("""
//                      -fx-background-color: white;
//                        -fx-border-color: black;
                        -fx-border-width: 0.5px;
                        -fx-font-size: 14px;
                        -fx-alignment: top-center;
                        -fx-text-fill: #FFFFFF;
                    """);
                    cardView = cardLabel;
                } else {
                    ImageView imageView = new ImageView(cardBack);
                    imageView.setFitWidth(100);
                    imageView.setFitHeight(150);
                    cardView = imageView;
                }
                cardView.setUserData(c);
                cardSelectionState.put(cardView, false);
                views.add(cardView);
            }
            playerCardViews.put(player, views);
        }
    }

    private void startNewRound() {
        currentRound = new ThirteenRound(players, playersWinGame, playerWinLastRound);
        clearSelection();
        processCurrentTurn();
    }

    private void processCurrentTurn() {
        if (!currentRound.isRoundActive()) {
            endRound();
            return;
        }

        Actor currentPlayer = currentRound.getCurrentPlayer();
        if (currentPlayer == null) {
            endRound();
            return;
        }

        System.out.println("Processing turn for player " + currentPlayer.getId() +
                " (Bot: " + currentRound.isCurrentPlayerBot() + ")");

        highlightCurrentPlayer();

        if (currentRound.isCurrentPlayerBot()) {
            processBotTurn();
        } else {
            enablePlayerControls(true);
            System.out.println("Human player's turn");
        }
    }

    private void processBotTurn() {
        enablePlayerControls(false);

        Timeline botThinkingDelay = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> {
            Actor bot = currentRound.getCurrentPlayer();
            System.out.println("Bot is thinking...");

            if (bot instanceof ThirteenBot) {
                ThirteenBot thirteenBot = (ThirteenBot) bot;
                if (thirteenBot.autoSelectCards(currentRound.getCardsOnTable())) {
                    syncBotSelectionToUI(bot);
                    Timeline playDelay = new Timeline(new KeyFrame(Duration.seconds(1), ev -> handleBotPlay()));
                    playDelay.play();
                } else {
                    Timeline passDelay = new Timeline(new KeyFrame(Duration.seconds(1), ev -> handleBotPass()));
                    passDelay.play();
                }
            }
        }));
        botThinkingDelay.play();
    }

    private void syncBotSelectionToUI(Actor bot) {
        List<Node> botCardViews = playerCardViews.get(bot);
        StackPane botPane = getPlayerPane(bot);
        ListOfCards selectedCards = bot.getCardsSelected();

        clearSelectionForPlayer(bot);

        System.out.println("Syncing bot selection: " + selectedCards.getSize() + " cards");

        for (int i = 0; i < selectedCards.getSize(); i++) {
            Card selectedCard = selectedCards.getCardAt(i);
            for (int j = 0; j < botCardViews.size(); j++) {
                Node cardView = botCardViews.get(j);
                Card cardData = (Card) cardView.getUserData();

                if (cardData != null &&
                        cardData.getRank() == selectedCard.getRank() &&
                        cardData.getSuit() == selectedCard.getSuit() &&
                        !this.selectedCards.contains(cardView)) {

                    this.selectedCards.add(cardView);
                    cardSelectionState.put(cardView, true);

                    double[] position = calculateCardPosition(botPane, j);
                    moveCardForSelection(cardView, botPane, position[0], position[1], true);
                    break;
                }
            }
        }
    }

    private void handleBotPlay() {
        Actor bot = currentRound.getCurrentPlayer();
        System.out.println("Bot decided to play");

        if (currentRound.play()) {
            animatePlayedCards();
            Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> {
                if (playersWinGame.contains(bot)) {
                    removeAllCardsFromWinner(bot);
                }
                if (!currentRound.isRoundActive()) {
                    endRound();
                } else {
                    nextTurn();
                }
            }));
            delay.play();
        } else {
            System.out.println("Bot play failed, passing instead");
            handleBotPass();
        }
    }

    private void handleBotPass() {
        System.out.println("Bot decided to pass");
        resetSelectedCardsToOriginalPosition();
        currentRound.pass();
        clearSelection();

        Timeline delay = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
            if (!currentRound.isRoundActive()) {
                endRound();
            } else {
                processCurrentTurn();
            }
        }));
        delay.play();
    }

    private void clearSelectionForPlayer(Actor player) {
        List<Node> playerCards = playerCardViews.get(player);
        if (playerCards != null) {
            Iterator<Node> iterator = selectedCards.iterator();
            while (iterator.hasNext()) {
                Node selectedCard = iterator.next();
                if (playerCards.contains(selectedCard)) {
                    iterator.remove();
                    cardSelectionState.put(selectedCard, false);
                }
            }
        }
    }

    private void nextTurn() {
        currentRound.nextTurn();
        clearSelection();
        if (currentRound.isRoundActive() && currentRound.getCurrentPlayer() != null) {
            processCurrentTurn();
        } else {
            endRound();
        }
    }

    private void endRound() {
        playerWinLastRound = currentRound.getPlayerLastInRound();
        if (players.size() > 1) {
            if (centerPane != null) {
                Platform.runLater(() -> centerPane.getChildren().clear());
            }
            startNewRound();
        } else {
            endGame();
        }
    }

    private void endGame() {
        Actor winner = playersWinGame.get(0);
        enablePlayerControls(false);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project_oop_20242/EndGameScene.fxml"));
            Parent root = loader.load();
            EndGameController controller = loader.getController();
            String winnerName = winner instanceof ThirteenBot
                    ? "Bot " + (playerOriginalIndex.get(winner) )
                    : "Player " + (playerOriginalIndex.get(winner) + 1);
            controller.setWinnerName(winnerName);

            Stage stage = (Stage) playButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setFullScreen(true);
            stage.setMaximized(true);
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePlay() {
        if (currentRound != null && currentRound.isCurrentPlayerHuman()) {
            updatePlayerSelection();
            Actor playingPlayer = currentRound.getCurrentPlayer();
            if (currentRound.play()) {
                animatePlayedCards();
                Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> {
                    if (playersWinGame.contains(playingPlayer)) {
                        removeAllCardsFromWinner(playingPlayer);
                    }
                    if (!currentRound.isRoundActive()) {
                        endRound();
                    } else {
                        nextTurn();
                    }
                }));
                delay.play();
            }
        }
    }

    private void removeAllCardsFromWinner(Actor winner) {
        List<Node> winnerCards = playerCardViews.get(winner);
        StackPane winnerPane = getPlayerPane(winner);
        if (winnerCards != null && winnerPane != null) {
            Platform.runLater(() -> {
                winnerPane.getChildren().removeAll(winnerCards);
                for (Node card : winnerCards) {
                    cardSelectionState.remove(card);
                }
                winnerCards.clear();
            });
        }
    }

    @FXML
    private void handlePass() {
        if (currentRound != null && currentRound.isCurrentPlayerHuman()) {
            resetSelectedCardsToOriginalPosition();
            currentRound.pass();
            clearSelection();
            Timeline delay = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
                if (!currentRound.isRoundActive()) {
                    endRound();
                } else {
                    processCurrentTurn();
                }
            }));
            delay.play();
        }
    }

    private void resetSelectedCardsToOriginalPosition() {
        Actor currentPlayer = currentRound.getCurrentPlayer();
        StackPane currentPlayerPane = getPlayerPane(currentPlayer);
        List<Node> playerCards = playerCardViews.get(currentPlayer);
        for (Node selectedCard : selectedCards) {
            int cardIndex = playerCards.indexOf(selectedCard);
            if (cardIndex != -1) {
                double[] originalPosition = calculateCardPosition(currentPlayerPane, cardIndex);
                moveCardForSelection(selectedCard, currentPlayerPane, originalPosition[0], originalPosition[1], false);
                cardSelectionState.put(selectedCard, false);
            }
        }
        selectedCards.clear();
    }

    private void updatePlayerSelection() {
        Actor currentPlayer = currentRound.getCurrentPlayer();
        List<Node> playerCards = playerCardViews.get(currentPlayer);
        currentPlayer.clearSelected();
        for (int i = 0; i < playerCards.size(); i++) {
            Node cardView = playerCards.get(i);
            if (selectedCards.contains(cardView)) {
                currentRound.select(i);
            }
        }
    }

    private void clearSelection() {
        selectedCards.clear();
        for (Actor player : players) {
            List<Node> cards = playerCardViews.get(player);
            if (cards != null) {
                for (Node card : cards) {
                    cardSelectionState.put(card, false);
                }
            }
        }
    }

    private void animatePlayedCards() {
        Actor currentPlayer = currentRound.getCurrentPlayer();
        List<Node> cardsToAnimate = new ArrayList<>(selectedCards);

        for (int i = 0; i < cardsToAnimate.size(); i++) {
            Node card = cardsToAnimate.get(i);
            animateCardToCenter(card, i);
        }
        Timeline removeCards = new Timeline(new KeyFrame(Duration.seconds(0.8), e -> {
            removePlayedCardsFromHand(cardsToAnimate, currentPlayer);
        }));
        removeCards.play();
    }

    private void animateCardToCenter(Node card, int cardIndex) {
        if (centerPane == null) return;
        Card cardData = (Card) card.getUserData();
        Node animatedCard;

        if (isTextMode) {
            Label cardLabel = new Label(cardData.toString());
            cardLabel.setMinSize(60, 90);
            cardLabel.setPrefSize(60, 90);
            cardLabel.setStyle("""
                -fx-background-color: white;
                -fx-border-color: white;
                -fx-border-width: 1px;
                -fx-font-size: 14px;
                -fx-alignment: center;

                -fx-padding: 5px;
            """);
            animatedCard = cardLabel;
        } else {
            Image cardImage = new Image(getClass().getResource(cardData.getImagePath()).toExternalForm());
            ImageView imageView = new ImageView(cardImage);
            imageView.setFitWidth(80);
            imageView.setFitHeight(120);
            animatedCard = imageView;
        }

        double offsetX = cardIndex * 20;
        double offsetY = cardIndex * 5;
        animatedCard.setTranslateX(offsetX);
        animatedCard.setTranslateY(offsetY);

        Platform.runLater(() -> {
            centerPane.getChildren().add(animatedCard);
            ScaleTransition scaleIn = new ScaleTransition(Duration.seconds(0.3), animatedCard);
            scaleIn.setFromX(0.1);
            scaleIn.setFromY(0.1);
            scaleIn.setToX(1.0);
            scaleIn.setToY(1.0);
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), animatedCard);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            ParallelTransition playAnimation = new ParallelTransition(scaleIn, fadeIn);
            playAnimation.play();
        });
    }

    private void removePlayedCardsFromHand(List<Node> cardsToRemove, Actor player) {
        List<Node> playerCards = playerCardViews.get(player);
        StackPane playerPane = getPlayerPane(player);
        Platform.runLater(() -> {
            playerPane.getChildren().removeAll(cardsToRemove);
            for (Node card : cardsToRemove) {
                playerCards.remove(card);
                cardSelectionState.remove(card);
            }
            rearrangePlayerCards(player);
        });
    }

    private void rearrangePlayerCards(Actor player) {
        List<Node> playerCards = playerCardViews.get(player);
        StackPane playerPane = getPlayerPane(player);
        for (int i = 0; i < playerCards.size(); i++) {
            Node card = playerCards.get(i);
            double[] newPosition = calculateCardPosition(playerPane, i);
            TranslateTransition moveCard = new TranslateTransition(Duration.seconds(0.5), card);
            moveCard.setToX(newPosition[0]);
            moveCard.setToY(newPosition[1]);
            moveCard.play();
            card.setOnMouseClicked(null);
            enableClickToggle(playerPane, card, newPosition[0], newPosition[1]);
        }
    }

    private void highlightCurrentPlayer() {
        Actor currentPlayer = currentRound != null ? currentRound.getCurrentPlayer() : null;
        for (Map.Entry<Actor, Integer> entry : playerOriginalIndex.entrySet()) {
            Actor player = entry.getKey();
            List<Node> cards = playerCardViews.get(player);
            if (cards == null) continue;
            for (Node cardView : cards) {
                Card logicCard = (Card) cardView.getUserData();
                if (player.equals(currentPlayer)) {
                    if (isTextMode) {
                        if (cardView instanceof Label label) {
                            label.setText(logicCard.toString());
                        }
                    } else {
                        if (cardView instanceof ImageView imageView) {
                            imageView.setImage(new Image(getClass().getResource(logicCard.getImagePath()).toExternalForm()));
                        }
                    }
                } else {
                    if (isTextMode) {
                        if (cardView instanceof Label label) {
                            label.setText("XX");
                        }
                    } else {
                        if (cardView instanceof ImageView imageView) {
                            imageView.setImage(cardBack);
                        }
                    }
                }
            }
        }
    }

    private void enablePlayerControls(boolean enable) {
        Platform.runLater(() -> {
            if (playButton != null) playButton.setDisable(!enable);
            if (passButton != null) passButton.setDisable(!enable);
        });
    }

    private void animateDealFromLogic() {
        Timeline timeline = new Timeline();
        int cardsPerPlayer = 13;
        int totalRounds = cardsPerPlayer * playerCount;
        for (int i = 0; i < totalRounds; i++) {
            int round = i;
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.1 * round), e -> {
                int playerIndex = round % playerCount;
                Actor player = players.get(playerIndex);
                StackPane targetPane = getPlayerPaneByIndex(player.getPosition());
                List<Node> cards = playerCardViews.get(player);
                Node card = cards.get(round / playerCount);
                dealCardToPlayer(card, targetPane, playerIndex, round / playerCount);
            }));
        }
        timeline.setOnFinished(e -> {
            clearDeckPane();
            startNewRound();
        });
        timeline.play();
    }

    private StackPane getPlayerPane(Actor player) {
        Integer originalIndex = playerOriginalIndex.get(player);
        if (originalIndex == null) {
            throw new IllegalStateException("Player not found in original index map: " + player);
        }
        return switch (originalIndex) {
            case 0 -> southPane;
            case 1 -> westPane;
            case 2 -> northPane;
            case 3 -> eastPane;
            default -> throw new IllegalStateException("Invalid player index: " + originalIndex);
        };
    }

    private void dealCardToPlayer(Node card, StackPane targetPane, int playerIndex, int cardCount) {
        card.setTranslateX(0);
        card.setTranslateY(0);
        if (!deckPane.getChildren().contains(card)) {
            deckPane.getChildren().add(card);
        }
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.2), card);
        double[] offsets = getPlayerOffset(playerIndex);
        transition.setByX(offsets[0]);
        transition.setByY(offsets[1]);
        transition.setOnFinished(ev -> {
            deckPane.getChildren().remove(card);
            double[] position = calculateCardPosition(targetPane, cardCount);
            // Only apply rotation in graphical mode
            if (!isTextMode) {
                if (playerIndex == 1) card.setRotate(90);
                else if (playerIndex == 3) card.setRotate(-90);
            }
            card.setTranslateX(position[0]);
            card.setTranslateY(position[1]);
            targetPane.getChildren().add(card);
            enableClickToggle(targetPane, card, position[0], position[1]);
        });
        transition.play();
    }

    private double[] getPlayerOffset(int playerIndex) {
        return switch (playerIndex) {
            case 0 -> new double[]{0, 200};
            case 1 -> new double[]{-200, 0};
            case 2 -> new double[]{0, -200};
            case 3 -> new double[]{200, 0};
            default -> new double[]{0, 0};
        };
    }

    private double[] calculateCardPosition(StackPane targetPane, int cardCount) {
        double fanOffsetX = 0, fanOffsetY = 0;
       if (isTextMode) {
           if (targetPane == southPane || targetPane == northPane) {
               fanOffsetX = -220 + (cardCount * 40);
           } else if (targetPane == eastPane) {
               fanOffsetY = 220 - (cardCount * 40);
           } else if (targetPane == westPane) {
               fanOffsetY = -220 + (cardCount * 40);
           }
           return new double[]{fanOffsetX, fanOffsetY};
       }else{
           if (targetPane == southPane || targetPane == northPane) {
               fanOffsetX = -180 + (cardCount * 30);
           } else if (targetPane == eastPane) {
               fanOffsetY = 180 - (cardCount * 30);
           } else if (targetPane == westPane) {
               fanOffsetY = -180 + (cardCount * 30);
           }
           return new double[]{fanOffsetX, fanOffsetY};
       }
    }

    private void enableClickToggle(StackPane targetPane, Node cardView, double baseX, double baseY) {
        cardView.setOnMouseClicked(e -> {
            Actor currentPlayer = currentRound.getCurrentPlayer();
            StackPane currentPlayerPane = getPlayerPane(currentPlayer);
            if (!targetPane.equals(currentPlayerPane)) {
                return;
            }
            toggleCardSelection(cardView, targetPane, baseX, baseY);
        });
    }

    private void toggleCardSelection(Node card, StackPane targetPane, double baseX, double baseY) {
        Boolean isSelected = cardSelectionState.getOrDefault(card, false);
        if (!isSelected) {
            if (!selectedCards.contains(card)) {
                selectedCards.add(card);
            }
            cardSelectionState.put(card, true);
            moveCardForSelection(card, targetPane, baseX, baseY, true);
        } else {
            selectedCards.remove(card);
            cardSelectionState.put(card, false);
            moveCardForSelection(card, targetPane, baseX, baseY, false);
        }
    }

    private StackPane getPlayerPaneByIndex(int playerIndex) {
        return switch (playerIndex) {
            case 0 -> southPane;
            case 1 -> westPane;
            case 2 -> northPane;
            case 3 -> eastPane;
            default -> throw new IllegalStateException("Invalid player index: " + playerIndex);
        };
    }

    private void moveCardForSelection(Node card, StackPane targetPane, double baseX, double baseY, boolean select) {
        double offset = select ? 30 : 0;
        TranslateTransition moveTransition = new TranslateTransition(Duration.seconds(0.2), card);
        int playerIndex = -1;
        for (Map.Entry<Actor, Integer> entry : playerOriginalIndex.entrySet()) {
            StackPane playerPane = getPlayerPaneByIndex(entry.getValue());
            if (playerPane.equals(targetPane)) {
                playerIndex = entry.getValue();
                break;
            }
        }
        if (playerIndex == 0) {
            moveTransition.setToX(baseX);
            moveTransition.setToY(baseY - offset);
        } else if (playerIndex == 2) {
            moveTransition.setToX(baseX);
            moveTransition.setToY(baseY + offset);
        } else if (playerIndex == 1) {
            moveTransition.setToX(baseX + offset);
            moveTransition.setToY(baseY);
        } else if (playerIndex == 3) {
            moveTransition.setToX(baseX - offset);
            moveTransition.setToY(baseY);
        } else {
            moveTransition.setToX(baseX);
            moveTransition.setToY(baseY);
        }
        moveTransition.play();
    }

    private void clearDeckPane() {
        deckPane.getChildren().clear();
        deck.clear();
    }
}