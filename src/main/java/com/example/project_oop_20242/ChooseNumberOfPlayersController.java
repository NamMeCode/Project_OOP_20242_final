package com.example.project_oop_20242;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChooseNumberOfPlayersController implements Initializable {
    private int gameID = -1;
    private boolean playWithBots = false;

    @FXML private Button _2PlayerButton;
    @FXML private Button _3PlayerButton;
    @FXML private Button _4PlayerButton;
    @FXML private Button backToChooseGameplayButton;

    public void initData(int gameID, boolean playWithBots) {
        this.gameID = gameID;
        this.playWithBots = playWithBots;
    }

    public void enterButtonEffect(Button button) {
        ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
        st.setToX(1.2);
        st.setToY(1.2);
        st.play();
        DropShadow ds = new DropShadow();
        ds.setColor(Color.YELLOW);
        ds.setRadius(10);
        button.setEffect(ds);
    }

    public void exitButtonEffect(Button button) {
        ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();
        button.setEffect(null);
    }

    @FXML public void _2PlayersEnter() { enterButtonEffect(_2PlayerButton); }
    public void _2PlayersExit() { exitButtonEffect(_2PlayerButton); }

    public void _3PlayersEnter() { enterButtonEffect(_3PlayerButton); }
    public void _3PlayersExit() { exitButtonEffect(_3PlayerButton); }

    public void _4PlayersEnter() { enterButtonEffect(_4PlayerButton); }
    public void _4PlayersExit() { exitButtonEffect(_4PlayerButton); }

    public void backToChooseGameplayEnter() { enterButtonEffect(backToChooseGameplayButton); }
    public void backToChooseGameplayExit() { exitButtonEffect(backToChooseGameplayButton); }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            Scene scene = _2PlayerButton.getScene();
            if (scene != null) {
                scene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ESCAPE) {
                        closeStage();
                    }
                });
            }
        });
    }

    private void closeStage() {
        Stage stage = (Stage) _2PlayerButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void backToChooseGameplay() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ChooseGameplay.fxml"));
            Scene newScene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) _2PlayerButton.getScene().getWindow();
            stage.setScene(newScene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML public void start2PlayerGame() { startGame(2); }
    @FXML public void start3PlayerGame() { startGame(3); }
    @FXML public void start4PlayerGame() { startGame(4); }

    private void startGame(int playerCount) {
        try {
            String fxmlPath;
            String gameType;

            switch (gameID) {
                case 1 -> {
                    fxmlPath = "ThirteenGame.fxml";
                    gameType = "ThirteenN";
                }
                case 2 -> {
                    fxmlPath = "ThirteenGame.fxml";
                    gameType = "ThirteenS";
                }
                default -> throw new IllegalArgumentException("Invalid gameID: " + gameID);
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());

            ThirteenController controller = loader.getController();
            controller.setUpGame(playerCount, gameType, playWithBots);

            Stage stage = (Stage) _2PlayerButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
