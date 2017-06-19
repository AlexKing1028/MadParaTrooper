package main.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by wesley on 2017/6/13.
 */
public class Trooper {
    private StringProperty ip;
    private StringProperty id;
    private StringProperty state;

    public enum State{
        AC("accept"),
        RE("reject"),
        NO("none");
        private String value;
        State(String v){
            value = v;
        }
        public String getValue(){
            return value;
        }
    }

    public Trooper(String id, String ip){
        setId(id);
        setIp(ip);
        // setState(State.NO.getValue());
        setState(State.NO.getValue());
    }

    public StringProperty ip(){
        if (ip==null) ip=new SimpleStringProperty(this, "ip");
        return ip;
    }
    public void setIp(String value){
        ip().set(value);
    }
    public String getIp(){
        return ip().get();
    }

    public StringProperty id(){
        if (id==null) id=new SimpleStringProperty(this, "id");
        return id;
    }
    public void setId(String value){
        id().set(value);
    }
    public String getId(){
        return id().get();
    }

    public StringProperty state(){
        if (state==null) state=new SimpleStringProperty(this, "state");
        return state;
    }
    public void setState(String value){
        state().set(value);
    }
    public String getState(){
        return state().get();
    }
}
