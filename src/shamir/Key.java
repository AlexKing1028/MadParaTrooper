package shamir;

import java.io.Serializable;

public class Key implements Serializable{
	public double x;
	public double y;
	
	public Key(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Key(String keystr) {
		String[] key = keystr.split(" ");
		x = Double.parseDouble(key[0]);
		y = Double.parseDouble(key[1]);
	}
	
	public String toString() {
		String keystr = "" + x + " " + y;
		return keystr;
	}
}
