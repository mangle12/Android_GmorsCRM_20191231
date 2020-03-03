package tw.com.masterhand.gmorscrm.activity.task;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;

import butterknife.BindView;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;
import tw.com.masterhand.gmorscrm.enums.SubmitStatus;
import tw.com.masterhand.gmorscrm.fragments.SampleDetailFragment;
import tw.com.masterhand.gmorscrm.model.MultipleSubmit;
import tw.com.masterhand.gmorscrm.model.SampleWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.ItemApproval;

public class SampleDetailActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.itemApproval)
    protected ItemApproval itemApproval;

    protected SampleWithConfig sample;
    protected String viewerId;
    protected String tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_detail);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        viewerId = TokenManager.getInstance().getUser().getId();
        Logger.e(TAG, "viewerId:" + viewerId);
        getData();
    }

    protected void init() {

        appbar.setTitle(getString(R.string.apply_sample) + getString(R.string
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
        mDisposable.add(DatabaseHelper.getInstance(this).getSampleByTrip(tripId).observeOn
                (AndroidSchedulers.mainThread())
                .subscribe(new Consumer<SampleWithConfig>() {
                               @Override
                               public void accept
                                       (SampleWithConfig result)
                                       throws Exception {
                                   sample = result;
                                   Logger.i(TAG, "sample:" + gson.toJson(sample));
                                   /*2018-1-17新增:只有建立者可以修改*/
                                   /*2018-2-8新增:未提交項目才可以修改*/
                                   if (TokenManager.getInstance().getUser().getId().equals(result
                                           .getTrip()
                                           .getUser_id()) && result.getTrip().getSubmit() ==
                                           SubmitStatus.NONE) {
                                       appbar.removeFuction();
                                       appbar.addFunction(getString(R.string.submit), new View
                                               .OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               submit();
                                           }
                                       });
                                       appbar.addFunction(R.mipmap.common_edit, new View
                                               .OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               Intent intent = new Intent(view.getContext(),
                                                       SampleCreateActivity.class);
                                               intent.putExtra(MyApplication.INTENT_KEY_TRIP,
                                                       sample.getSample().getTrip_id());
                                               startActivity(intent);
                                           }
                                       });
                                   }
                                   SampleDetailFragment fragment
                                           = SampleDetailFragment
                                           .newInstance(sample,
                                                   viewerId);
                                   getSupportFragmentManager()
                                           .beginTransaction()
                                           .add(R.id.fragmentContainer,
                                                   fragment)
                                           .commit();
                               }
                           }
                        , new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(SampleDetailActivity.this, "getSampleByTrip error:" +
                                        throwable
                                                .getMessage(), Toast
                                        .LENGTH_LONG).show();
                            }
                        }
                ));
    }

    protected void submit() {
        final MultipleSubmit submit = new MultipleSubmit();
        submit.setTrip_ids(sample.getSample().getTrip_id());
        ApiHelper.getInstance().getSubmitApi().submit
                (TokenManager
                        .getInstance().getToken(), submit)
                .enqueue
                        (new Callback<JSONObject>() {

                            @Override
                            public void onResponse
                                    (Call<JSONObject> call,
                                     Response<JSONObject>
                                             response) {
                                try {
                                    switch (response.code
                                            ()) {
                                        case 200:
                                            JSONObject
                                                    result =
                                                    response
                                                            .body();
                                            int success =
                                                    result
                                                            .getInt
                                                                    ("success");
                                            if (success ==
                                                    1) {
                                                DatabaseHelper
                                                        .getInstance
                                                                (SampleDetailActivity.this)
                                                        .saveTripSubmit
                                                                (submit)
                                                        .observeOn
                                                                (AndroidSchedulers
                                                                        .mainThread())
                                                        .subscribe
                                                                (new CompletableObserver() {

                                                                    @Override
                                                                    public void
                                                                    onSubscribe
                                                                            (Disposable d) {
                                                                        mDisposable.add(d);
                                                                    }

                                                                    @Override
                                                                    public void
                                                                    onComplete() {
                                                                        setResult(ApprovalStatus
                                                                                .UNSIGN.getCode());
                                                                        finish();
                                                                    }

                                                                    @Override
                                                                    public void
                                                                    onError(Throwable e) {
                                                                        Toast.makeText
                                                                                (SampleDetailActivity
                                                                                                .this,
                                                                                        e.getMessage(),
                                                                                        Toast.LENGTH_LONG)
                                                                                .show();
                                                                    }
                                                                });
                                            } else {
                                                Toast.makeText
                                                        (SampleDetailActivity.this,
                                                                result.getString
                                                                        ("errorMsg"),
                                                                Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                            break;
                                        case ApiHelper
                                                .ERROR_TOKEN_EXPIRED:
                                            showLoginDialog();
                                            break;
                                        default:
                                            Logger.e(TAG,
                                                    "submit " +
                                                            "failed");
                                    }
                                } catch (Exception e) {
                                    ErrorHandler
                                            .getInstance()
                                            .setException
                                                    (SampleDetailActivity
                                                            .this, e);
                                }
                            }

                            @Override
                            public void onFailure
                                    (Call<JSONObject>
                                             call,
                                     Throwable t) {
                                Toast.makeText
                                        (SampleDetailActivity.this,
                                                t.getMessage(),
                                                Toast.LENGTH_LONG).show();
                            }
                        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
