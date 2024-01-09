module com.example.projecttcp {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires javafx.media;


    opens com.example.projecttcp to javafx.fxml;
    exports com.example.projecttcp;
    exports com.example.projecttcp.protocol;
}