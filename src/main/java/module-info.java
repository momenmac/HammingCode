module org.example.hammingcode {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.hammingcode to javafx.fxml;
    exports org.example.hammingcode;
}