package tw.com.masterhand.gmorscrm.tools;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import tw.com.masterhand.gmorscrm.R;

public class ImageTools {
    public static Drawable getCircleDrawable(Resources res, Bitmap src) {
        RoundedBitmapDrawable dr =
                RoundedBitmapDrawableFactory.create(res, getCroppedBitmap(src));
        dr.setCornerRadius(Math.max(src.getWidth(), src.getHeight()) / 2.0f);
        dr.setAntiAlias(true);
        return dr;
    }

    public static Drawable getCornerDrawable(Resources res, Bitmap src) {
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory
                .create(res, src);
        roundedBitmapDrawable.setCornerRadius(res.getDimensionPixelSize(R.dimen.corner));
        roundedBitmapDrawable.setAntiAlias(true);
        return roundedBitmapDrawable;
    }

    public static BitmapDrawable getColorDrawable(Context context, int resId, int colorId) {
        BitmapDrawable output = new BitmapDrawable(context.getResources(), BitmapFactory
                .decodeResource
                        (context.getResources(), resId));
        int color = ContextCompat.getColor(context, colorId);
        output.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        return output;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        matrix.setRotate(orientation);
        Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return rotateBitmap;
    }

    public static Bitmap getCroppedBitmap(Bitmap srcBmp) {
        Bitmap dstBmp;
        if (srcBmp.getWidth() >= srcBmp.getHeight()) {

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth() / 2 - srcBmp.getHeight() / 2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );

        } else {

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight() / 2 - srcBmp.getWidth() / 2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }
        return Bitmap.createScaledBitmap(dstBmp, 100, 100, false);
    }
}
