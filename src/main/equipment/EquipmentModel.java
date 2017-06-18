package main.equipment;

import javafx.collections.ObservableList;
import javafx.util.Callback;
import main.MainModel;
import main.auth.AuthModel;
import main.model.Equipment;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wesley shi on 2017/6/14.
 */
public class EquipmentModel {
    ObservableList<Equipment> equipments;
    public EquipmentModel(ObservableList<Equipment> equips){
        equipments = equips;
    }

    Callback<byte[], String> response = new Callback<byte[], String>() {
        @Override
        public String call(byte[] param) {
            /**
             * solve equipment opening broadcast
             * if commander:
             *   solve opening request & open equipment
             *
             */
            return null;
        }
    };

    public void loadData(){
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("equipments.obj"));
            ArrayList<Equipment> ae = ((ArrayList<Equipment>) ois.readObject());
            equipments.addAll(ae);
        } catch (Exception e){
            e.printStackTrace();
        }
        // read from file...
    }

    public void addItem(Equipment e){
        equipments.add(e);
    }

    public void removeItem(Equipment e){
        equipments.remove(e);
    }

    /**
     * send opening equipment request
     * @param equipment
     */
    public void openEquipment(Equipment equipment){
        if (AuthModel.isCommander()){
            // add directly
        } else{
            // sending request to commander
            Thread t = new Thread(()->{
                // send message
                try{
                    Thread.sleep(2000);
                } catch (InterruptedException ie){
                    ie.printStackTrace();
                }
            });
            t.start();
        }
    }

    public void broadcastOpening(Equipment equipment){
        if (!AuthModel.isCommander()){
            // do nothing
            return;
        }
        // broadcast to all troopers
        // ....
    }

}
