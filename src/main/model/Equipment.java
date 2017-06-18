package main.model;

import shamir.Box;
import shamir.Key;

import java.io.Serializable;

/**
 * Created by wesley shi on 2017/6/13.
 */
public class Equipment implements Serializable{
    String id;
    Box box;
    Key key;
    String description;

    public Equipment(String id, Box box, Key key, String description) {
        this.id = id;
        this.description = description;
        this.box = box;
        this.key = key;
    }

    public Key getKey() {
        return key;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString(){
        return id + " " + description;
    }

    @Override
    public boolean equals(Object o){
        if (o == null || !(o instanceof Equipment)){
            return false;
        }
        Equipment e = (Equipment)o;
        return e.id == id;
    }
}
