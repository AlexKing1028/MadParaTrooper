package millionaire;

import rsa.RsaTools;

import java.math.BigInteger;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import main.tools.DataTransfer;

/**
 * Created by rui on 2017/6/17.
 */
public class Millionnaire {
	/**
	 * 
	 * @param rank
	 *            A的军衔
	 * @param publicKey
	 *            B的公钥
	 * @param randomnum
	 *            A生成的大随机数 用加密后的大随机数减去A的军衔
	 * @return 返回结果
	 * @throws Exception 
	 */
	public static BigInteger step1(int rank, BigInteger randomnum) throws Exception{
		// 将A生成的大随机数用B的公钥加密
		BigInteger rankBig=new BigInteger(rank+"");
		//BigInteger encryptC=RSA.encryptByPublicKey(randomnum);// 用B的公钥加密
		String encrypNum=RsaTools.encryptBASE64(randomnum.toByteArray());
		BigInteger encryptC=new BigInteger(Base64.decode(encrypNum));
		return encryptC.subtract(rankBig);
	}
	
	public static void main(String[]args){
		try {
			//step1(1, 2222);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param mA
	 *            A传给B的信息，可能是大整数
	 * @param rank
	 *            军衔
	 * @param privateKey
	 *            私钥
	 * @param bound
	 *            A生成的大整数范围
	 * @return
	 */
	public static int[] step2(BigInteger mA, int rank, BigInteger _Bbound) {
		//BigInteger bi = new BigInteger(_Bbound + "");
		while (!_Bbound.isProbablePrime(20)) {
			_Bbound = _Bbound.subtract(BigInteger.ONE);
		}
		//_Bbound = _Bbound.intValue();
		BigInteger p = _Bbound;// B生成的大随机素数
		int[] y = new int[100];// 使用秘钥解密后的100个数
		int[] z = new int[100];
		int[] result = new int[101];
		// 当1<=u<=100，计算mA+u通过秘钥解密后的值
		for (int u = 1; u <= 100; u++) {
			BigInteger _uBigInteger=new BigInteger(u+"");
			byte[] decryptResult;
			try {
				decryptResult = RsaTools.decryptBASE64(mA.add(_uBigInteger).toString());
				y[u-1]=DataTransfer.bytesToInt(decryptResult, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		boolean pnum = false;// 素数P是否符合要求
//		while (!pnum) {// if pnum=false ,choose a p
//			// p=Prime.getPrime();//choose a big random number and smaller than
//			// x
//			for (int u = 1; u <= 100; u++) {
//				z[u - 1] = y[u - 1] / p;// y mod p
//			}
//			// 验证z
//			for (int u = 1; u <= 100; u++) {// validate z
//				if (z[u - 1] < 0 || z[u - 1] >= p - 1) {// validate for every u
//														// 0<z<p-1
//					pnum = false;
//					// 产生新的素数
//					while (!p.isProbablePrime(20)) {
//						p = p.subtract(BigInteger.ONE);
//					}
//				}
//				for (int v = 1; v <= 100; v++) {// validata for each u!=v
//												// |z[u-1]-z[v-1]|>=2
//					if (u != v) {
//						if (Math.abs(z[u - 1] - z[v - 1]) < 2) {
//							pnum = false;
//							// 产生新的素数
//							while (!p.isProbablePrime(20)) {
//								p = p.subtract(BigInteger.ONE);
//							}
//						}
//					}
//				}
//			}
//			pnum = true;
//		}
		// 生成结果序列
		for (int u = 1; u < 101; u++) {
			if (u <= rank) {
				result[u - 1] = z[u - 1];
			}
			result[u - 1] = z[u - 1] + 1;
		}
		result[100] = p.intValue();
		return result;

	}

	/**
	 * 如果返回值为true，说明A的军衔小于或等于B，如果返回值为false，说明A的军衔大于B
	 * 
	 * @param x
	 * @param rank
	 * @param z
	 * @return
	 */
	public static int step3(int x, int rank, int[] z) {
		if ((z[rank - 1] - x) / z[100] == 0) {
			// RankA<=RankB
			return 0;
		} else {
			// RankA>RankB
			return 1;
		}
	}

}
