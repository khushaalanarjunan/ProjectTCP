package com.example.projecttcp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.List;

import com.example.projecttcp.protocol.Chat;

public class ChatController {
    // Chat Page
    @FXML
    private ScrollPane paneChat;
    @FXML
    private VBox chatContainer;
    @FXML
    private TextArea txt_message;
    @FXML
    private Button btn_send;
    private Boolean shift = true;

    @FXML
    private Button btn_logout;

    String username;
    String address;
    ChatService chatService;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private void addChatMessage(String message) {
        addChatMessage(new Chat(username, message));
    }

    public void addChatMessage(Chat chat) {
        Platform.runLater(() -> {
            Label chatMessage = new Label(String.format("%s : %s", chat.getUsername(), chat.getMessage()));
            Boolean isSender = chat.getUsername().equals(username);
            chatMessage.setWrapText(true);
            String backgroundColor = isSender ? "-fx-background-color: #005C4B;"
                    : "-fx-background-color: #353535;";
            String textColor = isSender ? "-fx-text-fill: #D3D3D3;" : "-fx-text-fill: #D3D3D3;";

            chatMessage.setStyle(
                    backgroundColor +
                            "-fx-border-radius: 15; " +
                            "-fx-background-radius: 15; " +
                            "-fx-padding: 10; " +
                            "-fx-font-size: 16; " +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);"+
                            textColor);
            Region arrow = createArrow(isSender);
            arrow.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");

            HBox messageContainer = new HBox();
            messageContainer.setAlignment(isSender ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
            VBox.setMargin(messageContainer, new Insets(4, 0, 4, 0));
            if (isSender) {
                messageContainer.getChildren().addAll(chatMessage, arrow);
            } else {
                messageContainer.getChildren().addAll(arrow, chatMessage);
            }
            HBox.setHgrow(messageContainer, Priority.ALWAYS);
            chatContainer.getChildren().add(messageContainer);
        });
    }

    @FXML
    private void shiftisSender() {
        if (shift) {
            shift = false;
        } else {
            shift = true;
        }
    }

    private Region createArrow(boolean isSender) {
        Region arrow = new Region();
        arrow.setMinSize(10, 10);
        arrow.setMaxSize(10, 10);

        String arrowStyle = isSender ? "-fx-background-color: #005C4B;"
                : "-fx-background-color: #353535;";

        arrow.setStyle(arrowStyle);

        return arrow;
    }

    public void initializeMessages(List<Chat> chats) {
        Platform.runLater(() -> {
            for (Chat chat : chats) {
                addChatMessage(chat);
            }
        });
    }


    @FXML
    private void initialize() {
        try {

            chatService = new ChatService(this, address);
            new Thread(chatService).start();


            btn_send.setDisable(true);
            btn_send.setVisible(false);


            txt_message.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    btn_send.setDisable(newValue.isEmpty());
                    btn_send.setVisible(!newValue.isEmpty());
                }
            });

            txt_message.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.ENTER && !btn_send.isDisabled()) {
                    event.consume();
                    sendButtonClicked(new ActionEvent());
                }
            });
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }

        try {
            Image imagesend = new Image(getClass().getResourceAsStream("icons8-send-32.png"));
            btn_send.setGraphic(new ImageView(imagesend));
            Image imagelogout = new Image(getClass().getResourceAsStream("icons8-logout-32.png"));
            btn_logout.setGraphic(new ImageView(imagelogout));
            paneChat.setFitToWidth(true);
            paneChat.lookup(".scroll-bar").setStyle("-fx-background-color: #000;");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    private void sendButtonClicked(ActionEvent event) {
        String message = txt_message.getText();
        txt_message.setText("");
        addChatMessage(message);
        new Thread(()->{
            try {
                chatService.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void logout(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 700, 700);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        close();
    }

    public void close(){
        chatService.close();
    }
}