package main.auth;

import javafx.collections.ObservableList;
import javafx.util.Callback;
import main.MainModel;
import main.model.Trooper;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wesley shi on 2017/6/17.
 */
public class AuthModel {
    private static boolean commander;
    private static String commanderIP;

    protected ObservableList<Trooper> troopers;

    protected List<Callback<byte[], String>> callbacks = new ArrayList<>();
    protected AuthModel(ObservableList<Trooper> ts){
        troopers = ts;
        callbacks.add(new Callback<byte[], String>() {
            @Override
            public String call(byte[] param) {
                /**
                 * solve
                 * 1. start authentication response
                 * 2. authentication response
                 * 3. choose commander #
                 */
                return "ok";
            }
        });
        // binding the callback to ispServer
        MainModel.getIspServer().setCallbacks(callbacks);
    }

    public void refreshList(Inet4Address[] addresses){

    }

    public static boolean isCommander(){
        return commander;
    }

    public static String getCommanderIP() {
        return commanderIP;
    }
}
