package main.auth;

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
					int levelAMessage = DataTransfer.bytesToInt(data, 1);
					int bound = DataTransfer.bytesToInt(data, 5);
					int[] results  = LevelCompare.callStep2(levelAMessage, bound);
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
						if (acceptCount == troopers.size()-1){
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
					startCompare();
					break;
                case Constant.Broadcast_IM_LEADER:
                    Inet4Address srcnet = ((Inet4Address) param.getAddress());
                    if (!MainModel.getPeerDetector().GetLocalAddress().equals(srcnet)){
                        commander = false;
                        commanderIP = srcnet.getHostAddress();
                        Platform.runLater(()->{
                            ObservableList<Stage> stages = FXRobotHelper.getStages();
                            stages.get(0).setScene(SceneManager.create("equipment/equipment.fxml"));
                        });
                    }
                    break;
				case Constant.Broadcast_START_AUTHENTICATION:
				    startAuthentication();
					return "start auth";
				}
				return "ok";
			}
		});
		// binding the callback to ispServer
		MainModel.getIspServer().setCallbacks(callbacks);
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

	private void startAuthentication(){

    }

	private static int maxbound = 999999999;
	private static int minbound = 10000000;
	private void startCompare(){
		Inet4Address[] addresses = MainModel.getPeerDetector().GetPeerAddresses();//获取所有人的IP
		for (int i = 0; i < addresses.length; i++) {
			Random rd = new Random();
			int bound = rd.nextInt(maxbound) % (maxbound - minbound + 1) + minbound;// 生成一个较大的整数
			int stepA = rd.nextInt(100);
			int value = LevelCompare.callStep1(bound, stepA);
			byte[] src=new byte[8];
			DataTransfer.intToBytes(value,src,0);
			DataTransfer.intToBytes(bound, src, 4);
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
