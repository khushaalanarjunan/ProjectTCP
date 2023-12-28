package com.example.projecttcp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class ChatController {
    // Login Page
    @FXML
    private Button btn_login;

    //Chat Page
    @FXML
    private ScrollPane paneChat;
    @FXML
    private VBox chatContainer;
    @FXML
    private TextArea txt_message;
    @FXML
    private Button btn_send;
    @FXML
    private Button btn_shift;
    private Boolean shift=true;
    @FXML
    private Button btn_logout;


    private ArrayList<Integer> a = new ArrayList<>();

    private void addChatMessage(String message, boolean isSender) {
        Label chatMessage = new Label("Khushaalan :" + message);
        chatMessage.setWrapText(true);
        String backgroundColor = isSender ? "-fx-background-color: rgba(0, 41, 255, 0.83);" : "-fx-background-color: rgba(220, 220, 220, 0.83);";
        String textColor = isSender ? "-fx-text-fill: white;" : "-fx-text-fill: black;";

        chatMessage.setStyle(
                backgroundColor +
                        "-fx-border-radius: 15; " +
                        "-fx-padding: 10; " +
                        "-fx-font-size: 16; " +
                        textColor
        );

        Region arrow = createArrow(isSender);

        HBox messageContainer = new HBox();
        messageContainer.setAlignment(isSender ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        if(isSender) {
            messageContainer.getChildren().addAll(chatMessage, arrow);
        }
        else{
            messageContainer.getChildren().addAll(arrow,chatMessage);
        }
        HBox.setHgrow(messageContainer, Priority.ALWAYS);
        chatContainer.getChildren().add(messageContainer);
    }

    @FXML
    private void shiftisSender(){
        if(shift){
            shift=false;
        }
        else{
            shift=true;
        }
    }
    private Region createArrow(boolean isSender) {
        Region arrow = new Region();
        arrow.setMinSize(10, 10);
        arrow.setMaxSize(10, 10);

        String arrowStyle = isSender ?
                "-fx-background-color: rgba(0, 41, 255, 0.83);" :
                "-fx-background-color: rgba(220, 220, 220, 0.83);";

        arrow.setStyle(arrowStyle);

        return arrow;
    }


    @FXML
    private void refreshChat() {
        addChatMessage(txt_message.getText(), shift);
        txt_message.setText("");
    }
    @FXML
    private void initialize() {
        try {
            btn_send.setDisable(true);

            txt_message.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    btn_send.setDisable(newValue.trim().isEmpty());
                }
            });

            txt_message.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.ENTER && !btn_send.isDisabled()) {
                    event.consume();
                    sendButtonClicked(new ActionEvent());
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    private void sendButtonClicked(ActionEvent event) {
        refreshChat();
    }
    @FXML
    private void login(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("chat-list.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 700, 700);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);

    }

    @FXML
    private void logout(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 700, 700);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);

    }

}