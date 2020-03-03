package tw.com.masterhand.gmorscrm.tools;

import android.content.res.Resources;

public class UnitChanger {

    /**
     * dp轉px
     *
     * @return px
     */
    public static int dpToPx(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

}
