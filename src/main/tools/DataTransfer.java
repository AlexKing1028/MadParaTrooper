package main.tools;

/**
 * Created by wesley on 2017/6/18.
 */
public class DataTransfer {
    public static int bytesToInt(byte[] b, int start){
        if (start < 0 || b == null || b.length < start+4){
            return -1;
        }
        start = start+3;
        return  b[start--] & 0xFF |
                (b[start--] & 0xFF) << 8 |
                (b[start--] & 0xFF) << 16 |
                (b[start] & 0xFF) << 24;
    }

    public static byte[] intToBytes(int a){
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static byte[] intToBytes(int a, byte[] src, int start){
        src[start++] = (byte) ((a >> 24) & 0xFF);
        src[start++] = (byte) ((a >> 16) & 0xFF);
        src[start++] = (byte) ((a >> 8) & 0xFF);
        src[start] = (byte) (a & 0xFF);
        return src;
    }

    public static byte[] longToBytes(long l, byte[] b, int start){
        if (start < 0 || b == null || b.length < start+8){
            return b;
        }
        b[start] = (byte)  (0xff & (l >> 56));
        b[start+1] = (byte)  (0xff & (l >> 48));
        b[start+2] = (byte)  (0xff & (l >> 40));
        b[start+3] = (byte)  (0xff & (l >> 32));
        b[start+4] = (byte)  (0xff & (l >> 24));
        b[start+5] = (byte)  (0xff & (l >> 16));
        b[start+6] = (byte)  (0xff & (l >> 8));
        b[start+7] = (byte)  (0xff & l);
        return b;
    }

    public static long bytesToLong(byte[] b, int start){
        return  (0xff00000000000000L    & ((long)b[start+0] << 56))  |
                (0x00ff000000000000L    & ((long)b[start+1] << 48))  |
                (0x0000ff0000000000L    & ((long)b[start+2] << 40))  |
                (0x000000ff00000000L    & ((long)b[start+3] << 32))  |
                (0x00000000ff000000L    & ((long)b[start+4] << 24))  |
                (0x0000000000ff0000L    & ((long)b[start+5] << 16))  |
                (0x000000000000ff00L    & ((long)b[start+6] << 8))   |
                (0x00000000000000ffL    &  (long)b[start+7]);
    }

    public static byte[] doubleToBytes(double d, byte[] b, int start){
        long longbits = Double.doubleToLongBits(d);
        return longToBytes(longbits, b, start);
    }

    public static double bytesToDouble(byte[] b, int start){
        return Double.longBitsToDouble(bytesToLong(b, start));
    }
}
