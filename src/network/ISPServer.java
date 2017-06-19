package network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import main.MainModel;
import javafx.util.Callback;

/**
 * Created by Ningchen Wang on 6/17/2017.
 */
public class ISPServer extends Thread {
    private boolean debug = false;

    public Inet4Address localAddress;
    private ServerSocket serverSocket;
    //private HashMap<Inet4Address, Socket> clientSockets;
    private int networkPrefixLength;
    boolean runningFlag = true;

    List<Callback<DatagramPacket, String>> callbacks;
    
    final static public byte sourceVerify = 0;
    final static public byte sourceElect = 1;
    final static public byte sourceUnlock = 2;


    public ISPServer(Inet4Address localAddress) throws IOException {
        this.localAddress = localAddress;
        networkPrefixLength = 24;
        if (!debug) {
            serverSocket = new ServerSocket(9874, 100, localAddress);
            //clientSockets = new HashMap<Inet4Address, Socket>();
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
            while (runningFlag) {
            	Socket socket = serverSocket.accept();
            	InputStream inputStream = socket.getInputStream();
            	int receiveLen = inputStream.read(receiveData);
            	socket.close();
            	InetAddress IPAddress = socket.getInetAddress();
            	int port = socket.getPort();
//                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
//                serverSocket.receive(receivePacket);
//                InetAddress IPAddress = receivePacket.getAddress();
//                System.out.println("RECEIVED: " + IPAddress);
//                int port = receivePacket.getPort();
                if ((port == 9873 || port == 9874) && IPAddress instanceof Inet4Address && sameNetwork((Inet4Address)IPAddress)) {
                	// Message Handlers
                    /**
                     * run callbacks
                     */
                    for (Callback<DatagramPacket, String> cb: callbacks()){
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveLen);
                        receivePacket.setAddress(IPAddress);
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
//					*/
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

    public void kill() throws IOException {
    	serverSocket.close();
        runningFlag = false;
    }
    
    // sourceType should be one of three value ISPServer.sourceVerify(0), ISPServer.sourceElect(1), ISPServer.sourceUnlock(2)
    public void send(Inet4Address dst, byte[] content, byte sourceType) throws IOException {
    	byte[] buf;
    	buf = new byte[content.length + 1];
    	buf[0] = sourceType;
    	System.arraycopy(content, 0, buf, 1, content.length);
    	//DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, dst, 9874);
    	System.out.println("SEND: "+dst+" "+ new String(buf));
    	Socket clientSocket = new Socket();
    	clientSocket.setReuseAddress(true);
    	clientSocket.bind(new InetSocketAddress(localAddress, 9873));
    	clientSocket.connect(new InetSocketAddress(dst, 9874));
    	OutputStream outputStream = clientSocket.getOutputStream();
    	outputStream.write(buf);
    	clientSocket.shutdownOutput();
    	clientSocket.close();
    }
    
    public void sendBroadcast(byte[] content, byte sourceType) throws IOException {
    	for (Inet4Address n : MainModel.getPeerDetector().GetPeerAddresses()) {
        	this.send(n, content, sourceType);    		
    	}
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
