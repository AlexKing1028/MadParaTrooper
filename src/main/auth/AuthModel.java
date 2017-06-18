package main.auth;

import javafx.collections.ObservableList;
import javafx.util.Callback;
import main.MainModel;
import main.model.Trooper;
import main.tools.Constant;
import main.tools.DataTransfer;
import millionaire.LevelCompare;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wesley shi on 2017/6/17.
 */
public class AuthModel {
	private static boolean commander;
	private static String commanderIP;

	protected ObservableList<Trooper> troopers;

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
					int[] results = LevelCompare.callStep2(levelAMessage, bound);
					byte[] src = new byte[0];
					for (int i = 0; i < results.length; i++) {
						DataTransfer.intToBytes(results[i], src, 4 * i);
					}
					try {
						MainModel.getIspServer().send((Inet4Address) param.getAddress(), src,
								Constant.LEVEL_MESSAGE_LIST);
					} catch (IOException e) {
						e.printStackTrace();
					}
				case Constant.LEVEL_MESSAGE_LIST:
					int [] list=new int[101];
					for(int i=0;i<101;i++){
						int z=DataTransfer.bytesToInt(data, 1+4*i);
						list[i]=z;
					}
					int compareResult=LevelCompare.callStep3(list);
					byte[] src1=DataTransfer.intToBytes(compareResult);
					try {
						MainModel.getIspServer().send((Inet4Address) param.getAddress(), src1,
								Constant.LEVEL_MESSAGE_LIST);
					} catch (IOException e) {
						e.printStackTrace();
					}
				case Constant.LEVEL_COMPARE_RESULT:
					int result=DataTransfer.bytesToInt(data, 1);
				}

				return "ok";
			}
		});
		// binding the callback to ispServer
		MainModel.getIspServer().setCallbacks(callbacks);
	}

	public void refreshList(Inet4Address[] addresses) {
		int len = troopers.size();
		for (Inet4Address address : addresses) {
			boolean exist = false;
			// check if existed
			for (int i = 0; i < len; i++) {
				if (address.getHostAddress().equals(troopers.get(i).getIp())) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				// add new address
				Trooper trooper = new Trooper("", address.getHostAddress());
				troopers.add(trooper);
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
