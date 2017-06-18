package main;

import main.model.Trooper;
import main.model.User;
import network.ISPServer;
import network.PeerDetector;
import network.WLANDetectorWrapper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.SocketException;
import java.time.Period;

/**
 * Created by wesley on 2017/6/14.
 */
public class MainModel {
    private static ISPServer ispServer;
    private static  PeerDetector peerDetector;

    User user;

    public User load(){
        try{
            BufferedReader br = new BufferedReader(new FileReader("user.txt"));
            int id = Integer.parseInt(br.readLine());
            int level = Integer.parseInt(br.readLine());
            String name = br.readLine();
            String description = br.readLine();
            user = new User(id, level, name, description);
            return user;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String getUser(){
        if (user == null) return "";
        return user.toString();
    }

    public int connectWifi(){
        try{
            int tmp = getPeerDetector().Initialize(user.getId());
            return tmp;
        } catch (Exception e){
            e.printStackTrace();
        }
        return -2;
    }

    /**
     * init ispServer
     */
    static void initIspServer(){
        try{
            //ispServer = new ISPServer(getPeerDetector().GetLocalAddress());
            ispServer = new ISPServer(null);
        }catch (SocketException se){
            se.printStackTrace();
        }
    }

    public static ISPServer getIspServer(){
        if (ispServer == null){
            initIspServer();
        }
        return ispServer;
    }
    public static PeerDetector getPeerDetector(){
        if (peerDetector == null){
            peerDetector = new PeerDetector();
        }
        return peerDetector;
    }
}
