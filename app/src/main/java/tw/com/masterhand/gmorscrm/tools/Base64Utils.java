package tw.com.masterhand.gmorscrm.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public class Base64Utils {
    private final static int FLAG = Base64.DEFAULT;

    public static byte[] encode(String data) throws UnsupportedEncodingException {
        return Base64.encode(data.getBytes("UTF-8"), FLAG);
    }

    public static String encodeToString(byte[] data) {
        return Base64.encodeToString(data, FLAG);
    }

    public static byte[] decode(byte[] data) {
        return Base64.decode(data, FLAG);
    }

    public static byte[] decodeFromString(String data) {
        return Base64.decode(data, FLAG);
    }

    /**
     * 將Bitmap轉為base64 String
     *
     * @param image :bitmap
     */
    public static String encodeBitmapToString(Bitmap image) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOS);
        String result = Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
        result = result.replaceAll("\n", "");
        return result;
    }

    /**
     * 將Base64 String轉為Bitmap
     *
     * @param input :String
     * @return Bitmap
     */
    public static Bitmap decodeToBitmapFromString(String input) {
        if (input == null)
            return null;
        byte[] decodedBytes = Base64.decode(input, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0,
                decodedBytes.length);
    }
}
