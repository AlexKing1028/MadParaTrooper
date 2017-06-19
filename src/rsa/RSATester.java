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
            System.err.println("��Կ: \n\r" + publicKey);  
            System.err.println("˽Կ�� \n\r" + privateKey);
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
            System.err.println("��Կ���ܡ���˽Կ����");
            String source = "����һ��û���κ���������֣��㿴���˵���û����������";
            System.out.println("\r����ǰ���֣�\r\n" + source);
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
            System.out.println("���ܺ����֣�\r\n" + new String(encodedData));
            byte[] decodedData = RSAUtils.decryptByPrivateKey(encodedData, privateKey);
            String target = new String(decodedData);
            System.out.println("���ܺ�����: \r\n" + target);
        }catch (Exception e){
            e.printStackTrace();
        }
    }  
}
