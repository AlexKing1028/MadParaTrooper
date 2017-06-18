package millionaire;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.SocketException;
import java.util.Random;

import main.MainModel;
import main.tools.Constant;
import main.tools.DataTransfer;
import network.ISPServer;

/**
 * 
 * @author Jarek-Lab Compare Level A compare to others
 *
 */
public class LevelCompare {
	private static int maxbound = 999999999;
	private static int minbound = 10000000;
	private static int x;
	private static boolean isLeader=true;
	

	public static void main(String []args) throws IOException{
		try {
			ISPServer server = MainModel.getIspServer();//获取全局的server
			Inet4Address[] addresses = MainModel.getPeerDetector().GetPeerAddresses();//获取所有人的IP
			for (int i = 0; i < addresses.length; i++){
				Random rd = new Random();
				int bound = rd.nextInt(maxbound) % (maxbound - minbound + 1) + minbound;// 生成一个较大的整数
				int stepA = rd.nextInt(100);
				int value=callStep1(bound,stepA);
				byte[] src=DataTransfer.intToBytes(value);
				DataTransfer.intToBytes(bound, src, 4);  
				server.send(addresses[i], src, Constant.LEVEL_MESSAGE_INT);
				//有一个时间
				if(!isLeader){
					System.out.println("you are not the leader");
					continue;
				}
				
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public static int callStep1(int bound, int temp) {
		int _Arandom = bound + temp;
		x = _Arandom;
		int MessageAtoB = Millionnaire.step1(MainModel.user.getLevel(), MainModel.user.getDescription(), _Arandom);
		return MessageAtoB;
		// if(Millionnaire.step3(_Arandom, user1.getLevel(), results)){
		// //A的军衔小于或等于B
		// compareResults[i]=0;
		// }else{
		// //A的军衔大于B
		// compareResults[i]=1;
		// }
		// }
		// 如果有一个是0，说明A的军衔不是最大的，如果全是1说明A的军衔是最大的
		// for(int i=0;i<compareResults.length;i++){
		// if(compareResults[i]==0){
		// return false;
		// }
		// }
		// return true;
	}

	public static int[] callStep2(int messageFromOther,int bound) {
		Random rd=new Random();
		//int temp2=rd.nextInt(100);
		//int _Brandom=bound-temp2;
		int[] results = Millionnaire.step2(messageFromOther, MainModel.user.getLevel(),
				MainModel.user.getDescription(),bound);
		return results;

	}
	
	public static int callStep3(int []result){
		if(Millionnaire.step3(x, MainModel.user.getLevel(), result)==1){
			isLeader=true;
			return 1;
		}else{
			isLeader=false;
			return 0;
		}
	}


}
