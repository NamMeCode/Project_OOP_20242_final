module com.example.project_oop_20242 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.project_oop_20242 to javafx.fxml;
    exports com.example.project_oop_20242;
}