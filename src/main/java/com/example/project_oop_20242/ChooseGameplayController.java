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

public class ChooseGameplayController implements Initializable {
    private int gameID;

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    @FXML
    private Button playWithBotsButton;
    @FXML
    private Button playWithPlayersButton;
    @FXML
    private Button backToMenuButton;

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

    @FXML public void playWithBotsEnter() { enterButtonEffect(playWithBotsButton); }
    public void playWithBotsExit() { exitButtonEffect(playWithBotsButton); }

    public void playWithPlayersEnter() { enterButtonEffect(playWithPlayersButton); }
    public void playWithPlayersExit() { exitButtonEffect(playWithPlayersButton); }

    public void backToMenuEnter() { enterButtonEffect(backToMenuButton); }
    public void backToMenuExit() { exitButtonEffect(backToMenuButton); }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            Scene scene = playWithBotsButton.getScene();
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
        Stage stage = (Stage) playWithBotsButton.getScene().getWindow();
        stage.close();
    }

    public void backToMenu() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Menu.fxml"));
            Scene newScene = new Scene(fxmlLoader.load(), 1536, 1024);
            Stage stage = (Stage) playWithBotsButton.getScene().getWindow();
            stage.setScene(newScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void setPlayWithBotsScence() {
        loadChooseNumberOfPlayersScene(true);
    }

    @FXML
    public void setPlayWithPlayersScence() {
        loadChooseNumberOfPlayersScene(false);
    }

    public void loadChooseNumberOfPlayersScene(boolean playWithBots) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChooseNumberOfPlayers.fxml"));
            Scene newScene = new Scene(loader.load());
            ChooseNumberOfPlayersController controller = loader.getController();
            controller.initData(gameID, playWithBots);
            Stage stage = (Stage) playWithPlayersButton.getScene().getWindow();
            stage.setScene(newScene);
            stage.setFullScreen(false);
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
