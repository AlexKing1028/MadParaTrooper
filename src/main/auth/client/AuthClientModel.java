package main.auth.client;

import javafx.collections.ObservableList;
import javafx.util.Callback;
import main.auth.AuthModel;
import main.model.Trooper;

import java.util.List;

/**
 * Created by wesley shi on 2017/6/14.
 */
public class AuthClientModel extends AuthModel{

    Callback<byte[], String> response = new Callback<byte[], String>() {
        @Override
        public String call(byte[] param) {
            /**
             * solve stop authentication response: send result to hotspot
             *
             */
            return null;
        }
    };

    public AuthClientModel(ObservableList<Trooper> ts) {
        super(ts);
    }

    public void addAll(List<Trooper> ts){
        troopers.addAll(ts);
    }

    /*
    public int sendVerification(int index, Trooper t){
        // verify a trooper
        t.setState(Trooper.State.AC.getValue());
        troopers.set(index, t);
        return 0;
    }
    */
}
