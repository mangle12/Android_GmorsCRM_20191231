package tw.com.masterhand.gmorscrm;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;
import org.reactivestreams.Subscription;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.setting.Department;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.ParticipantSelectCard;

public class UserSelectActivity extends BaseUserCheckActivity implements TabLayout
        .OnTabSelectedListener, ParticipantSelectCard.OnSelectListener {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.tvEmpty)
    TextView tvEmpty;

    List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        String approverId = getIntent().getStringExtra(MyApplication.INTENT_KEY_APPROVER);
        if (TextUtils.isEmpty(approverId)) {
            // 選擇本地資料庫使用者
            appbar.setTitle(getString(R.string.title_activity_user_select));
            mDisposable.add(DatabaseHelper.getInstance(this).getUserByCompany(TokenManager
                    .getInstance().getUser()
                    .getCompany_id()).observeOn(AndroidSchedulers
                    .mainThread()).toList().subscribe(new Consumer<List<User>>() {
                @Override
                public void accept(List<User> users) throws Exception {
                    stopProgressDialog();
                    userList = users;
                    initList();
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable t) throws Exception {
                    Toast.makeText(UserSelectActivity.this, t.getMessage(), Toast.LENGTH_LONG)
                            .show();
                    finish();
                }
            }));
        } else {
            // 線上取得可更換的審批人
            appbar.setTitle(getString(R.string.title_activity_approver_select));
            ApiHelper.getInstance().getApprovalApi().getAvailableApprovers(TokenManager
                    .getInstance().getToken(), approverId).enqueue(new Callback<JSONObject>() {

                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                    stopProgressDialog();
                    try {
                        switch (response.code()) {
                            case 200:
                                JSONObject result = response.body();
                                int success = result.getInt("success");
                                if (success == 1) {
                                    userList = gson.fromJson(result.getString("users"), new
                                            TypeToken<List<User>>() {
                                            }.getType());
                                    initList();
                                } else {
                                    Toast.makeText(UserSelectActivity.this,
                                            result.getString("errorMsg"),
                                            Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                break;
                            default:
                                Logger.e(TAG, "get approver failed");
                                finish();
                        }
                    } catch (Exception e) {
                        ErrorHandler.getInstance().setException
                                (UserSelectActivity.this, e);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t) {
                    Toast.makeText(UserSelectActivity.this, t.getMessage(), Toast.LENGTH_LONG)
                            .show();
                    finish();
                }
            });
        }
    }

    private void init() {
        startProgressDialog();
        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            ((LinearLayout) root).setDividerPadding(20);
            ((LinearLayout) root).setDividerDrawable(ContextCompat.getDrawable(this, R.drawable
                    .divider_gray_h));
        }

    }

    private void initList() {
        container.removeAllViews();
        for (User user : userList) {
            LinearLayout
                    .LayoutParams params = new LinearLayout
                    .LayoutParams
                    (LinearLayout
                            .LayoutParams.MATCH_PARENT, LinearLayout
                            .LayoutParams
                            .WRAP_CONTENT);
            ParticipantSelectCard card = generateItem(user);
            card.setSelectListner(UserSelectActivity.this);
            container.addView(card, params);
        }
        Drawable icon = ContextCompat.getDrawable(UserSelectActivity
                        .this,
                R.mipmap.common_tra_gonext);
        icon.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        tabLayout.removeAllTabs();
        tabLayout.addTab(generateTab(null, icon, getString(R.string
                .all_people), userList.size()));
        updateTab();
        updateList();
    }

    private void updateTab() {
        DatabaseHelper.getInstance(this).getDepartment(TokenManager.getInstance().getUser()
                .getCompany_id()).observeOn(AndroidSchedulers.mainThread()
        ).subscribe(new FlowableSubscriber<Department>() {

            @Override
            public void onSubscribe(@NonNull Subscription s) {
                s.request(Integer.MAX_VALUE);
            }

            @Override
            public void onNext(Department department) {
                tabLayout.addTab(generateTab(department, null, department.getName(),
                        getDepartmentUserCount(department)));
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                tabLayout.addOnTabSelectedListener(UserSelectActivity.this);
            }
        });
    }

    /**
     * 取得部門人數
     */
    private int getDepartmentUserCount(Department department) {
        int count = 0;
        for (User user : userList) {
            if (user.getDepartment_id().equals(department.getId()))
                count++;
        }
        return count;
    }

    /**
     * 產生部門tab項目
     */
    private TabLayout.Tab generateTab(Department department, Drawable icon,
                                      String title, int count) {
        TabLayout.Tab tab = tabLayout.newTab();
        View view = getLayoutInflater().inflate(R.layout.tab, tabLayout, false);
        TextView tvTitle = view.findViewById(R.id.textView_title);
        TextView tvSubtitle = view.findViewById(R.id.textView_subtitle);
        tvTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null);
        tvTitle.setText(title);
        tvSubtitle.setText("" + count + getString(R.string.people_unit));
        tab.setCustomView(view);
        if (department != null)
            tab.setTag(department.getId());
        else
            tab.setTag("");
        return tab;
    }

    private ParticipantSelectCard generateItem(User participant) {
        ParticipantSelectCard card = new ParticipantSelectCard(this);
        card.setParticipant(participant);
        return card;
    }

    void updateList() {
        if (userList.size() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            return;
        }
        tvEmpty.setVisibility(View.GONE);
        String departmentId = (String) tabLayout.getTabAt(tabLayout.getSelectedTabPosition())
                .getTag();
        if (TextUtils.isEmpty(departmentId)) {
            for (int i = 0; i < container.getChildCount(); i++) {
                ParticipantSelectCard card = (ParticipantSelectCard) container.getChildAt(i);
                card.setVisibility(View.VISIBLE);
            }
        } else {
            int count = 0;
            for (int i = 0; i < container.getChildCount(); i++) {
                ParticipantSelectCard card = (ParticipantSelectCard) container.getChildAt(i);
                if (card.getParticipant().getDepartment_id().equals(departmentId)) {
                    card.setVisibility(View.VISIBLE);
                    count++;
                } else {
                    card.setVisibility(View.GONE);
                }
            }
            if (count == 0)
                tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        updateList();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onSelect(User participant) {
        Intent intent = new Intent();
        intent.putExtra(MyApplication.INTENT_KEY_USER, participant.getId());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onUnselect(User participant) {
    }
}
