package tw.com.masterhand.gmorscrm.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.enums.ReimbursementType;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.setting.Department;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.NumberFormater;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.model.ReimbursementTarget;

public class ItemReimbursementTarget extends LinearLayout {
    @BindView(R.id.tvName)
    TextView tvName;// 對象名稱
    @BindView(R.id.textView_available)
    TextView tvAvailable;// 可報銷定額
    @BindView(R.id.textView_remain)
    TextView tvRemain;// 剩餘可報銷定額

    Activity context;
    ReimbursementTarget target;

    public ItemReimbursementTarget(Context context) {
        super(context);
        init(context);
    }

    public ItemReimbursementTarget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemReimbursementTarget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_reimbursement_target, this);
        ButterKnife.bind(this, view);
        if (isInEditMode())
            return;
        context = (Activity) mContext;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Gson gson = new GsonBuilder().setDateFormat(TimeFormater.DATABASE_DATE_TIME_STRING).create();
                intent.putExtra(MyApplication.INTENT_KEY_TARGET, gson.toJson(target));
                context.setResult(Activity.RESULT_OK, intent);
                context.finish();
            }
        });
    }

    public void setTarget(User user) {
        target = new ReimbursementTarget();
        target.setType(ReimbursementType.SELF);
        target.setTargetId(user.getId());
        target.setName(context.getString(R.string.reimbursement_type_self));
        target.setAvailable(user.getExpense_quota());
        target.setRemain(user.getExpense_left());
        tvName.setText(target.getName());

        // 可報銷定額
        tvAvailable.setText(context.getString(R.string.exp_available) + NumberFormater.getMoneyFormat(target.getAvailable()) + context.getString(R
                .string.money_unit) + "，");
        // 剩餘金額
        tvRemain.setText(context.getString(R.string.remain) + NumberFormater.getMoneyFormat(target.getRemain()) + context.getString(R.string.money_unit));
    }

    public void setTarget(Department department) {
        target = new ReimbursementTarget();
        target.setType(ReimbursementType.DEPARTMENT);
        target.setTargetId(department.getId());
        target.setName(department.getName());
        target.setAvailable(department.getExpense_quota());
        target.setRemain(department.getExpense_left());
        tvName.setText(target.getName());
        if (TokenManager.getInstance().getUser().getId().equals(department.getManager_id()))
        {
            // 可報銷定額
            tvAvailable.setText(context.getString(R.string.exp_available) + NumberFormater.getMoneyFormat(target.getAvailable()) + context.getString(R.string.money_unit) + "，");
        }

        // 剩餘金額
        tvRemain.setText(context.getString(R.string.remain) + NumberFormater.getMoneyFormat(target.getRemain()) + context.getString(R.string.money_unit));
    }

    public ReimbursementTarget getTarget() {
        return target;
    }
}
