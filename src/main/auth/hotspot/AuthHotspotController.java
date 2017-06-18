package main.auth.hotspot;

import com.sun.javafx.robot.impl.FXRobotHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.MainModel;
import main.model.Trooper;
import main.tools.BaseController;
import main.tools.SceneManager;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.URL;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * Created by wesley shi on 2017/6/13.
 */
public class AuthHotspotController extends BaseController{

    @FXML
    TableView<Trooper> iplist;
    @FXML
    Button authentication;
    @FXML
    Button refresh;
    @FXML
    Button back;
    @FXML
    TextArea console;
    @FXML
    Button stop;
    @FXML
    Button choose_commander;
    @FXML
    TableColumn<Trooper, String> column_id;
    @FXML
    TableColumn<Trooper, String> column_ip;
    @FXML
    TableColumn<Trooper, String> column_state;


    AuthHotspotModel ahm;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO (don't really need to do anything here).
        ObservableList<Trooper> troopers = FXCollections.observableArrayList();
        ahm = new AuthHotspotModel(troopers);
        iplist.setItems(troopers);
        // mock data
        ahm.addItem(new Trooper("1000", "192.168.0.2"));
        ahm.addItem(new Trooper("1001", "192.168.0.8"));
        ahm.addItem(new Trooper("1003", "192.168.0.3"));
        ahm.addItem(new Trooper("1005", "192.168.0.5"));
        // connect to each column...
        column_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        column_ip.setCellValueFactory(new PropertyValueFactory<>("ip"));
        column_state.setCellValueFactory(new PropertyValueFactory<>("state"));
        iplist.setRowFactory(tv->{
            TableRow<Trooper> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2&&(!row.isEmpty())){
                    Trooper t = row.getItem();
                    if (t.getState().equals("ok")){
                        return;
                    }
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("confirm");
                    alert.setHeaderText("send rsa verification message to trooper"+ t.getId() + "?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get()==ButtonType.OK){
                        // ahm.sendVerification(row.getIndex(), t);
                        Platform.runLater(()->{
                            console.appendText("equipment"+ t.getId()+" is agreed!\n");
                        });
                    }
                }
            });
            return row;
        });
    }

    public void startAuthentication(ActionEvent actionEvent){
        // send message to other troopers
        println(console, "start authentication!");
        authentication.setDisable(true);
        stop.setDisable(false);
        ahm.startAuth();
    }

    public void stopAuthentication(ActionEvent actionEvent){
        println(console, "stop authentication!!!!");
        authentication.setDisable(false);
        stop.setDisable(true);
    }

    public void chooseCommander(ActionEvent actionEvent){
        println(console, "choosing commander");
        // send request
        Thread t = new Thread(()->{
            int r = new Random().nextInt(3000);
            try {
                Thread.sleep(r);
            }catch (InterruptedException ie){
                ie.printStackTrace();
            }
            Platform.runLater(()->{
                // println(console, "already chosen");
                // jump to equipment opening page
                ObservableList<Stage> stages = FXRobotHelper.getStages();
                stages.get(0).setScene(SceneManager.create("equipment/equipment.fxml"));
            });
        });
        t.start();
    }

    public void refresh(ActionEvent actionEvent){
        //Inet4Address[] addresses = MainModel.getPeerDetector().GetPeerAddresses();
        Inet4Address[] addresses = new Inet4Address[10];
        for (int i=0; i< 10; i++){
            try{
                addresses[i] = ((Inet4Address) Inet4Address.getByName("192.168.0." + i));
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        ahm.refreshList(addresses);
    }

    public void back(ActionEvent actionEvent){
        // stop all
        ObservableList<Stage> stages = FXRobotHelper.getStages();
        stages.get(0).setScene(SceneManager.create("main.fxml"));
    }
}
