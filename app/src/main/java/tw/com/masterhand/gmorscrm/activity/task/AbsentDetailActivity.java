package tw.com.masterhand.gmorscrm.activity.task;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.ApprovalListActivity;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;
import tw.com.masterhand.gmorscrm.enums.SubmitStatus;
import tw.com.masterhand.gmorscrm.model.AbsentWithConfig;
import tw.com.masterhand.gmorscrm.model.Conversation;
import tw.com.masterhand.gmorscrm.model.MultipleSubmit;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.ItemApproval;
import tw.com.masterhand.gmorscrm.view.ItemConversation;

public class AbsentDetailActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.textView_title)
    TextView tvTitle;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.btnApproval)
    Button btnApproval;
    @BindView(R.id.container_photo)
    LinearLayout containerPhoto;
    @BindView(R.id.tvReason)
    TextView tvReason;
    @BindView(R.id.tvApprovalDescription)
    TextView tvApprovalDescription;
    @BindView(R.id.container_conversation)
    LinearLayout containerConversation;
    @BindView(R.id.itemApproval)
    protected ItemApproval itemApproval;

    protected AbsentWithConfig absent;
    protected String viewerId;
    protected String tripId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absent_detail);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        getData();
    }

    protected void init() {
        viewerId = TokenManager.getInstance().getUser().getId();
        Logger.e(TAG, "viewerId:" + viewerId);
        appbar.setTitle(getString(R.string.work_absent) + getString(R.string
                .title_activity_work_detail));

        tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            finish();
        }
    }

    protected void getData() {
        if (TextUtils.isEmpty(tripId))
            return;
        Logger.i(TAG, "tripId:" + tripId);
        mDisposable.add(DatabaseHelper.getInstance(this).getAbsentByTrip(tripId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<AbsentWithConfig>() {
            @Override
            public void accept(AbsentWithConfig result) throws Exception {
                absent = result;
                /*2018-1-17新增:只有建立者可以修改*/
                /*2018-2-8新增:未提交項目才可以修改*/
                if (TokenManager.getInstance().getUser().getId().equals(result.getTrip()
                        .getUser_id()) && result.getTrip().getSubmit() == SubmitStatus.NONE) {
                    appbar.removeFuction();
                    appbar.addFunction(getString(R.string.submit), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            submit();
                        }
                    });
                    appbar.addFunction(R.mipmap.common_edit, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // 編輯
                            Intent intent = new Intent(view.getContext(), AbsentCreateActivity.class);
                            intent.putExtra(MyApplication.INTENT_KEY_TRIP, absent.getAbsent().getTrip_id());
                            startActivityForResult(intent, MyApplication.REQUEST_EDIT);
                        }
                    });
                }
                updateDetail();
                updatePhoto(absent.getFiles());
                updateConversation();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(AbsentDetailActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        }));

        ApiHelper.getInstance().getTripApi().getApprovalDescription(TokenManager.getInstance()
                .getToken(), tripId).enqueue(new Callback<JSONObject>() {

            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                try {
                    JSONObject result = response.body();
                    int success = result.getInt("success");
                    if (success == 1) {
                        String description = result.getString("description");
                        if (!TextUtils.isEmpty(description))
                            tvApprovalDescription.setText(description);
                    }
                } catch (Exception e) {
                    ErrorHandler.getInstance().setException(AbsentDetailActivity.this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                ErrorHandler.getInstance().setException(AbsentDetailActivity.this, t);
            }
        });
    }

    //提交
    protected void submit() {
        final MultipleSubmit submit = new MultipleSubmit();
        submit.setTrip_ids(absent.getAbsent().getTrip_id());

        ApiHelper.getInstance().getSubmitApi().submit(TokenManager.getInstance().getToken(), submit).enqueue(new Callback<JSONObject>() {

            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                try {
                    switch (response.code()) {
                        case 200:
                            JSONObject result = response.body();
                            int success = result.getInt("success");
                            if (success == 1) {
                                DatabaseHelper.getInstance(AbsentDetailActivity.this)
                                        .saveTripSubmit(submit)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new CompletableObserver() {

                                            @Override
                                            public void onSubscribe(Disposable d) {
                                                mDisposable.add(d);
                                            }

                                            @Override
                                            public void onComplete() {
                                                setResult(ApprovalStatus.UNSIGN.getCode());
                                                finish();
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Toast.makeText(AbsentDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(AbsentDetailActivity.this, result.getString("errorMsg"), Toast.LENGTH_LONG).show();
                            }
                            break;

                        case ApiHelper.ERROR_TOKEN_EXPIRED:
                            showLoginDialog();
                            break;
                        default:
                            Logger.e(TAG, "submit failed");
                    }
                } catch (Exception e) {
                    ErrorHandler.getInstance().setException(AbsentDetailActivity.this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Toast.makeText(AbsentDetailActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.btnApproval)
    void showApproval() {
        Intent intent = new Intent(this, ApprovalListActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_TRIP, absent.getAbsent().getTrip_id());
        startActivity(intent);
    }

    void updatePhoto(List<File> files) {
        containerPhoto.removeAllViews();
        Logger.e(TAG, "file count:" + files.size());
        for (File file : files) {
            containerPhoto.addView(generatePhotoItem(file));
        }
        if (containerPhoto.getChildCount() == 0)
            containerPhoto.addView(getEmptyTextView(getString(R.string.error_msg_no_photo)));
    }

    public void updateDetail() {
        tvTitle.setText(absent.getTrip().getName());
        String time = "";
        if (absent.getTrip().getDate_type()) {
            time += TimeFormater.getInstance().toDateFormat(absent.getTrip().getFrom_date());
            time += " - ";
            time += TimeFormater.getInstance().toDateFormat(absent.getTrip().getTo_date());
        } else {
            time += TimeFormater.getInstance().toDateTimeFormat(absent.getTrip().getFrom_date());
            time += " - ";
            time += TimeFormater.getInstance().toDateTimeFormat(absent.getTrip().getTo_date());
        }
        tvTime.setText(time);
        tvReason.setText(absent.getAbsent().getReason());
        tvName.setText(absent.getUserName());
    }

    /**
     * 更新對話
     */
    public void updateConversation() {
        containerConversation.removeAllViews();
        if (absent == null) {
            Logger.e(TAG, "absent is null");
            return;
        }
        mDisposable.add(
                DatabaseHelper.getInstance(this).getConversationByParent(absent.getAbsent().getId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Conversation>() {
                    @Override
                    public void accept(Conversation conversation) throws Exception {
                        ItemConversation itemConversation = new ItemConversation(AbsentDetailActivity.this);
                        itemConversation.setConversation(conversation);
                        itemConversation.getMainMessage().showIndex(false);
                        containerConversation.addView(itemConversation);
                    }
                }));
    }


    /**
     * 新增討論
     */
    @OnClick(R.id.imageButton_add)
    void addConversation() {
        if (absent == null) {
            Logger.e(TAG, "absent id is null");
            return;
        }
        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_TRIP, absent.getAbsent().getTrip_id());
        intent.putExtra(MyApplication.INTENT_KEY_ID, absent.getAbsent().getId());
        startActivityForResult(intent, MyApplication.REQUEST_ADD_CONVERSATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MyApplication.REQUEST_ADD_CONVERSATION:
                    updateConversation();
                    break;
                default:

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
