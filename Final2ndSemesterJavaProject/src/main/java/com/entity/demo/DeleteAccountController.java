package com.entity.demo;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class DeleteAccountController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button cancleBtn;

    @FXML
    private PasswordField confirmField;

    @FXML
    private Button deleteAccountBtn;

    @FXML
    private Label failedLabel;

    @FXML
    private AnchorPane panel;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField retypePassword;
    
    private Stage stage;
    private String userId;

    public void CloseStage() {
        stage = (Stage) panel.getScene().getWindow();
        stage.close();
    }

    public void setUsername(String userId) {
        this.userId = userId;
    }

    @FXML
    void initialize() {
        failedLabel.setVisible(false);
        if (retypePassword.getText().equals(password.getText())) {
            if (confirmField.getText().equals("confirm")) {
                if (DBUtil.deleteAccount(userId, password.getText())) {
                    CloseStage();
                } else {
                    failedLabel.setVisible(true);
                    failedLabel.setText("Your password is incorrect");
                }
            } else {
                failedLabel.setVisible(true);
                failedLabel.setText("You didn't type 'confirm in confirm field'");
            }
            
        } else {
            failedLabel.setVisible(true);
            failedLabel.setText("You must re-type your password");
        }
        cancleBtn.setOnAction(event -> {
            CloseStage();
        });
    }

}
