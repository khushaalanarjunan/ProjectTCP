package com.example.projecttcp;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private Button btn_login;

    @FXML
    private TextField txt_name;

    @FXML
    private TextField txt_ipaddress;

    @FXML
    private void login(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("chat-list.fxml"));
        try {
            Parent root = fxmlLoader.load();
            var chatController = (ChatController) fxmlLoader.getController();
            chatController.setUsername(txt_name.getText());
            chatController.setAddress(txt_ipaddress.toString());

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setOnHidden(e -> {
                chatController.close();
            });
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }

    }
}
