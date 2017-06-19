package rsa;

import java.util.Map;

public class RSATester {

	static String publicKey;  
    static String privateKey;  
  
    static {  
        try {  
            Map<String, Object> keyMap = RSAUtils.genKeyPair();  
            publicKey = RSAUtils.getPublicKey(keyMap);  
            privateKey = RSAUtils.getPrivateKey(keyMap);  
            System.err.println("��Կ: \n\r" + publicKey);  
            System.err.println("˽Կ�� \n\r" + privateKey);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
      
    public static void main(String[] args) throws Exception {  
        test();    
    }  
  
    static void test() throws Exception {  
        System.err.println("��Կ���ܡ���˽Կ����");  
        String source = "����һ��û���κ���������֣��㿴���˵���û����������";  
        System.out.println("\r����ǰ���֣�\r\n" + source);  
        byte[] data = source.getBytes();  
        byte[] encodedData = RSAUtils.encryptByPublicKey(data, publicKey);  
        System.out.println("���ܺ����֣�\r\n" + new String(encodedData));  
        byte[] decodedData = RSAUtils.decryptByPrivateKey(encodedData, privateKey);  
        String target = new String(decodedData);  
        System.out.println("���ܺ�����: \r\n" + target);  
    }  
}
