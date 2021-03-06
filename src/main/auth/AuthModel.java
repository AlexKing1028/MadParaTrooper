package main.auth;

import com.sun.glass.ui.Screen;
import com.sun.javafx.robot.impl.FXRobotHelper;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.Main;
import main.MainModel;
import main.model.Trooper;
import main.tools.Constant;
import main.tools.DataTransfer;
import main.tools.SceneManager;
import millionaire.LevelCompare;
import network.ISPServer;
import rsa.RSAUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by wesley shi on 2017/6/17.
 */
public class AuthModel {
	private static boolean commander;
	private static String commanderIP;

	protected ObservableList<Trooper> troopers;

	protected int acceptCount;
	protected boolean need_compare = true;

	protected List<Callback<DatagramPacket, String>> callbacks = new ArrayList<>();

	protected AuthModel(ObservableList<Trooper> ts) {
		troopers = ts;
		callbacks.add(new Callback<DatagramPacket, String>() {
			@Override
			public String call(DatagramPacket param) {
				/**
				 * solve 1. start authentication response 2. authentication
				 * response 3. choose commander #
				 */
				byte[] data = param.getData();
				byte sourceType = data[0];
				switch (sourceType) {
				case Constant.LEVEL_MESSAGE_INT:
					int levellength=data[1];
					int boundlength=data[2];
					int length=levellength+boundlength+3;
					byte[] levelMessageByte=new byte[levellength];
					byte[] boundMessageByte=new byte[boundlength];
					for(int i=3;i<levellength+3;i++){
						levelMessageByte[i-3]=data[i];
					}
					for(int i=levellength+3;i<length;i++){
						boundMessageByte[i-levellength-3]=data[i];
					}
					BigInteger levelMessage=new BigInteger(levelMessageByte);
					BigInteger boundMessage=new BigInteger(boundMessageByte);
					//int levelAMessage = DataTransfer.bytesToInt(data, 1);
					//int bound = DataTransfer.bytesToInt(data, 5);
					int[] results  = LevelCompare.callStep2(levelMessage, boundMessage);
					byte[] src = new byte[404];
					for (int i = 0; i < results.length; i++) {
						DataTransfer.intToBytes(results[i], src, 4 * i);
					}
					try {
						MainModel.getIspServer().send((Inet4Address) param.getAddress(), src,
								Constant.LEVEL_MESSAGE_LIST);
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				case Constant.LEVEL_MESSAGE_LIST:
					if (!need_compare){
						return "i am not leader";
					}
					int [] list=new int[101];
					for(int i=0;i<101;i++){
						int z=DataTransfer.bytesToInt(data, 1+4*i);
						list[i]=z;
					}
					int compareResult=LevelCompare.callStep3(list);
					byte[] src1=new byte[4];
					src1=DataTransfer.intToBytes(compareResult, src1, 0);
					try {
						MainModel.getIspServer().send((Inet4Address) param.getAddress(), src1,
								Constant.LEVEL_COMPARE_RESULT);

					} catch (IOException e) {
						e.printStackTrace();
					}
					if (compareResult == 1){
						acceptCount++;
						if (acceptCount == troopers.size()){
							broadcastLeader();
						}
					} else {
						need_compare = false;
					}
					break;
				case Constant.LEVEL_COMPARE_RESULT:
					int result=DataTransfer.bytesToInt(data, 1);
					break;
				case Constant.Broadcast_START_COMMANDER:
					try {
						startCompare();
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
                case Constant.Broadcast_IM_LEADER:
                    Inet4Address srcnet = ((Inet4Address) param.getAddress());
                    commander = MainModel.getPeerDetector().GetLocalAddress().equals(srcnet);
                    commanderIP = srcnet.getHostAddress();
                    Platform.runLater(()->{
                        ObservableList<Stage> stages = FXRobotHelper.getStages();
                        stages.get(0).setScene(SceneManager.create("equipment/equipment.fxml"));
                    });
                    break;
				case Constant.Broadcast_START_AUTHENTICATION:
				    startAuthentication();
					return "start auth";
                case Constant.AUTH_1_TO:
                    // get target public key
                    int len = DataTransfer.bytesToInt(data, 1);
                    byte[] content = new byte[len];
                    for (int i=0; i<len; i++){
                        content[i] = data[i+5];
                    }
                    try {
                        byte[] b = RSAUtils.decryptByPrivateKey(content, MainModel.user.getRsa());
                        int uid = MainModel.getPeerDetector().GetPeerIDMap().get(param.getAddress());
                        String upub = MainModel.user.getRsa_pubs().get(uid);
                        byte[] sendback =  wrapBytes(RSAUtils.encryptByPublicKey(b, upub));
                        MainModel.getIspServer().send(((Inet4Address) param.getAddress()), sendback, Constant.AUTH_2_TO);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case Constant.AUTH_2_TO:
                    int len2 = DataTransfer.bytesToInt(data, 1);
                    byte[] content2 = new byte[len2];
                    for (int i=0; i<len2; i++){
                        content2[i] = data[i+5];
                    }
                    try{
                        byte[] b = RSAUtils.decryptByPrivateKey(content2, MainModel.user.getRsa());
                        boolean pass = true;
                        for (int i=0; i<secret.length; i++){
                            if (secret[i] != b[i]){
                                pass = false;
                            }
                        }
                        if (pass){
                            // find the trooper
                            current = null;
                            String srcaddress = param.getAddress().getHostAddress();
                            for (current_i =0; current_i<troopers.size(); current_i++){
                                Trooper tmp = troopers.get(current_i);
                                if (tmp.getIp().equals(srcaddress)){
                                    current = tmp;
                                    break;
                                };
                            }
                            if (current == null){
                                return "invalid trooper";
                            }
                            System.out.println("accept "+ current.getId() + " !");
                            Platform.runLater(()->{
                                // accept
                                current.setState(Trooper.State.AC.getValue());
                                troopers.set(current_i, current);
                            });
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
				}
				return "ok";
			}
		});
		// binding the callback to ispServer
		MainModel.getIspServer().setCallbacks(callbacks);
	}

	private Trooper current = null;
	private int current_i = 0;

	private byte[] wrapBytes(byte[] content){
        int len = content.length;
        byte[] c = new byte[Integer.BYTES + content.length];
        DataTransfer.intToBytes(len, c, 0);
        for (int j=0; j<content.length; j++){
            c[j+4] = content[j];
        }
	    return c;
    }

    public void refreshList(Inet4Address[] addresses){
        int len = troopers.size();
        HashMap<Inet4Address, Integer> id_map = MainModel.getPeerDetector().GetPeerIDMap();
        for (Inet4Address address: addresses){
            boolean exist = false;
            // check if existed
            for (int i=0; i<len; i++){
                if (address.getHostAddress().equals(troopers.get(i).getIp())){
                    exist = true;
                    break;
                }
            }
            if (!exist){
                // add new address
                Trooper trooper=new Trooper(id_map.getOrDefault(address, -1)+"", address.getHostAddress());
                troopers.add(trooper);
            }
        }
    }

	/**
	 * broadcast to all people that i am leader
	 */
	private void broadcastLeader(){
    	commander =  true;
    	commanderIP = MainModel.getPeerDetector().GetLocalAddress().getHostAddress();
    	try{
    		byte[] content = new byte[4];
    		content = DataTransfer.intToBytes(MainModel.user.getId(), content, 0);
    		MainModel.getIspServer().sendBroadcast(content, Constant.Broadcast_IM_LEADER);
		} catch (Exception e){
    		e.printStackTrace();
		}
	}

	byte[] secret = new byte[2];
	private void startAuthentication(){
        Inet4Address[] addresses = MainModel.getPeerDetector().GetPeerAddresses();
        for (int i=0; i<addresses.length; i++){
            new Random().nextBytes(secret);
            int uid = MainModel.getPeerDetector().GetPeerIDMap().get(addresses[i]);
            String upub = MainModel.user.getRsa_pubs().get(uid);
            try{
                byte[] content = RSAUtils.encryptByPublicKey(secret, upub);
                MainModel.getIspServer().send(addresses[i], wrapBytes(content), Constant.AUTH_1_TO);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

	private static int maxbound = 999999999;
	private static int minbound = 10000000;
	private void startCompare() throws Exception{
		Inet4Address[] addresses = MainModel.getPeerDetector().GetPeerAddresses();//鑾峰彇鎵�鏈変汉鐨処P
		for (int i = 0; i < addresses.length; i++) {
			Random rd = new Random();
			int bound = rd.nextInt(maxbound) % (maxbound - minbound + 1) + minbound;// 鐢熸垚涓�涓緝澶х殑鏁存暟
			int stepA = rd.nextInt(100);
			BigInteger _bound=new BigInteger(bound+"");
			BigInteger _stepA=new BigInteger(stepA+"");
			BigInteger value=LevelCompare.callStep1(_bound,_stepA);
			byte[] _valuebyte=value.toByteArray();
			int lenthV=_valuebyte.length;
			byte[] _boundbyte=_bound.toByteArray();
			int lenthB=_boundbyte.length;
			byte[] src=new byte[lenthV+lenthB+2];
			src[0]=(byte) lenthV;
			src[1]=(byte) lenthB;
			for(int j=2;j<lenthV+2;j++){
				src[j]=_valuebyte[j-2];
			}
			for(int k=lenthV+2;k<src.length;k++){
				src[k]=_boundbyte[k-lenthV-2];
			}
			try {
				MainModel.getIspServer().send(addresses[i], src, Constant.LEVEL_MESSAGE_INT);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	
	public static boolean isCommander() {
		return commander;
	}

	public static String getCommanderIP() {
		return commanderIP;
	}
}
