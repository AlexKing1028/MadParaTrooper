package millionaire;

import java.io.IOException;
import java.math.BigInteger;
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
	

	public static void main(String []args) throws Exception{
		try {
			//ISPServer server = MainModel.getIspServer();//获取全局的server
			//Inet4Address[] addresses = MainModel.getPeerDetector().GetPeerAddresses();//获取所有人的IP
			//for (int i = 0; i < addresses.length; i++){
				
				Random rd = new Random();
				//BigInteger bound=new BigInteger(9,rd);
				int bound = rd.nextInt(maxbound) % (maxbound - minbound + 1) + minbound;// 生成一个较大的整数
				int stepA = rd.nextInt(100);
				BigInteger _bound=new BigInteger(bound+"");
				BigInteger _stepA=new BigInteger(stepA+"");
				//BigInteger value=bound.add(_stepA);
				BigInteger value=callStep1(_bound,_stepA);
				System.out.println(value);
				byte[] src=value.toByteArray();
				int lenth=src.length;
				////////////
				//DataTransfer.intToBytes(bound, src, lenth); 
				////////////
				//server.send(addresses[i], src, Constant.LEVEL_MESSAGE_INT);
				
				byte[] _valuebyte=value.toByteArray();
				int lenthV=_valuebyte.length;
				byte[] _boundbyte=_bound.toByteArray();
				int lenthB=_boundbyte.length;
				byte[] src1=new byte[lenthV+lenthB+2];
				src1[0]=(byte) lenthV;
				src1[1]=(byte) lenthB;
				for(int j=2;j<lenthV+2;j++){
					src1[j]=_valuebyte[j-2];
				}
				for(int k=lenthV+2;k<src1.length;k++){
					src1[k]=_boundbyte[k-lenthV-2];
				}
				test(src1);
				
			//}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public static void test(byte[] data){
		int levellength=data[0];
		int boundlength=data[1];
		int length=levellength+boundlength+2;
		byte[] levelMessageByte=new byte[levellength];
		byte[] boundMessageByte=new byte[boundlength];
		for(int i=2;i<levellength+2;i++){
			levelMessageByte[i-2]=data[i];
		}
		for(int i=levellength+2;i<length;i++){
			boundMessageByte[i-levellength-2]=data[i];
		}
		BigInteger levelMessage=new BigInteger(levelMessageByte);
		BigInteger boundMessage=new BigInteger(boundMessageByte);
		//int levelAMessage = DataTransfer.bytesToInt(data, 1);
		//int bound = DataTransfer.bytesToInt(data, 5);
		int[] results  = LevelCompare.callStep2(levelMessage, boundMessage);
		for(int j=0;j<results.length;j++){
			System.out.print(results[j]+" ");
		}
	}

	public static BigInteger callStep1(BigInteger bound,BigInteger temp) throws Exception {
		BigInteger _Arandom=bound.add(temp);
		//int _Arandom = bound + temp;
		//x = _Arandom;
		
		//BigInteger MessageAtoB = Millionnaire.step1(MainModel.user.getLevel(), _Arandom);
		BigInteger MessageAtoB = Millionnaire.step1(21, _Arandom);
		return MessageAtoB;
	}

	public static int[] callStep2(BigInteger messageFromOther,BigInteger bound) {
		Random rd=new Random();
		//int temp2=rd.nextInt(100);
		//int _Brandom=bound-temp2;
		//int[] results = Millionnaire.step2(messageFromOther, MainModel.user.getLevel(),bound);
		int[] results = Millionnaire.step2(messageFromOther, 8,bound);
		return results;

	}
	
	public static int callStep3(int []result){
		if(Millionnaire.step3(x, MainModel.user.getLevel(), result)==1){
			return 1;
		}else{
			return 0;
		}
	}


}
