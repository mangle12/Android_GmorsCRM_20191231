package tw.com.masterhand.gmorscrm.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.ActivityChooserView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.UserSelectActivity;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.setting.Department;
import tw.com.masterhand.gmorscrm.room.setting.User;

public class FilterPersonal extends RelativeLayout {
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvSelected)
    TextView tvSelected;

    Context context;
    User user;
    OnSelectedListener listener;

    public interface OnSelectedListener {
        void onSelected(User user);
    }

    public void setListener(OnSelectedListener listener) {
        this.listener = listener;
    }

    public FilterPersonal(Context context) {
        super(context);
        init(context);
    }

    public FilterPersonal(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FilterPersonal(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_statistic_filter, this);
        ButterKnife.bind(this, view);
        if (isInEditMode())
            return;
        tvTitle.setText(R.string.filter_personal);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserSelectActivity.class);
                ((Activity) context).startActivityForResult(intent, MyApplication.REQUEST_SELECT_USER);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MyApplication.REQUEST_SELECT_USER) {
                final String userId = data.getStringExtra(MyApplication.INTENT_KEY_USER);
                DatabaseHelper.getInstance(context).getUserById(userId).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<User>() {

                    @Override
                    public void accept(User result) throws Exception {
                        if (result != null) {
                            setSelected(result);
                            if (listener != null)
                                listener.onSelected(user);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        user = null;
                        tvSelected.setText(R.string.empty_show);
                    }
                });
            }
        }
    }

    public void setSelected(User result) {
        if (result != null) {
            user = result;
            tvSelected.setText(user.getShowName());
        }
    }

    public User getSelected() {
        return user;
    }

    public void clear() {
        user = null;
        tvSelected.setText(R.string.empty_show);
    }
}
