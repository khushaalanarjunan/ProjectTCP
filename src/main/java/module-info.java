module com.example.projecttcp {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;


    opens com.example.projecttcp to javafx.fxml;
    exports com.example.projecttcp;
    exports com.example.projecttcp.protocol;
}