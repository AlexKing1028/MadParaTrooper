package millionaire;

import rsa.RSA;

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
	 */
	public static int step1(int rank, String publicKey, int randomnum) {
		// 将A生成的大随机数用B的公钥加密
		int c = RSA.encryptByPublicKey(randomnum, publicKey);// 用B的公钥加密
		return c - rank;
	}

	/**
	 * 
	 * @param mA
	 *            A传给B的信息，可能是大整数，暂时定为int
	 * @param rank
	 *            军衔
	 * @param privateKey
	 *            私钥
	 * @param bound
	 *            A生成的大整数范围
	 * @return
	 */
	public static int[] step2(int mA, int rank, String privateKey, int _Bbound) {
		// BigInteger a=bound.nextProbablePrime();//这地方还有问题，应该是比bound稍小一点的大随机素数
		while (!(Prime.isPrime(_Bbound))) {
			_Bbound--;
		}
		int p = _Bbound;// B生成的大随机素数
		int[] y = new int[100];// 使用秘钥解密后的100个数
		int[] z = new int[100];
		int[] result = new int[101];
		// 当1<=u<=100，计算mA+u通过秘钥解密后的值
		for (int u = 1; u <= 100; u++) {
			y[u - 1] = RSA.decryptByPrivateKey(mA + u, privateKey);
		}
		boolean pnum = false;// 素数P是否符合要求
		while (!pnum) {// if pnum=false ,choose a p
			// p=Prime.getPrime();//choose a big random number and smaller than
			// x
			for (int u = 1; u <= 100; u++) {
				z[u - 1] = y[u - 1] / p;// y mod p
			}
			// 验证z
			for (int u = 1; u <= 100; u++) {// validate z
				if (z[u - 1] < 0 || z[u - 1] >= p - 1) {// validate for every u
														// 0<z<p-1
					pnum = false;
					// 产生新的素数
					while (!(Prime.isPrime(p--))) {
						p--;
					}
				}
				for (int v = 1; v <= 100; v++) {// validata for each u!=v
												// |z[u-1]-z[v-1]|>=2
					if (u != v) {
						if (Math.abs(z[u - 1] - z[v - 1]) < 2) {
							pnum = false;
							// 产生新的素数
							while (!(Prime.isPrime(p--))) {
								p--;
							}
						}
					}
				}
			}
			pnum = true;
		}
		// 生成结果序列
		for (int u = 1; u < 101; u++) {
			if (u <= rank) {
				result[u - 1] = z[u - 1];
			}
			result[u - 1] = z[u - 1] + 1;
		}
		result[100] = p;
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
