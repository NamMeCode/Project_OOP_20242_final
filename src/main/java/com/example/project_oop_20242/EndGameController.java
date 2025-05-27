package com.example.project_oop_20242;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class EndGameController {
    @FXML
    private Label winnerLabel;

    public void setWinnerName(String name) {
        winnerLabel.setText("🏆 The winner is: " + name);
    }
}