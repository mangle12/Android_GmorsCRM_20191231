package tw.com.masterhand.gmorscrm.activity.statistic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import tw.com.masterhand.gmorscrm.BaseWebCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.enums.StatisticMenu;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;
import tw.com.masterhand.gmorscrm.view.Appbar;

public class StatisticActivity extends BaseWebCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.container)
    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        appbar.invalidate();
    }

    @Override
    protected void onUserChecked() {

    }

    private void init() {
        appbar.setTitle(getString(R.string.title_activity_statistic));

        for (StatisticMenu menu : StatisticMenu.values()) {
            container.addView(generateItem(menu));
        }
    }

    /**
         * 產生選單項目
         */
    private View generateItem(final StatisticMenu menu) {
        int padding = UnitChanger.dpToPx(8);
        TextView tvItem = new TextView(this);
        tvItem.setPaddingRelative(padding, 0, 0, 0);
        tvItem.setCompoundDrawablePadding(padding);
        tvItem.setText(getString(menu.getTitle()));
        tvItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tvItem.setCompoundDrawablesRelativeWithIntrinsicBounds(menu.getImage(), 0, 0, 0);
        tvItem.setGravity(Gravity.CENTER_VERTICAL);//垂直置中
        tvItem.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_list_selector));
        tvItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                switch (menu) {
                    case TARGET://個人目標完成率
                        intent = new Intent(StatisticActivity.this, StatisticTargetActivity.class);
                        break;
                    case VISIT://業務外出活動排行
                        intent = new Intent(StatisticActivity.this, StatisticVisitActivity.class);
                        break;
                    case PERFORMANCE://業務員銷售業績
                        intent = new Intent(StatisticActivity.this, StatisticPerformanceActivity.class);
                        break;
                    case SIGN://預估簽約商機排行
                        intent = new Intent(StatisticActivity.this, StatisticSignActivity.class);
                        break;
                    case WIN://銷售機會贏單
                        intent = new Intent(StatisticActivity.this, StatisticWinActivity.class);
                        break;
                    case SALE://銷售機會
                        intent = new Intent(StatisticActivity.this, StatisticSaleActivity.class);
                        break;
                }
                if (intent != null) {
                    intent.putExtra(MyApplication.INTENT_KEY_STATISTIC_MENU, menu.getValue());
                    startActivity(intent);
                }
            }
        });
        return tvItem;
    }
}
