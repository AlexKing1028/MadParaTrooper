package rsa;

import java.io.*;
import java.util.Map;

public class RSATester {

	static String publicKey="";
    static String privateKey="";

    static {
        try {
            Map<String, Object> keyMap = RSAUtils.genKeyPair();  
            publicKey = RSAUtils.getPublicKey(keyMap);  
            privateKey = RSAUtils.getPrivateKey(keyMap);  
            System.err.println("公钥: \n\r" + publicKey);  
            System.err.println("私钥： \n\r" + privateKey);
            BufferedWriter bwpub = new BufferedWriter(new FileWriter("rsa1.pub"));
            bwpub.write(publicKey);
            bwpub.close();
            BufferedWriter bw = new BufferedWriter(new FileWriter("rsa1"));
            bw.write(privateKey);
            bw.close();
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }

    public static void main(String[] args) throws Exception {  
        test();    
    }  
  
    static void test() throws Exception {
        try {
            System.err.println("公钥加密——私钥解密");
            String source = "这是一行没有任何意义的文字，你看完了等于没看，不是吗？";
            System.out.println("\r加密前文字：\r\n" + source);
            byte[] data = source.getBytes();
            String line = null;
            BufferedReader  brpub = new BufferedReader(new FileReader("rsa.pub"));
            while ((line = brpub.readLine())!= null){
                publicKey += line+'\n';
            }
            System.out.println(publicKey);
            brpub.close();

            BufferedReader  br = new BufferedReader(new FileReader("rsa"));
            while ((line = br.readLine())!= null){
                privateKey += line +'\n';
            }
            System.out.println(privateKey);
            br.close();

            byte[] encodedData = RSAUtils.encryptByPublicKey(data, publicKey);
            System.out.println("加密后文字：\r\n" + new String(encodedData));
            byte[] decodedData = RSAUtils.decryptByPrivateKey(encodedData, privateKey);
            String target = new String(decodedData);
            System.out.println("解密后文字: \r\n" + target);
        }catch (Exception e){
            e.printStackTrace();
        }
    }  
}
