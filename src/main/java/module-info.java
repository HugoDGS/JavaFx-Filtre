module com.example.projet {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;


    opens com.example.projet to javafx.fxml;
    exports com.example.projet;
}