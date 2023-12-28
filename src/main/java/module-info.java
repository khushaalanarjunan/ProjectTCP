module com.example.projecttcp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.projecttcp to javafx.fxml;
    exports com.example.projecttcp;
}