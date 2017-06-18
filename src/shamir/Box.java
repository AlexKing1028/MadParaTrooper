package shamir;

import java.io.Serializable;
import java.util.Random;

public class Box implements Serializable{
	private static final long serialVersionUID = 9222843820698308538l;
	private double lock;
	private int least_people;
	
	public Box(int people) {
		least_people = people;
		Random random = new Random();
		lock = random.nextInt(9999) + 1;
	}
	
	public Key[] Lock(int total_people) {
		double[] coefficient = new double[least_people];
		coefficient[0] = lock;
		Random random = new Random();
		for (int i = 1; i < least_people; i++) {
			coefficient[i] = random.nextInt(9999) + 1;
		}
		
		Key[] keys = new Key[total_people];
		for (int i = 0; i < total_people; i++) {
			double x = i + 1;
			double y = 0;
			int j = least_people - 1;
			while (j >= 0) {
				y = x * y + coefficient[j];
				j--;
			}
			keys[i] = new Key(x, y);
		}
		
		return keys;
	}
	
	public int unLock(int people, Key[] keys) {
		if (people < least_people)
			return -1;
		double[][] coef = new double[least_people][least_people + 1];
		for (int i = 0; i < least_people; i++) {
			coef[i][0] = keys[i].y;
			coef[i][1] = 1;
			for (int j = 2; j <= least_people; j++)
				coef[i][j] = coef[i][j - 1] * keys[i].x;
		}
		int count = 1;
		while (count < least_people) {
			for (int i = 0; i <= least_people - count; i++) {
				for (int j = 0; j <= least_people - count + 1; j++)
					coef[i][j] /= coef[i][least_people - count + 1];
			}
			for (int i = 0; i <= least_people - count; i++) {
				for (int j = 0; j <= least_people - count + 1; j++)
					coef[i][j] -= coef[least_people - count][j];
			}
			count++;
		}
		double a = coef[0][0] / coef[0][1];
		if (lock - a > -1 && lock - a < 1)
			return 1;
		else
			return -2;
	}
}
