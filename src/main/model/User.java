package main.model;

import java.util.HashMap;
import java.util.List;

/**
 * Created by wesley shi on 2017/6/16.
 */
public class User {
    private int id;
    private int level;
    private String name;
    private String description;
    private String rsa_pub;
    private String rsa;
    private HashMap<Integer, String> rsa_pubs;

    public User(int id, int level, String name, String description) {
        this.id = id;
        this.level = level;
        this.name = name;
        this.description = description;
    }

    public String getRsa_pub() {
        return rsa_pub;
    }

    public void setRsa_pub(String rsa_pub) {
        this.rsa_pub = rsa_pub;
    }

    public String getRsa() {
        return rsa;
    }

    public HashMap<Integer, String> getRsa_pubs() {
        return rsa_pubs;
    }

    public void setRsa_pubs(HashMap<Integer, String> rsa_pubs) {
        this.rsa_pubs = rsa_pubs;
    }

    public void setRsa(String rsa) {
        this.rsa = rsa;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", level=" + level +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
