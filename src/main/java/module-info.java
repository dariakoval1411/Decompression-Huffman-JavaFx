module com.example.huffmanencoding {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.huffmanencoding to javafx.fxml;
    exports com.example.huffmanencoding;
}