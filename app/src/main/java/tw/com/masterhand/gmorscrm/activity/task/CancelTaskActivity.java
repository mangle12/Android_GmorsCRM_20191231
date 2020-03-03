package tw.com.masterhand.gmorscrm.activity.task;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Trip;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;

import static tw.com.masterhand.gmorscrm.MyApplication.INTENT_KEY_REASON;

public class CancelTaskActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.container_reason)
    LinearLayout containerReason;
    @BindView(R.id.editText_reason)
    EditText etReason;

    String[] reasons;
    String selected = "";

    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_task);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
    }

    private void init() {
        appbar.setBackground(ContextCompat.getColor(this, R.color.red));
        appbar.setTextColor(ContextCompat.getColor(this, R.color.white));
        appbar.setTitle(getString(R.string.title_activity_cancel_task));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (containerReason.getVisibility() == View.VISIBLE) {
                    selected = etReason.getText().toString();
                }
                if (TextUtils.isEmpty(selected)) {
                    Toast.makeText(CancelTaskActivity.this, getString(R.string.error_msg_empty),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                mDisposable.add(DatabaseHelper.getInstance(CancelTaskActivity.this).cancelTrip
                        (trip, selected)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>
                                () {

                            @Override
                            public void accept(String reason) throws Exception {
                                Intent intent = new Intent();
                                intent.putExtra(INTENT_KEY_REASON, reason);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                ErrorHandler.getInstance().setException(CancelTaskActivity.this,
                                        throwable);
                            }
                        }));
            }
        });

        reasons = getResources().getStringArray(R.array.cancel_reason);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen
                .list_size));
        for (String reason : reasons) {
            container.addView(generateItem(reason, params));
        }

        String tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            finish();
            return;
        }
        mDisposable.add(DatabaseHelper.getInstance(this).getTripById(tripId).observeOn
                (AndroidSchedulers
                        .mainThread()).subscribe(new Consumer<Trip>() {

            @Override
            public void accept(Trip result) throws Exception {
                trip = result;
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(CancelTaskActivity.this, throwable);
            }
        }));
    }

    private View generateItem(String reason, LinearLayout.LayoutParams params) {

        TextView tvItem = new TextView(this);
        tvItem.setText(reason);
        tvItem.setGravity(Gravity.CENTER);
        tvItem.setBackgroundResource(R.drawable.bg_cancel_reason_selector);
        tvItem.setTextColor(ContextCompat.getColorStateList(this, R.color.text_cancel_reason));
        tvItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tvItem = (TextView) view;
                for (int i = 0; i < container.getChildCount(); i++) {
                    container.getChildAt(i).setSelected(false);
                }
                selected = tvItem.getText().toString();
                tvItem.setSelected(true);
                if (tvItem.getText().toString().equals(reasons[reasons.length - 1])) {
                    containerReason.setVisibility(View.VISIBLE);
                } else {
                    containerReason.setVisibility(View.GONE);
                }
            }
        });
        tvItem.setLayoutParams(params);
        return tvItem;
    }
}
