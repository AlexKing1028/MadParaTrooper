package main.equipment;

import javafx.collections.ObservableList;
import javafx.util.Callback;
import main.MainModel;
import main.auth.AuthModel;
import main.model.Equipment;
import main.tools.Constant;
import main.tools.DataTransfer;
import shamir.Key;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by wesley shi on 2017/6/14.
 */
public class EquipmentModel {
    private ObservableList<Equipment> equipments;
    private HashMap<Equipment, HashSet<Key>> collected_keys;
    public EquipmentModel(ObservableList<Equipment> equips){
        equipments = equips;
        callbacks.add(response);
        MainModel.getIspServer().setCallbacks(callbacks);
        if (AuthModel.isCommander()){
            collected_keys = new HashMap<>();
        }
    }

    List<Callback<DatagramPacket, String>> callbacks = new ArrayList<>();

    Callback<DatagramPacket, String> response = new Callback<DatagramPacket, String>() {
        @Override
        public String call(DatagramPacket param) {
            /**
             * 1. solve equipment opening broadcast
             * 2. if commander:
             *      solve opening request & open equipment
             *
             */

            byte[] data = param.getData();
            byte sourceType = data[0];
            String id = DataTransfer.bytesToInt(data, 1)+"";
            int len = equipments.size();
            int i =0;
            Equipment e = null;
            // find the equipment
            for (; i<len; i++) {
                e = equipments.get(i);
                if (e.getId().equals(id)) {
                    break;
                }
            }
            if (e == null){
                return "invalid id";
            }
            switch (sourceType){
                case Constant.Broadcast_OPEN_EQUIPMENT:
                    // find the equipment and update its state
                    if (!AuthModel.getCommanderIP().equals(param.getAddress().getHostAddress())){
                        return "invalid request";
                    }
                    e.setState(Equipment.State.Open.getValue());
                    equipments.set(i, e);
                    return "invalid id";
                case Constant.OPEN_REQUEST:
                    if (AuthModel.isCommander() && collected_keys!=null){
                        Key k = Key.fromBytes(data, 5);
                        HashSet<Key> sk = addKey(e, k);
                        unlock(e, sk);
                        return "ok";
                    }
                    return "unauthorized";
            }
            return "invalid request";
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

    private void unlock(Equipment e, HashSet<Key> sk){
        int result =  e.tryUnlock(sk);
        if (result == 1){
            // pass
            broadcastOpening(e);
        } else if (result == -2){
            // wrong keys
            sk.clear();
        } else{
            // people is not enough
        };
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
            addKey(equipment, equipment.getKey());
            unlock(equipment, collected_keys.get(equipment));
        } else{
            // sending request to commander
            try{
                Inet4Address commander = ((Inet4Address) InetAddress.getByName(AuthModel.getCommanderIP()));
                byte[] data = new byte[Key.getByteLen() + Integer.BYTES];
                int eid = Integer.parseInt(equipment.getId());
                Key k = equipment.getKey();
                data = DataTransfer.intToBytes(eid, data, 0);
                data = k.toBytes(data, Integer.BYTES);
                MainModel.getIspServer().send(commander, data, Constant.OPEN_REQUEST);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void broadcastOpening(Equipment equipment){
        if (!AuthModel.isCommander()){
            // do nothing
            return;
        }
        // broadcast to all troopers
        // ....
        byte[] content = new byte[1];
        content[0] = Byte.parseByte(equipment.getId());
        try{
            MainModel.getIspServer().sendBroadcast(content, Constant.Broadcast_OPEN_EQUIPMENT);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private HashSet<Key> addKey(Equipment e, Key k){
        if (!collected_keys.containsKey(e)){
            collected_keys.put(e, new HashSet<>());
        }
        HashSet<Key> sk = collected_keys.get(e);
        sk.add(k);
        return sk;
    }

}
