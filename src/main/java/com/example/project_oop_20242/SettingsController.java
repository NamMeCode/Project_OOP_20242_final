package com.example.project_oop_20242;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    static boolean isTextMode ; // Mặc định là graphic mode
    @FXML private Label ChooseDisplayModeLabel;
    @FXML private Button backButton;
    @FXML private Button selectTextModeButton;
    @FXML private Button selectGraphicModeButton;
    @FXML
    public void selectTextModeEnter() {

        enterButtonEffect(selectTextModeButton);
    }
    public void selectTextModeExit() {

        exitButtonEffect(selectTextModeButton);
    }
    public void selectGraphicModeEnter() {

        enterButtonEffect(selectGraphicModeButton);
    }
    public void selectGraphicModeExit() {

        exitButtonEffect(selectGraphicModeButton);
    }
    public void backButtonEnter() {
        enterButtonEffect(backButton);
    }
    public void backButtonExit() {
        exitButtonEffect(backButton);
    }
    public void enterButtonEffect(Button button) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), button);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);
        scaleTransition.play();

        // Thêm hiệu ứng sáng lên (DropShadow)
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.YELLOW);
        dropShadow.setRadius(10);
        button.setEffect(dropShadow);
    }

    public void exitButtonEffect(Button button) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), button);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);
        scaleTransition.play();

        button.setEffect(null);
    }
    @FXML
    public void selectTextMode() {
        isTextMode = true;
    }

    @FXML
    public void selectGraphicMode() {
        isTextMode = false;
    }
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            Scene scene = backButton.getScene();
            if (scene != null) {
                scene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ESCAPE) {
                        closeStage();
                    }
                });
            }
        });

        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.YELLOW);
        dropShadow.setRadius(10);

        ChooseDisplayModeLabel.setEffect(dropShadow);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), ChooseDisplayModeLabel);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.5);
        fadeTransition.setCycleCount(Timeline.INDEFINITE);
        fadeTransition.setAutoReverse(true);
        fadeTransition.play();
    }
    private void closeStage() {
        Stage stage = (Stage) ChooseDisplayModeLabel.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void backToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Menu.fxml"));
            Scene newScene = new Scene(loader.load(), 1536, 1024);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(newScene);
            stage.setMaximized(true);
            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}