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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.activity.customer.ContactPersonCreateActivity;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.ContactPerson;
import tw.com.masterhand.gmorscrm.room.record.Contacter;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.ContactPersonSelectCard;

public class ContactPersonSelectActivity extends BaseUserCheckActivity implements TabLayout.OnTabSelectedListener, ContactPersonSelectCard.OnSelectListener {
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
    @BindView(R.id.btnAdd)
    Button btnAdd;

    List<Contacter> selected;
    List<ContactPerson> contactList;
    Map<String, Integer> tab;
    boolean isMultiSelectMode;

    String customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_person_select);
        init();
    }


    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        getContactList();
    }

    private void init() {
        isMultiSelectMode = getIntent().getBooleanExtra(MyApplication.INTENT_KEY_ENABLE, true);
        tvEmpty.setVisibility(View.GONE);

        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            ((LinearLayout) root).setDividerPadding(10);
            ((LinearLayout) root).setDividerDrawable(ContextCompat.getDrawable(this, R.drawable.divider_gray_h));
        }

        customerId = getIntent().getStringExtra(MyApplication.INTENT_KEY_CUSTOMER);
        if (TextUtils.isEmpty(customerId))
            finish();

        String contactString = getIntent().getStringExtra(MyApplication.INTENT_KEY_PEOPLE);
        if (!TextUtils.isEmpty(contactString)) {
            selected = gson.fromJson(contactString, new TypeToken<ArrayList<Contacter>>() {}.getType());
        }
        else {
            selected = new ArrayList<>();
        }

        appbar.setTitle(getString(R.string.title_activity_contact_select));//新增聯絡人
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCompleted();
            }
        });
    }

    void onCompleted() {
        for (int i = 0; i < container.getChildCount(); i++) {
            ContactPersonSelectCard item = (ContactPersonSelectCard) container.getChildAt(i);
            if (item.isSelected())
                addToSelected(item.getContactPerson());
            else
                deleteFromSelected(item.getContactPerson());
        }
        Intent intent = new Intent();
        intent.putExtra(MyApplication.INTENT_KEY_PEOPLE, gson.toJson(selected));
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    /**
         * 取得客戶聯絡人
         */
    void getContactList() {
        Logger.e(TAG, "getContactList");
        DatabaseHelper.getInstance(this).getContactPersonByCustomer(customerId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(Subscription subscription) throws Exception {
                        container.removeAllViews();
                        contactList = new ArrayList<>();
                        tabLayout.removeAllTabs();
                        tab = new HashMap<>();
                    }
                })
                .subscribe(new FlowableSubscriber<ContactPerson>() {

                    @Override
                    public void onSubscribe(@NonNull Subscription s) {
                        s.request(Integer.MAX_VALUE);
                    }

                    @Override
                    public void onNext(ContactPerson contact) {
                        contactList.add(contact);
                        if (!TextUtils.isEmpty(contact.getCustomer_department_name()))
                            if (tab.containsKey(contact.getCustomer_department_name()))
                            {
                                tab.put(contact.getCustomer_department_name(), tab.get(contact.getCustomer_department_name()) + 1);
                            }
                            else
                            {
                                tab.put(contact.getCustomer_department_name(), 1);
                            }

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        ContactPersonSelectCard card = generateItem(contact);
                        if (inSelected(contact))
                            card.setSelected(true);

                        card.setSelectListner(ContactPersonSelectActivity.this);
                        container.addView(card, params);
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {
                        Drawable icon = ContextCompat.getDrawable(ContactPersonSelectActivity.this, R.mipmap.common_tra_gonext);
                        icon.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
                        tabLayout.addTab(generateTab(icon, getString(R.string
                                .all_people), contactList.size()));
                        updateTab();
                    }
                });
    }

    private void updateTab() {
        Logger.e(TAG, "updateTab:" + tab.keySet().size());
        for (Map.Entry<String, Integer> entry : tab.entrySet()) {
            tabLayout.addTab(generateTab(null, entry.getKey(), entry.getValue()));
        }
        tabLayout.addOnTabSelectedListener(this);
    }

    /**
     * 產生部門tab項目
     */
    private TabLayout.Tab generateTab(Drawable icon, String title, int count) {
        TabLayout.Tab tab = tabLayout.newTab();
        View view = getLayoutInflater().inflate(R.layout.tab, tabLayout, false);
        TextView tvTitle = view.findViewById(R.id.textView_title);
        TextView tvSubtitle = view.findViewById(R.id.textView_subtitle);
        tvTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null);
        tvTitle.setText(title);
        tvSubtitle.setText("" + count + getString(R.string.people_unit));
        tab.setCustomView(view);
        if (title != null)
        {
            tab.setTag(title);
        }
        else
        {
            tab.setTag("");
        }

        return tab;
    }

    private void updateList() {
        Logger.e(TAG, "updateList:" + contactList.size());
        if (contactList.size() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            return;
        }

        String departmentName = (String) tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getTag();
        if (TextUtils.isEmpty(departmentName) || departmentName.equals(getString(R.string
                .all_people))) {
            for (int i = 0; i < container.getChildCount(); i++) {
                ContactPersonSelectCard card = (ContactPersonSelectCard) container.getChildAt(i);
                card.setVisibility(View.VISIBLE);
            }
        }
        else {
            int count = 0;
            for (int i = 0; i < container.getChildCount(); i++) {
                ContactPersonSelectCard card = (ContactPersonSelectCard) container.getChildAt(i);
                if (card.getContactPerson().getCustomer_department_name().equals(departmentName)) {
                    card.setVisibility(View.VISIBLE);
                    count++;
                } else {
                    card.setVisibility(View.GONE);
                }
            }

            if (count > 0)
            {
                tvEmpty.setVisibility(View.GONE);
            }
            else
            {
                tvEmpty.setVisibility(View.VISIBLE);
            }
        }
    }

    private void addToSelected(ContactPerson input) {
        if (inSelected(input))
        {
            return;
        }

        Contacter contacter = new Contacter();
        contacter.setUser_id(input.getId());
        selected.add(contacter);
    }

    private void deleteFromSelected(ContactPerson input) {
        for (Contacter contacter : selected) {
            if (contacter.getUser_id().equals(input.getId()) && contacter.getDeleted_at() == null) {
                contacter.setDeleted_at(new Date());
                break;
            }
        }
    }

    private boolean inSelected(ContactPerson contactPerson) {
        for (Contacter selectedItem : selected) {
            if (selectedItem.getUser_id().equals(contactPerson.getId()) && selectedItem.getDeleted_at() == null)
            {
                return true;
            }
        }

        return false;
    }

    private ContactPersonSelectCard generateItem(final ContactPerson contactPerson) {
        ContactPersonSelectCard card = new ContactPersonSelectCard(this);
        card.setContactPerson(contactPerson);
        return card;
    }

    @OnClick(R.id.btnAdd)
    void createContact() {
        Intent intent = new Intent(this, ContactPersonCreateActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_CUSTOMER, customerId);
        startActivityForResult(intent, MyApplication.REQUEST_ADD_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MyApplication.REQUEST_ADD_CONTACT:
                    getContactList();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
            ContactPersonSelectCard card = (ContactPersonSelectCard) container.getChildAt(i);
            if (btnSelectAll.isSelected())
                card.setSelected(true);
            else
                card.setSelected(false);
        }
    }

    /**
         * 檢查全選狀態
         */
    void checkSelectAll() {
        boolean shouldAllSelected = true;
        for (int i = 0; i < container.getChildCount(); i++) {
            ContactPersonSelectCard card = (ContactPersonSelectCard) container.getChildAt(i);
            if (!card.isSelected()) {
                shouldAllSelected = false;
                break;
            }
        }

        btnSelectAll.setSelected(shouldAllSelected);
        if (btnSelectAll.isSelected())
            btnSelectAll.setText(R.string.select_all_cancel);
        else
            btnSelectAll.setText(R.string.select_all);
    }

    @Override
    public void onSelect(ContactPerson contactPerson) {
        if (isMultiSelectMode)
            checkSelectAll();
        else
            onCompleted();
    }

    @Override
    public void onUnselect(ContactPerson contactPerson) {
        btnSelectAll.setSelected(false);
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
}
