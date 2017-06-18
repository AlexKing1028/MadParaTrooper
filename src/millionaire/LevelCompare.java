package millionaire;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;

import main.MainModel;
import main.model.User;
import network.ISPServer;

/**
 * 
 * @author Jarek-Lab 
 * Compare Level A compare to others
 *
 */
public class LevelCompare {
	private static int soldierNum = 3;
	public int[] compareResults = new int[soldierNum-1];
	
	public static void main(String []args) throws IOException{
		try {
			ISPServer server=MainModel.getIspServer();//获取全局的server
			Inet4Address[] addresses = MainModel.getPeerDetector().GetPeerAddresses();//获取所有人的IP
			for (int i = 0; i < addresses.length; i++){
				int value=callStep1();
				byte[] src = new byte[4];  
			    src[3] =  (byte) ((value>>24) & 0xFF);  
			    src[2] =  (byte) ((value>>16) & 0xFF);  
			    src[1] =  (byte) ((value>>8) & 0xFF);    
			    src[0] =  (byte) (value & 0xFF);   
				server.send(addresses[i], src, ISPServer.sourceElect);
				
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	

	public static int callStep1() {
		User user1 = new User(1, 1, "", "");// 应该通过IP获取到user对象
		//for (int i = 0; i < soldierNum; i++) {
			//生成一个随机大的整数和一个随机值，相加得到一个大随机数
			int maxbound=999999999,minbound=10000000;
			Random rd=new Random();
			int bound=rd.nextInt(maxbound)%(maxbound-minbound+1)+minbound;//生成一个较大的整数
			//BigInteger _bound = new BigInteger(9, rd);//用于控制A、B生成的大随机数相差不大，且A的随机数略大于B
			int temp=rd.nextInt(100);
			//BigInteger _temp=new BigInteger(temp+"");
			//BigInteger _Arandom=_bound.add(_temp);
			int _Arandom=bound+temp;
			User user2 = new User(2, 2, "", "");// 应该通过IP获取到user对象
			int MessageAtoB = Millionnaire.step1(user1.getLevel(), user2.getDescription(), _Arandom);
			return MessageAtoB;
			//需要传输
			//trans MessageAtoB to B
			//get return list
//			tranMessageToAnther(MessageAtoB);
//			int temp2=rd.nextInt(100);
//			int _Brandom=bound-temp2;
//			int[] results=getListFrom();
//			//int[] results = Millionnaire.step2(MessageAtoB, user2.getLevel(), user2.getDescription(),_Brandom);
//			if(Millionnaire.step3(_Arandom, user1.getLevel(), results)){
//				//A的军衔小于或等于B
//				compareResults[i]=0;
//			}else{
//				//A的军衔大于B
//				compareResults[i]=1;
//			}
//		}
		//如果有一个是0，说明A的军衔不是最大的，如果全是1说明A的军衔是最大的
//		for(int i=0;i<compareResults.length;i++){
//			if(compareResults[i]==0){
//				return false;
//			}
//		}
//		return true;
	}
	
	public static int[] callStep2(){
		return null;
		
	}

	private int[] getListFrom() {
		// TODO Auto-generated method stub
		return null;
	}

	private void tranMessageToAnther(int messageAtoB) {
		// TODO Auto-generated method stub
		
	}
	

}
