package com.entity.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

public class ClientController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private VBox chatView;

    @FXML
    private TextField messageField;

    @FXML
    private AnchorPane panel;

    @FXML
    private Button sendBtn;

    @FXML
    private VBox accountPanel;

    @FXML
    private Button changePasswordBtn;

    @FXML
    private ScrollPane chatContainer;

    @FXML
    private TabPane chatMenu;

    @FXML
    private Button deleteAccountBtn;

    @FXML
    private Button groupChatBtn;

    @FXML
    private Label helloLabel;

    @FXML
    private Button logoutBtn;

    @FXML
    private Tab privateChatTab;

    @FXML
    private VBox privateChatsContainer;

    @FXML
    private Tab publicChatTab;

    @FXML
    private VBox publicChatsContainer;

    @FXML
    private TableView<MemberManager> memberListView;

    @FXML
    private Tab friendRequestTab;

    @FXML
    private VBox friendRequestsContainer;

    @FXML
    private ScrollPane friendRequestPane;

    @FXML
    private ScrollPane privateChatPane;

    @FXML
    private ScrollPane publicChatPane;

    @FXML
    private TableColumn<MemberManager, String> memberColumn;

    private Stage stage;
    public static ObservableList<MemberManager> memberList = FXCollections.observableArrayList();

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ListenHandler listenHandler;
    private String username;
    private String receiverUsername;
    private int onlineUsersCount;
    private boolean publicChat;
    private boolean chatting;

    public void shutdown() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (!socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void send(ActionEvent event) {

    }

    @FXML
    void changePassword(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChangePassword.fxml"));
            Parent root = loader.load();
            ChangePasswordController changePasswordController = loader.getController();
            changePasswordController.setUsername(username);
            Stage stage1 = new Stage();
            stage1.setTitle("Change password");
            stage1.setScene(new Scene(root));
            stage1.show();
            stage1.setAlwaysOnTop(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void CloseStage() {
        stage = (Stage) panel.getScene().getWindow();
        stage.close();
    }

    @FXML
    void addFriend(MouseEvent event) {
        try {
            MemberManager member = memberListView.getSelectionModel().getSelectedItem();
            receiverUsername = member.getUsername();
            if (!receiverUsername.equals(username)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AddFriend.fxml"));
                Parent root = loader.load();
                AddFriendController addFriendController = loader.getController();
                addFriendController.setSender(username);
                addFriendController.setReceiver(receiverUsername);
                Stage stage1 = new Stage();
                stage1.setTitle("Add friend");
                stage1.setScene(new Scene(root));
                stage1.show();
                stage1.setAlwaysOnTop(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        try {

            socket = new Socket("127.0.0.1", 1917);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            memberColumn.setCellValueFactory(new PropertyValueFactory<MemberManager, String>("username"));
            memberListView.setItems(ClientController.memberList);
            
            messageField.setEditable(false);
            sendBtn.setDisable(true);

            sendBtn.setOnAction(event -> {
                String message = messageField.getText();
                if (publicChat) {
                    out.println(message);
                    System.out.println(username + ": " + message);
                } else { 
                    out.println("#>--->" + receiverUsername + ": " + message);
                    System.out.println(username + ": " + "#>--->" + receiverUsername + ": " + message);
                }
                messageField.setText("");
                if (message.equalsIgnoreCase("::exit")) {
                    shutdown();
                    stage = (Stage) panel.getScene().getWindow();
                    stage.close();
                }
            });

            logoutBtn.setOnAction(event -> {
                out.println("::exit");
                CloseStage();
            });

            deleteAccountBtn.setOnAction(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("DeleteAccount.fxml"));
                    Parent root = loader.load();
                    DeleteAccountController deleteAccountController = loader.getController();
                    deleteAccountController.setUsername(username);
                    Stage stage1 = new Stage();
                    stage1.setTitle("Delete account");
                    stage1.setScene(new Scene(root));
                    stage1.show();
                    stage1.setAlwaysOnTop(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            groupChatBtn.setOnAction(event -> {
                loadGroupChatMessage();
                out.println("::onGroupChat");
                publicChat = true;
                System.out.println(onlineUsersCount);
            });

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();
            LoginController loginController = loader.getController();
            loginController.setParentController(this);
            Stage stage1 = new Stage();
            stage1.setTitle("Login");
            stage1.setScene(new Scene(root));
            stage1.show();
            stage1.setAlwaysOnTop(true);

        } catch (IOException e) {
            shutdown();
            stage = (Stage) panel.getScene().getWindow();
            stage.close();
        }

    }

    public void setUserName(String username) {
        this.username = username;
        onlineUsersCount = 0;
        helloLabel.setText("Hello, " + username);
        out.println(username);
        publicChat = true;
        messageField.setEditable(true);
        sendBtn.setDisable(false);
        loadGroupChatMessage();
        DBUtil.loadContacts(this);
        loadFriendRequests();
        listenHandler = new ListenHandler();
        listenHandler.start();
    }
    public String getUsername() {
        return username;
    }
    public void loadGroupChatMessage() {
        chatView.getChildren().removeAll(chatView.getChildren());
        chatting = false;
        DBUtil.loadMessage(this);
        chatting = true;
    }
    public void loadPrivateMessage() {
        chatView.getChildren().removeAll(chatView.getChildren());
        DBUtil.loadDirectMessage(this, receiverUsername);
    }
    public void loadFriendRequests() {
        DBUtil.loadFriendRequests(this);
    }
    public void autoSrollDown() {
        chatContainer.setVvalue(chatContainer.getVmax());
    }
    public void addFriendRequestToContainer(String friendUsername) {
        HBox row = new HBox();
        Label friendUsernameLabel = new Label(friendUsername);
        Button yesBtn = new Button("Yes");
        Button noBtn = new Button("No");
        row.setMinHeight(30);
        row.setSpacing(7.0);
        yesBtn.setOnAction(event -> {
            DBUtil.acceptFriendRequest(this, username, friendUsernameLabel.getText(), true);
            row.setVisible(false);    
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    friendRequestsContainer.getChildren().remove(row);
                }
            });
        });
        noBtn.setOnAction(event -> {
            DBUtil.acceptFriendRequest(this, username, friendUsernameLabel.getText(), false);
            row.setVisible(false);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    friendRequestsContainer.getChildren().remove(row);
                }
            });
        });
        row.getChildren().add(friendUsernameLabel);
        row.getChildren().add(yesBtn);
        row.getChildren().add(noBtn);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                friendRequestsContainer.getChildren().add(row);
            }
        });
    }
    public void addContactToContainer(String friendUsername) {
        HBox row = new HBox();
        Button contact = new Button(friendUsername);
        contact.setOnAction(event -> {
            chatView.getChildren().removeAll(chatView.getChildren());
            DBUtil.loadDirectMessage(this, contact.getText());
            receiverUsername = contact.getText();
            publicChat = false;
            out.println("::notOnGroupChat");
        });
        row.getChildren().add(contact);
        contact.setMinWidth(200);
        contact.setMinHeight(30);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                privateChatsContainer.getChildren().add(row);
            }
        });
    }

    public void displayMessage(String inMessage) {
        String[] messageComponent = inMessage.split(": ", 2);
        if (messageComponent.length > 1) {
            String inUsername = messageComponent[0];
            String messageContent = messageComponent[1];
            Label chatMessageLabel = new Label(messageContent + "\n");
            chatMessageLabel.setWrapText(true);
            HBox row = new HBox();
            chatMessageLabel.setMaxWidth(300);
    
            Text text = new Text(chatMessageLabel.getText());
            new Scene(new Group(chatMessageLabel));
            text.applyCss(); 
            double width = text.getLayoutBounds().getWidth();
            row.setMinHeight(20*(Math.ceil(width / 290)) + 10);
            row.setFillHeight(true);
            
            chatMessageLabel.setPadding(new Insets(3, 5, 3, 5));
            if (inUsername.equals("Server")) {
                row.setAlignment(Pos.CENTER);
                row.getChildren().add(new Label(inUsername + ": " + messageContent));
            } else if (!inUsername.equals(username)) {
                row.setAlignment(Pos.CENTER_LEFT);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        chatView.getChildren().add(new Label(inUsername));
                    }
                });
                chatMessageLabel.setTextFill(Color.GREEN);
                chatMessageLabel.setBackground(new Background(
                    new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                chatMessageLabel.setMinHeight(20*(Math.ceil(width / 290)) + 10);
                row.getChildren().add(chatMessageLabel);
            } else {
                row.setAlignment(Pos.CENTER_RIGHT);
                chatMessageLabel.setTextFill(Color.YELLOWGREEN);
                chatMessageLabel.setBackground(new Background(
                    new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                chatMessageLabel.setMinHeight(20*(Math.ceil(width / 290)) + 10);
                row.getChildren().add(chatMessageLabel);
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    chatView.getChildren().add(row);
                    chatContainer.setVvalue(1.0);
                }
            });
            if (messageContent.contains(" left the chat!")) { // Loại bỏ user khỏi memberView cục bộ
                if (chatting) {
                    String removedUser = messageContent.split(" left the chat!")[0];
                    Iterator<MemberManager> it = memberList.iterator();
                    while (it.hasNext()) {
                        MemberManager item = it.next();
                        if (item.getUsername().equals(removedUser)) {
                            it.remove();
                            onlineUsersCount--;
                            System.out.println("User have been removed");
                            System.out.println(onlineUsersCount);
                        }
                    }
                    memberListView.setItems(ClientController.memberList);
                }
            }
        } else if (messageComponent.length == 1) {
            if (onlineUsersCount == 0) {
                ArrayList<String> onlineUsers = new ArrayList<>(
                    Arrays.asList(messageComponent[0].split(", "))
                );
                for (int i = 0; i < onlineUsers.size(); i++) {
                    memberList.add(new MemberManager(onlineUsers.get(i)));
                    System.out.println("User have been added");
                    onlineUsersCount = onlineUsersCount + 1;
                }
                System.out.println(onlineUsersCount);
            } else {
                MemberManager member = new MemberManager(messageComponent[0].split(", ")[0]);
                memberList.add(member);
                System.out.println("User have been added");
                onlineUsersCount = onlineUsersCount + 1;
            }
            memberListView.setItems(ClientController.memberList);
            System.out.println(onlineUsersCount);
        }
    }

    public class ListenHandler extends Thread {
        private String inMessage;
        public void run() {
            try {
                while ((inMessage = in.readLine()) != null) {
                    displayMessage(inMessage);
                }
            } catch (IOException e) {
                shutdown();
            }
        }
    }
}
