package com.example.project_oop_20242;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());


        stage.setTitle("Card games");
        Image iconImage = new Image(getClass().getResourceAsStream("/com/example/project_oop_20242/cards/img.png"));
        //scene.getStylesheets().add(getClass().getResource("/com/example/project_oop_20242/MenuStyle.css").toExternalForm());
        stage.getIcons().add(iconImage);
        stage.setScene(scene);

        // ⚠️ Thêm 3 dòng này để đảm bảo KHÔNG fullscreen
        stage.setFullScreen(false);      // Tắt fullscreen nếu đang bật
        stage.setMaximized(true);       // Không cho cửa sổ tự động full màn hình
        stage.setResizable(true);        // Cho resize lại nếu muốn

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}