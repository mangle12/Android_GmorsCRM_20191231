package tw.com.masterhand.gmorscrm.activity.setting;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayout;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import butterknife.BindView;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;
import tw.com.masterhand.gmorscrm.view.Appbar;

public class ThemColorActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.container)
    GridLayout container;

    // THEME COLOR
    private final int[] THEME_COLOR = {R.color.colorPrimary, R.color.blue, R.color.red, R.color.orange, R.color.green};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_color);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
    }

    private void init() {
        appbar.setTitle(getString(R.string.setting_color));

        Point size = new Point();
        ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(size);
        int screenWidth = size.x;
        int itemHeight = UnitChanger.dpToPx(48);
        int itemWidth = (screenWidth - UnitChanger.dpToPx(32)) / container.getColumnCount();
        for (int i = 0; i < THEME_COLOR.length; i++) {
            GridLayout.LayoutParams gridParams = new GridLayout.LayoutParams();
            gridParams.height = itemHeight;
            gridParams.width = itemWidth;
            gridParams.setGravity(Gravity.CENTER);
            gridParams.columnSpec = GridLayout.spec(i % container.getColumnCount());
            gridParams.rowSpec = GridLayout.spec(i / container.getColumnCount());
            container.addView(generateItem(ContextCompat.getColor(this, THEME_COLOR[i])), gridParams);
        }
    }

    private View generateItem(final int color) {
        ImageView ivColor = new ImageView(this);
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(ContextCompat.getColor(this, color));
        ivColor.setImageDrawable(colorDrawable);
        ivColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceHelper.saveThemeColor(color);
                appbar.setBackgroundColor(ContextCompat.getColor(v.getContext(), color));
            }
        });
        return ivColor;
    }
}
