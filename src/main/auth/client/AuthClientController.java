package main.auth.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import main.MainModel;
import main.model.Trooper;
import main.tools.BaseController;

import javax.swing.*;
import java.net.Inet4Address;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by wesley shi on 2017/6/14.
 */
public class AuthClientController extends BaseController{
    @FXML
    TableView<Trooper> iplist;
    @FXML
    TextArea console;
    @FXML
    Button refresh;
    @FXML
    TableColumn<Trooper, String> column_id;
    @FXML
    TableColumn<Trooper, String> column_ip;
    @FXML
    TableColumn<Trooper, String> column_state;

    AuthClientModel acm;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO (don't really need to do anything here).
        ObservableList<Trooper> troopers = FXCollections.observableArrayList();
        iplist.setItems(troopers);
        acm = new AuthClientModel(troopers);
        // mock data
        List<Trooper> lts = new ArrayList<>();
        lts.add(new Trooper("1000", "192.168.0.2"));
        lts.add(new Trooper("1001", "192.168.0.8"));
        lts.add(new Trooper("1003", "192.168.0.3"));
        lts.add(new Trooper("1005", "192.168.0.5"));
        acm.addAll(lts);
        // connect to each column...
        column_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        column_ip.setCellValueFactory(new PropertyValueFactory<>("ip"));
        column_state.setCellValueFactory(new PropertyValueFactory<>("state"));
        iplist.setRowFactory(tv->{
            TableRow<Trooper> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 0&&(!row.isEmpty())){
                    Trooper t = row.getItem();
                    if (t.getState().equals(Trooper.State.AC.getValue())){
                        return;
                    }
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("confirm");
                    alert.setHeaderText("send rsa verification message to trooper"+ t.getId() + "?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get()==ButtonType.OK){
                        // acm.sendVerification(row.getIndex(), t);
                        Platform.runLater(()->{
                            console.appendText("equipment"+ t.getId()+" is agreed!\n");
                        });
                    }
                }
            });
            return row;
        });
    }

    public void refresh(ActionEvent actionEvent){
        Inet4Address[] addresses = MainModel.getPeerDetector().GetPeerAddresses();
        acm.refreshList(addresses);
    }
}
