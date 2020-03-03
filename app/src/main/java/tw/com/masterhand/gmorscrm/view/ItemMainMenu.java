package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.activity.personal.PersonalActivity;
import tw.com.masterhand.gmorscrm.activity.approval.ApprovalMenuActivity;
import tw.com.masterhand.gmorscrm.activity.customer.ContactPersonCreateActivity;
import tw.com.masterhand.gmorscrm.activity.customer.CustomerActivity;
import tw.com.masterhand.gmorscrm.activity.customer.CustomerCreateActivity;
import tw.com.masterhand.gmorscrm.activity.reimbursement.ReimbursementRecordActivity;
import tw.com.masterhand.gmorscrm.activity.report.ReportActivity;
import tw.com.masterhand.gmorscrm.activity.sale.SaleActivity;
import tw.com.masterhand.gmorscrm.activity.sample.SampleListActivity;
import tw.com.masterhand.gmorscrm.activity.setting.SettingActivity;
import tw.com.masterhand.gmorscrm.activity.statistic.StatisticActivity;
import tw.com.masterhand.gmorscrm.activity.task.CreateActivity;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.activity.task.ReimbursementCreateActivity;
import tw.com.masterhand.gmorscrm.activity.task.TaskCreateActivity;
import tw.com.masterhand.gmorscrm.enums.MainMenu;

public class ItemMainMenu extends RelativeLayout {
    @BindView(R.id.imageView_icon)
    ImageView ivIcon;
    @BindView(R.id.textView_title)
    TextView tvTitle;

    Context mContext;
    Callback callback;

    public interface Callback {
        void onMenuSelected(MainMenu menu);
    }

    public ItemMainMenu(Context context) {
        super(context);
        init(context);
    }

    public ItemMainMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemMainMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_main_menu, this);
        ButterKnife.bind(this, view);
    }

    /**
     * 設定選擇監聽器
     */
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /**
     * 指定MainMenu
     */
    public void setMainMenu(final MainMenu menu) {
        ivIcon.setImageResource(menu.getImage());
        tvTitle.setText(mContext.getString(menu.getTitle()));
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null)
                    callback.onMenuSelected(menu);
            }
        });
    }
}
