package shamir;

import main.tools.DataTransfer;

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

	public byte[] toBytes(byte[] b, int start){
	    if (b == null || b.length < start+getByteLen()){
	        return b;
        }
        b = DataTransfer.doubleToBytes(x, b, start);
	    b = DataTransfer.doubleToBytes(y, b, start+Double.SIZE/8);
	    return b;
    }

    public static Key fromBytes(byte[] b, int start){
        if (b == null || b.length < start+getByteLen()){
            return null;
        }
	    double x = DataTransfer.bytesToDouble(b, start);
        double y = DataTransfer.bytesToDouble(b, start+Double.SIZE/8);
        return new Key(x, y);
    }

    public static int getByteLen(){
	    return 16;
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Key key = (Key) o;

		if (Double.compare(key.x, x) != 0) return false;
		return Double.compare(key.y, y) == 0;
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/*
	public static void main(String[] args){
        Key k = new Key(123.0, 14.56);
        byte[] b = new byte[Key.getByteLen()];
        b = k.toBytes(b, 0);
        Key k1 = Key.fromBytes(b, 0);
        System.out.println(k1.x+" "+k1.y);
    }
    */
}
