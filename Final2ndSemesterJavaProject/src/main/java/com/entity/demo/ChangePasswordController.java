package com.entity.demo;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ChangePasswordController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane panel;

    @FXML
    private Button cancleBtn;

    @FXML
    private Button changePasswordBtn;

    @FXML
    private PasswordField newPassword;

    @FXML
    private PasswordField oldPassword;

    @FXML
    private PasswordField retypePassword;

    @FXML
    private Label cantChangPasswordLabel;

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
        cantChangPasswordLabel.setVisible(false);
        changePasswordBtn.setOnAction(event -> {
            System.out.println(userId + " - " + oldPassword.getText());
            if (retypePassword.getText().equals(newPassword.getText())) {
                if (DBUtil.changePassword(userId, oldPassword.getText(), newPassword.getText())) {
                    CloseStage();
                } else {
                    cantChangPasswordLabel.setVisible(true);
                    cantChangPasswordLabel.setText("Your old password is incorrect");
                }
            } else {
                cantChangPasswordLabel.setVisible(true);
                cantChangPasswordLabel.setText("You must retype new password");
            }
        });
        cancleBtn.setOnAction(event -> {
            CloseStage();
        });
    }

}
