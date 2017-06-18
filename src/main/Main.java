package main;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.tools.SceneManager;
import network.ISPServer;

import java.util.HashMap;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        SceneManager.app = this;
        primaryStage.setTitle("Paratroopers");
        primaryStage.setScene(SceneManager.create("main.fxml", 600, 600));
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (MainModel.getPeerDetector() == null)
                {
                    return;
                }
                MainModel.getPeerDetector().Stop();
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
