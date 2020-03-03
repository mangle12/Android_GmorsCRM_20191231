package tw.com.masterhand.gmorscrm;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.enums.AcceptType;
import tw.com.masterhand.gmorscrm.enums.SignStatus;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.room.setting.Department;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.ParticipantSelectCard;

public class ParticipantSelectActivity extends BaseUserCheckActivity implements TabLayout
        .OnTabSelectedListener, ParticipantSelectCard.OnSelectListener {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.tvEmpty)
    TextView tvEmpty;
    @BindView(R.id.btnSelectAll)
    Button btnSelectAll;

    List<Participant> selected;
    List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_select);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        if (tabLayout.getTabCount() < 2)
            DatabaseHelper.getInstance(this).getUserByCompany(TokenManager.getInstance().getUser()
                    .getCompany_id()).observeOn(AndroidSchedulers
                    .mainThread()).doOnSubscribe(new Consumer<Subscription>() {
                @Override
                public void accept(Subscription subscription) throws Exception {
                    userList = new ArrayList<>();
                }
            }).subscribe(new FlowableSubscriber<User>() {
                @Override
                public void onSubscribe(@NonNull Subscription s) {
                    s.request(Integer.MAX_VALUE);
                }

                @Override
                public void onNext(User user) {
                    userList.add(user);
                    LinearLayout
                            .LayoutParams params = new LinearLayout
                            .LayoutParams
                            (LinearLayout
                                    .LayoutParams.MATCH_PARENT, LinearLayout
                                    .LayoutParams
                                    .WRAP_CONTENT);
                    ParticipantSelectCard card = generateItem(user);
                    if (inSelected(user))
                        card.setSelected(true);
                    if (!user.getId().equals(TokenManager.getInstance().getUser().getId())) {
                        card.setSelectListner(ParticipantSelectActivity.this);
                        container.addView(card, params);
                    }
                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onComplete() {
                    if (container.getChildCount() > 0) {
                        tvEmpty.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                    Drawable icon = ContextCompat.getDrawable(ParticipantSelectActivity.this,
                            R.mipmap.common_tra_gonext);
                    icon.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
                    tabLayout.addTab(generateTab(null, icon, getString(R.string
                            .all_people), userList.size()));
                    updateTab();
                }
            });
    }

    private void init() {
        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            ((LinearLayout) root).setDividerPadding(20);
            ((LinearLayout) root).setDividerDrawable(ContextCompat.getDrawable(this, R.drawable
                    .divider_gray_h));
        }
        String participantString = getIntent().getStringExtra(MyApplication
                .INTENT_KEY_PEOPLE);
        if (!TextUtils.isEmpty(participantString)) {
            selected = gson.fromJson(participantString, new
                    TypeToken<ArrayList<Participant>>() {
                    }.getType());
        } else {
            selected = new ArrayList<>();
        }
        appbar.setTitle(getString(R.string.title_activity_participant_select));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < container.getChildCount(); i++) {
                    ParticipantSelectCard item = (ParticipantSelectCard) container.getChildAt
                            (i);
                    User user = item.getParticipant();
                    if (item.isSelected()) {
                        addToSelected(user);
                    } else {
                        deleteFromSelected(user);
                    }
                }
                Intent intent = new Intent();
                intent.putExtra(MyApplication.INTENT_KEY_PEOPLE, gson.toJson(selected));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });


    }

    private void addToSelected(User user) {
        if (inSelected(user))
            return;
        Participant participant = new Participant();
        participant.setStatus(SignStatus.NONE);
        participant.setAccept(AcceptType.NONE);
        participant.setUser_id(user.getId());
        selected.add(participant);
    }

    private void deleteFromSelected(User user) {
        for (Participant participant : selected) {
            if (participant.getUser_id().equals(user.getId()) && participant.getDeleted_at() ==
                    null) {
                if (TextUtils.isEmpty(participant.getId()))
                    selected.remove(participant);
                else
                    participant.setDeleted_at(new Date());
                break;
            }
        }
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
                tabLayout.addOnTabSelectedListener(ParticipantSelectActivity.this);
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

    private boolean inSelected(User participant) {
        for (Participant selectedItem : selected) {
            if (selectedItem.getUser_id().equals(participant.getId()) && selectedItem
                    .getDeleted_at() == null)
                return true;
        }
        return false;
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
            if (count > 0)
                tvEmpty.setVisibility(View.GONE);
            else
                tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 點擊全選
     */
    @OnClick(R.id.btnSelectAll)
    void selectAll() {
        btnSelectAll.setSelected(!btnSelectAll.isSelected());
        if (btnSelectAll.isSelected())
            btnSelectAll.setText(R.string.select_all_cancel);
        else
            btnSelectAll.setText(R.string.select_all);
        for (int i = 0; i < container.getChildCount(); i++) {
            ParticipantSelectCard card = (ParticipantSelectCard) container.getChildAt(i);
            if (card.getVisibility() == View.VISIBLE) {
                if (btnSelectAll.isSelected())
                    card.setSelected(true);
                else
                    card.setSelected(false);
            }
        }
    }

    /**
     * 檢查全選狀態
     */
    void checkSelectAll() {
        boolean shouldAllSelected = true;
        for (int i = 0; i < container.getChildCount(); i++) {
            ParticipantSelectCard card = (ParticipantSelectCard) container.getChildAt(i);
            if (card.getVisibility() == View.VISIBLE) {
                if (!card.isSelected()) {
                    shouldAllSelected = false;
                    break;
                }
            }
        }
        btnSelectAll.setSelected(shouldAllSelected);
        if (btnSelectAll.isSelected())
            btnSelectAll.setText(R.string.select_all_cancel);
        else
            btnSelectAll.setText(R.string.select_all);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        updateList();
        checkSelectAll();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onSelect(User participant) {
        checkSelectAll();
    }

    @Override
    public void onUnselect(User participant) {
        btnSelectAll.setSelected(false);
        btnSelectAll.setText(R.string.select_all);
    }
}
