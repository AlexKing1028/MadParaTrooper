package network;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import javafx.util.Callback;

/**
 * Created by Ningchen Wang on 6/17/2017.
 */
public class ISPServer extends Thread {
    private boolean debug = false;

    public Inet4Address localAddress;
    private DatagramSocket serverSocket;
    private DatagramSocket clientSocket;
    private int networkPrefixLength;
    boolean runningFlag = true;

    List<Callback<DatagramPacket, String>> callbacks;
    
    final static public byte sourceVerify = 0;
    final static public byte sourceElect = 1;
    final static public byte sourceUnlock = 2;


    public ISPServer(Inet4Address localAddress) throws SocketException {
        this.localAddress = localAddress;
        networkPrefixLength = 24;
        if (!debug) {
            serverSocket = new DatagramSocket(9874, localAddress);
            clientSocket = new DatagramSocket(9873, localAddress);
        }
    }

    public void setCallbacks(List<Callback<DatagramPacket, String>> callBacks){
        this.callbacks = callBacks;
    }

    public void addCallback(Callback<DatagramPacket, String> callback){
        if (callbacks == null){
            callbacks = new ArrayList<>();
        }
        callbacks.add(callback);
    }

    public void clearCallbacks(){
        callbacks = new ArrayList<>();
    }

    private List<Callback<DatagramPacket, String>> callbacks(){
        if (callbacks == null) callbacks = new ArrayList<>();
        return callbacks;
    }

    public void run() {
        try {
            byte[] receiveData = new byte[1024];
            byte[] sendData = new byte[1024];
            while (runningFlag) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                InetAddress IPAddress = receivePacket.getAddress();
                System.out.println("RECEIVED: " + IPAddress);
                int port = receivePacket.getPort();
                if ((port == 9873 || port == 9874) && IPAddress instanceof Inet4Address && sameNetwork((Inet4Address)IPAddress)) {
                	// Message Handlers
                    /**
                     * run callbacks
                     */
                    for (Callback<DatagramPacket, String> cb: callbacks()){
                        String result = cb.call(receivePacket);
                    }
                    /*
                	switch (receiveData[0]) {
					case ISPServer.sourceVerify:
						// Verify module handler
						break;
					case ISPServer.sourceElect:
						// Elect module handler
						break;
					case ISPServer.sourceUnlock:
						// Unlock module handler
						break;
					default:
						System.err.println("Unknown SourceType package received!");
						break;
					}
					*/
                }
            }
        }
        catch (SocketException e) {
            System.out.println(e.getMessage());
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void kill() {
    	serverSocket.close();
        runningFlag = false;
    }
    
    // sourceType should be one of three value ISPServer.sourceVerify(0), ISPServer.sourceElect(1), ISPServer.sourceUnlock(2)
    public void send(Inet4Address dst, byte[] content, byte sourceType) throws IOException {
    	byte[] buf;
    	buf = new byte[content.length + 1];
    	buf[0] = sourceType;
    	System.arraycopy(content, 0, buf, 1, content.length);
    	DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, dst, 9874);
    	clientSocket.send(sendPacket);
    }
    
    public void sendBroadcast(byte[] content, byte sourceType) throws IOException {
    	this.send((Inet4Address)InetAddress.getByName("255.255.255.255"), content, sourceType);
    }

    private boolean sameNetwork(Inet4Address IPAddress) {
    	byte[] ipaddr = IPAddress.getAddress();
    	byte[] localaddr = localAddress.getAddress();
    	final int bits = networkPrefixLength & 7;
    	final int bytes = networkPrefixLength >>> 3;
    	for (int i = 0; i < bytes; i++) {
    		if (ipaddr[i] != localaddr[i]) {
    			return false;
    		}
    	}
    	final int shift = 8 - bits;
    	if (bits != 0 && ipaddr[bytes] >>> shift != localaddr[bytes] >>> shift) {
    		return false;
    	}
    	return true;
    }
}
