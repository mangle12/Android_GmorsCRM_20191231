package tw.com.masterhand.gmorscrm.tools;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class DialogBuilder {
    public static AlertDialog create(Context context, String title, String msg, String okString, DialogInterface.OnClickListener okListener, String cancelString, DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog);
        builder.setTitle(title).setMessage(msg);
        if (okString != null)
            builder.setPositiveButton(okString, okListener);
        if (cancelString != null)
            builder.setNegativeButton(cancelString, cancelListener);
        return builder.create();
    }
}
