package main;

import com.sun.javafx.robot.impl.FXRobotHelper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import main.tools.BaseController;
import main.tools.SceneManager;
import network.ISPServer;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller extends BaseController{
    @FXML
    private Button start;
    @FXML
    private TextArea console;

    private MainModel mm;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // TODO (don't really need to do anything here).
        mm = new MainModel();
        println(console, "loading data...");
        Thread t = new Thread(new Task<Integer>() {
            @Override
            protected Integer call() throws Exception{
                mm.load();
                Platform.runLater(()->{
                    println(console, "loading complete");
                    println(console, mm.getUser());
                });
                return 1;
            }
        });
        t.start();
    }

    public void start(ActionEvent actionEvent){
        // create hotspot...
        println(console,"setting up the subnet...");
        start.setDisable(true);
        Thread t = new Thread(new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                /**
                 * set up subnet
                 */
                int result = mm.connectWifi();
                if (result >0){
                    MainModel.getIspServer().start();
                }
                Platform.runLater(()->{
                    switch (result){
                        case -1:
                            /*
                            println(console, "failed to set up the subnet!");
                            start.setDisable(false);
                            break;
                            */
                        case 1:
                            ObservableList<Stage> stages1 = FXRobotHelper.getStages();
                            stages1.get(0).setScene(SceneManager.create("auth/hotspot/authhotspot.fxml"));
                            break;
                        case 2:
                        case 3:
                            ObservableList<Stage> stages = FXRobotHelper.getStages();
                            stages.get(0).setScene(SceneManager.create("auth/client/authclient.fxml"));
                            break;
                    }
                });
                return 0;
            }
        });
        t.start();
    }

    private void register(ActionEvent actionEvent){
        // send message to server
        println(console, "registering to the hotspot server...");
        Thread t = new Thread(new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                /**
                 * register self to hotspot
                 */
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ie){
                    ie.printStackTrace();
                }
                println(console, "already registered");
                Platform.runLater(()->{
                });
                return 0;
            }
        });
        t.start();
    }

}
