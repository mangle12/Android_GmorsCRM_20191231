package tw.com.masterhand.gmorscrm.tools;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AESUtils {

    private static String AES = "AES/CBC/PKCS5Padding";

    public static String encrypt(byte[] data, byte[] key, byte[] ivs) {
        try {
            Cipher cipher = Cipher.getInstance(AES);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            byte[] finalIvs = new byte[16];
            int len = ivs.length > 16 ? 16 : ivs.length;
            System.arraycopy(ivs, 0, finalIvs, 0, len);
            IvParameterSpec ivps = new IvParameterSpec(finalIvs);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivps);
            return Base64Utils.encodeToString(cipher.doFinal(data));
        } catch (Exception e) {
            Logger.e("AESUtils", "Exception:" + e.getMessage());
        }
        return null;
    }

    public static byte[] decrypt(String input, byte[] key, byte[] ivs) {
        try {
            byte[] data = Base64Utils.decodeFromString(input);
            Cipher cipher = Cipher.getInstance(AES);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            byte[] finalIvs = new byte[16];
            int len = ivs.length > 16 ? 16 : ivs.length;
            System.arraycopy(ivs, 0, finalIvs, 0, len);
            IvParameterSpec ivps = new IvParameterSpec(finalIvs);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivps);
            return cipher.doFinal(data);
        } catch (Exception e) {
            Logger.e("AESUtils", "Exception:" + e.getMessage());
        }
        return null;
    }
}
