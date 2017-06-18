package main.auth.hotspot;

import javafx.collections.ObservableList;
import javafx.util.Callback;
import main.MainModel;
import main.auth.AuthModel;
import main.model.Trooper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wesley on 2017/6/13.
 */
public class AuthHotspotModel extends AuthModel{

    Callback<byte[], String> responce = new Callback<byte[], String>() {
        @Override
        public String call(byte[] param) {
            /**
             * solve stopping authentication response..
             * select the trusted troopers
             */
            filterTroopers(param);
            return "ok";
        }
    };


    public AuthHotspotModel(ObservableList<Trooper> ts){
        super(ts);
        callbacks.add(responce);
    }

    public void addItem(Trooper t){
        troopers.add(t);
    }

    /*
    public int sendVerification(int index, Trooper t){
        t.setState(Trooper.State.AC.getValue());
        troopers.set(index, t);
        // verify a trooper
        return 0;
    }
     */

    /**
     * broadcast to all users to start authentication
     */
    public void startAuth(){

    }

    /**
     * broadcast to stop all auth
     */
    public void stopAuth(){

    }

    /**
     *
     * @return trusted troopers
     */
    private List<Trooper> filterTroopers(byte[] param){

        return null;
    }

    /**
     * get the trusted member
     * @param graph
     * @param size
     * @param host
     * @return
     */
    private List<Integer> filtering(boolean[][] graph, int size, int host){
        List<Integer> li = new ArrayList<>();
        li.add(host);
        for (int i=0; i<size; i++){
            if (i!=host && graph[host][i]){
                li.add(i);
            }
        }
        return li;
    }
}
