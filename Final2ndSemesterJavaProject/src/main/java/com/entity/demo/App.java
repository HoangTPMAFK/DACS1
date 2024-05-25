package com.entity.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
//        FXMLLoader loader = new FXMLLoader();
//        loader.setLocation(getClass().getResource("ClientScene.fxml"));
//        Parent root = loader.load();
        FXMLLoader load = new FXMLLoader(App.class.getResource("ClientScene.fxml"));

        stage.setScene(new Scene(load.load()));
        stage.setTitle("Chat application");
        stage.show();
    }
}

// Deserunt irure eu officia dolor est dolore voluptate.
// Fugiat dolore quis excepteur pariatur.
// Amet veniam aute ad commodo culpa. Voluptate magna minim proident excepteur
// consectetur irure do est. Velit occaecat irure ex exercitation nisi. Irure
// Lorem incididunt sint laboris non aute ut ut.
// Dolor commodo qui aliquip ea incididunt et nisi sunt nostrud non et. Qui
// mollit exercitation amet anim consequat reprehenderit veniam nulla
// adipisicing. Esse reprehenderit qui anim est enim ea ipsum dolore pariatur
// eiusmod aute pariatur. Voluptate ea qui esse occaecat reprehenderit nulla id
// cupidatat qui ad sit mollit enim enim. Nostrud elit proident incididunt
// pariatur nisi veniam enim esse ut duis.
