package com.example.project_oop_20242;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuController  implements Initializable {
    @FXML
    private ImageView imageView;
    @FXML
    private TextFlow chooseGameTextFlow;
    @FXML
    private Button thirteenSButton;
    @FXML
    private Button thirteenNButton;

    @FXML
    public void thirteenS() {
        loadChooseGameplayScene("ChooseGameplay.fxml",2);
    }
    @FXML
    public void thirteenN() {

        loadChooseGameplayScene("ChooseGameplay.fxml",1);
    }

    public void loadChooseGameplayScene(String sceneName,int gameID) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(sceneName));
            Scene newScene = new Scene(fxmlLoader.load(), 1536, 1024);
            ChooseGameplayController controller = fxmlLoader.getController();
            controller.setGameID(gameID);
            Stage stage = (Stage) thirteenSButton.getScene().getWindow();
            stage.setScene(newScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
    public void thirteenSEnter() {

        enterButtonEffect(thirteenSButton);
    }
    public void thirteenSExit() {

        exitButtonEffect(thirteenSButton);
    }
    public void thirteenNEnter() {

        enterButtonEffect(thirteenNButton);
    }
    public void thirteenNExit() {

        exitButtonEffect(thirteenNButton);
    }

    public void setIconForButton(Button button, String imageAdress,int width, int height) {
        Image ButtonImage = new Image(getClass().getResourceAsStream("/com/example/project_oop_20242/cards/"+imageAdress));
        ImageView ButtonImageView = new ImageView(ButtonImage);

        ButtonImageView.setFitWidth(width);
        ButtonImageView.setFitHeight(height);

        button.setGraphic(ButtonImageView);
        button.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
    }
    private void closeStage() {
        Stage stage = (Stage) thirteenNButton.getScene().getWindow();
        stage.close();
    }
    public void initialize(URL location, ResourceBundle resources) {
            Platform.runLater(() -> {
                Scene scene = thirteenNButton.getScene();
                if (scene != null) {
                    scene.setOnKeyPressed(event -> {
                        if (event.getCode() == KeyCode.ESCAPE) {
                            closeStage();
                        }
                    });
                }
            });
            setIconForButton(thirteenNButton, "thirteenNIcon.jpg",150,150);
            setIconForButton(thirteenSButton, "thirteenSIcon.jpg",150,150);

            DropShadow dropShadow = new DropShadow();
            dropShadow.setColor(Color.YELLOW);
            dropShadow.setRadius(10);

            chooseGameTextFlow.setEffect(dropShadow);

            FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), chooseGameTextFlow);
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.5);
            fadeTransition.setCycleCount(Timeline.INDEFINITE);
            fadeTransition.setAutoReverse(true);
            fadeTransition.play();
    }
}