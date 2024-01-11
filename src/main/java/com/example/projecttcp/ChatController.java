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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.Media;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

    @FXML
    private Button btn_uploadImage;
    @FXML
    private Label lbl_selected_file;
    private Boolean shift = true;

    @FXML
    private Button btn_logout;

    String username;
    String address;
    ChatService chatService;
    byte[] image = null;

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

    private void addChatMessage(String message, byte[] image) {
        addChatMessage(new Chat(username, message, image));
    }

    public void addChatMessage(Chat chat) {
        Platform.runLater(() -> {

            Label chatMessage = new Label(String.format("%s : %s", chat.getUsername(), chat.getMessage()));
            Boolean isSender = chat.getUsername().equals(username);
            if (!isSender) {
                Media sound = new Media(
                        new File("src/main/resources/com/example/projecttcp/messagetone.mp3").toURI().toString());
                javafx.scene.media.MediaPlayer mediaPlayer = new javafx.scene.media.MediaPlayer(sound);
                mediaPlayer.play();
            }

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
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);" +
                            textColor);
            Region arrow = createArrow(isSender);
            arrow.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");

            HBox messageContainer = new HBox();
            HBox imageContainer = new HBox();
            messageContainer.setAlignment(isSender ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
            imageContainer.setAlignment(isSender ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
            VBox.setMargin(messageContainer, new Insets(4, 0, 4, 0));
            ImageView imageView = null;

            Region border = new Region();
            border.setStyle(
                    "-fx-background-color: transparent; -fx-border-color: black; -fx-border-width: 2; -fx-border-radius: 20;");
            border.setMinSize(100, 100);

            StackPane pane = new StackPane();
            pane.setStyle("-fx-border-radius: 100px; " +
                    "-fx-background-radius: 100px; " +
                    "-fx-padding: 10; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");

            if (chat.getImage() != null && chat.getImage().length > 0) {
                imageView = new ImageView(new Image(new ByteArrayInputStream(chat.getImage())));
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(true);
                pane.getChildren().add(imageView);
                // chatContainer.getChildren().add(imageView);
            } else {
                System.out.println("No image for this chat");
            }

            if (isSender) {
                if (imageView != null) {
                    imageContainer.getChildren().add(pane);
                }
                messageContainer.getChildren().addAll(chatMessage, arrow);
            } else {
                if (imageView != null) {
                    imageContainer.getChildren().add(pane);
                }
                messageContainer.getChildren().addAll(arrow, chatMessage);
            }
            HBox.setHgrow(messageContainer, Priority.ALWAYS);
            chatContainer.getChildren().add(messageContainer);
            if (imageView != null) {
                chatContainer.getChildren().add(imageContainer);
                image = null;
                btn_send.setDisable(true);
                btn_send.setVisible(false);
            }
            lbl_selected_file.setVisible(false);
            lbl_selected_file.setText("");
        });
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
            lbl_selected_file.setVisible(false);

            txt_message.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    btn_send.setDisable(newValue.isEmpty() && image == null);
                    btn_send.setVisible(!newValue.isEmpty() || image != null);
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
            Image imageupload = new Image(getClass().getResourceAsStream("icons8-upload-32.png"));
            btn_uploadImage.setGraphic(new ImageView(imageupload));
            paneChat.setFitToWidth(true);
            paneChat.lookup(".scroll-bar").setStyle("-fx-background-color: #000;");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sendButtonClicked(ActionEvent event) {
        String message = txt_message.getText();

        txt_message.setText("");
        addChatMessage(message, image);
        new Thread(() -> {
            try {
                chatService.sendMessage(message, image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void uploadImageButtonClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                image = Files.readAllBytes(selectedFile.toPath());
                btn_send.setDisable(false);
                btn_send.setVisible(true);
                lbl_selected_file.setVisible(true);
                lbl_selected_file.setText(selectedFile.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    public void close() {
        chatService.close();
    }
}