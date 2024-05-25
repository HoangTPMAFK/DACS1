package com.entity.demo;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button loginBtn;

    @FXML
    private Label loginFailedLabel;

    @FXML
    private AnchorPane loginPane;

    @FXML
    private PasswordField loginPassword;

    @FXML
    private Tab loginTab;

    @FXML
    private TextField loginUsername;

    @FXML
    private TabPane panel;

    @FXML
    private PasswordField retypePassword;

    @FXML
    private Button signupBtn;

    @FXML
    private Label signupFailedLabel;

    @FXML
    private AnchorPane signupPane;

    @FXML
    private PasswordField signupPassword;

    @FXML
    private TextField signupUsername;

    @FXML
    private Tab singupTab;

    private ClientController clientController;
    private Stage stage;

    public void setParentController (ClientController clientController) {
        this.clientController = clientController;
    }

    public void CloseStage() {
        stage = (Stage) panel.getScene().getWindow();
        stage.close();
    }

    @FXML
    void initialize() {
        loginFailedLabel.setVisible(false);
        signupFailedLabel.setVisible(false);
        loginBtn.setOnAction(event -> {
            System.out.println(loginUsername.getText() + " - " + loginPassword.getText());
            if (DBUtil.userExist(loginUsername.getText(), loginPassword.getText())) {
                System.out.println("Login success");
                clientController.setUserName(loginUsername.getText());
                CloseStage();
            } else {
                loginFailedLabel.setVisible(true);
            }
        });
        signupBtn.setOnAction(event -> {
            if (signupPassword.getText().equals(retypePassword.getText())) {
                if (DBUtil.addUser(signupUsername.getText(), signupPassword.getText())) {
                    System.out.println("Signup success");
                    clientController.setUserName(signupUsername.getText());
                    CloseStage();
                } else {
                    signupFailedLabel.setVisible(true);
                    signupFailedLabel.setText("Username already exists");
                }
            } else {
                signupFailedLabel.setVisible(true);
                signupFailedLabel.setText("You must re-type password");
            }
        });
    }

}
