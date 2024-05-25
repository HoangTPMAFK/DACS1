package com.entity.demo;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AddFriendController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button noBtn;

    @FXML
    private AnchorPane panel;

    @FXML
    private Button yesBtn;

    @FXML
    private Label addFriendWithLabel;

    private String sender;
    private String receiver;
    private Stage stage;

    public void setSender (String sender) {
        this.sender = sender;
    }
    public void setReceiver(String receiver) {
        this.receiver = receiver;
        addFriendWithLabel.setText("Add friend with " + receiver + "?");
    }
    public void CloseStage() {
        stage = (Stage) panel.getScene().getWindow();
        stage.close();
    }

    @FXML
    void initialize() {
        yesBtn.setOnAction(event -> {
            DBUtil.sendFriendRequest(sender, receiver);
            CloseStage();
        });
        noBtn.setOnAction(event -> {
            CloseStage();
        });
    }
}
