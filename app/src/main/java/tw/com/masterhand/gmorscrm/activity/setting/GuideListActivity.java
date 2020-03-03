package tw.com.masterhand.gmorscrm.activity.setting;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import tw.com.masterhand.gmorscrm.BaseActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.model.GuideData;
import tw.com.masterhand.gmorscrm.model.GuidePage;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;
import tw.com.masterhand.gmorscrm.view.Appbar;

public class GuideListActivity extends BaseActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.container)
    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_list);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        appbar.invalidate();

    }

    private void init() {
        appbar.setTitle(getString(R.string.setting_guide));
        updateList();
    }

    private void updateList() {
        container.removeAllViews();
        String[] titleList = getResources().getStringArray(R.array.guide_category_title);
        for (int j = 0; j < titleList.length; j++) {
            generateTitle(titleList[j]);
            List<GuideData> itemList = getGuideList(j);
            for (int i = 0; i < itemList.size(); i++) {
                generateItem(itemList.get(i));
            }
        }
    }

    //標題
    private void generateTitle(String title) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.btn_size));
        params.topMargin = UnitChanger.dpToPx(16);
        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setGravity(Gravity.CENTER_VERTICAL);
        tvTitle.setPadding(UnitChanger.dpToPx(16), 0, 0, 0);
        tvTitle.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_required));
        container.addView(tvTitle, params);
    }

    //項目
    private void generateItem(final GuideData data) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.btn_size));
        Button item = new Button(this);
        item.setText(data.getTitle());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            item.setStateListAnimator(null);
        }

        item.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_light));
        item.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        item.setPadding(UnitChanger.dpToPx(8), 0, 0, 0);
        item.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.mipmap.common_arrow_right, 0);
        container.addView(item, params);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuideListActivity.this, GuideActivity.class);
                intent.putExtra(MyApplication.INTENT_KEY_GUIDE, new Gson().toJson(data));
                startActivity(intent);
            }
        });
    }

    private List<GuideData> getGuideList(int index) {
        ArrayList<GuideData> list = new ArrayList<>();
        switch (index) {
            case 0: {
                list.add(getGuide(0));
                list.add(getGuide(1));
                list.add(getGuide(2));
                list.add(getGuide(3));
                list.add(getGuide(4));
                list.add(getGuide(5));
                break;
            }
            case 1: {
                list.add(getGuide(6));
                list.add(getGuide(7));
                list.add(getGuide(8));
                list.add(getGuide(9));
                list.add(getGuide(10));
                list.add(getGuide(11));
                break;
            }
            case 2: {
                list.add(getGuide(12));
                list.add(getGuide(13));
                break;
            }
            case 3: {
                list.add(getGuide(14));
                list.add(getGuide(15));
                list.add(getGuide(16));
                break;
            }
        }
        return list;
    }

    private GuideData getGuide(int index) {
        List<GuidePage> pageList = new ArrayList<>();
        String[] titleList = getResources().getStringArray(R.array.guide_title);
        String[] msgList = new String[0];
        int[] imgList = new int[0];
        String title = titleList[index];
        switch (index) {
            case 0: {
                msgList = getResources().getStringArray(R.array.crm_add_appointment);
                imgList = new int[]{R.mipmap.crm_add_appointment1, R.mipmap.crm_add_appointment2,
                        R.mipmap.crm_add_appointment3, R.mipmap.crm_add_appointment4, R.mipmap
                        .crm_add_appointment5, R.mipmap.crm_add_appointment6};
                break;
            }
            case 1: {
                msgList = getResources().getStringArray(R.array.crm_approval_quote);
                imgList = new int[]{R.mipmap.crm_quotation1, R.mipmap.crm_quotation2, R.mipmap
                        .crm_quotation3, R.mipmap.crm_quotation4, R.mipmap.crm_quotation5, R
                        .mipmap.crm_quotation6};
                break;
            }
            case 2: {
                msgList = getResources().getStringArray(R.array.crm_submit_quote);
                imgList = new int[]{R.mipmap.crm_submit_report1, R.mipmap.crm_submit_report2, R
                        .mipmap.crm_submit_report3, R.mipmap.crm_submit_report4};
                break;
            }
            case 3: {
                msgList = getResources().getStringArray(R.array.crm_reimburse);
                imgList = new int[]{R.mipmap.crm_reimburse1, R.mipmap.crm_reimburse2, R.mipmap
                        .crm_reimburse3, R.mipmap.crm_reimburse4};
                break;
            }
            case 4: {
                msgList = getResources().getStringArray(R.array.crm_salesforce);
                imgList = new int[]{R.mipmap.crm_salesforce1, R.mipmap.crm_salesforce2, R.mipmap
                        .crm_salesforce3, R.mipmap.crm_salesforce4, R.mipmap.crm_salesforce5};
                break;
            }
            case 5: {
                msgList = getResources().getStringArray(R.array.crm_ledger);
                imgList = new int[]{R.mipmap.crm_ledger1, R.mipmap.crm_ledger2, R.mipmap
                        .crm_ledger3, R.mipmap.crm_ledger4, R.mipmap.crm_ledger5};

                break;
            }
            case 6: {
                msgList = getResources().getStringArray(R.array.crm_client);
                imgList = new int[]{R.mipmap.crm_client1, R.mipmap.crm_client2, R.mipmap
                        .crm_client3, R.mipmap.crm_client4, R.mipmap.crm_client5, R.mipmap
                        .crm_client6};

                break;
            }
            case 7: {

                msgList = getResources().getStringArray(R.array.crm_client_add_project);
                imgList = new int[]{R.mipmap.crm_client_add_project1, R.mipmap
                        .crm_client_add_project2, R.mipmap.crm_client_add_project3, R.mipmap
                        .crm_client_add_project4};
                break;
            }
            case 8: {
                msgList = getResources().getStringArray(R.array.crm_client_check_project);
                imgList = new int[]{R.mipmap.crm_client_check_content1, R.mipmap
                        .crm_client_check_content2};

                break;
            }
            case 9: {
                msgList = getResources().getStringArray(R.array.crm_client_add_history);
                imgList = new int[]{R.mipmap.crm_client_add_history1, R.mipmap
                        .crm_client_add_history2, R.mipmap.crm_client_add_history3, R.mipmap
                        .crm_client_add_history4, R.mipmap.crm_client_add_history5};

                break;
            }
            case 10: {
                msgList = getResources().getStringArray(R.array.crm_client_check_history);
                imgList = new int[]{R.mipmap.crm_client_check_history1, R.mipmap
                        .crm_client_check_history2, R.mipmap.crm_client_check_history3};

                break;
            }
            case 11: {
                msgList = getResources().getStringArray(R.array.crm_client_add_salesstage);
                imgList = new int[]{R.mipmap.crm_client_add_salesstage1, R.mipmap
                        .crm_client_add_salesstage2, R.mipmap.crm_client_add_salesstage3, R
                        .mipmap.crm_client_add_salesstage4, R.mipmap.crm_client_add_salesstage5};

                break;
            }
            case 12: {
                msgList = getResources().getStringArray(R.array.crm_approval_list);
                imgList = new int[]{R.mipmap.crm_approval_list1, R.mipmap.crm_approval_list2, R
                        .mipmap.crm_approval_list3, R.mipmap.crm_approval_list4, R.mipmap
                        .crm_approval_list5, R.mipmap.crm_approval_list6, R.mipmap
                        .crm_approval_list7};
                break;
            }
            case 13: {
                msgList = getResources().getStringArray(R.array.crm_execution);
                imgList = new int[]{R.mipmap.crm_execution1, R.mipmap.crm_execution2, R.mipmap
                        .crm_execution3, R.mipmap.crm_execution4, R.mipmap.crm_execution5, R
                        .mipmap.crm_execution6};

                break;
            }
            case 14: {
                msgList = getResources().getStringArray(R.array.crm_profile);
                imgList = new int[]{R.mipmap.crm_profile1, R.mipmap.crm_profile2, R.mipmap
                        .crm_profile3, R.mipmap.crm_profile4};
                break;
            }
            case 15: {
                msgList = getResources().getStringArray(R.array.crm_setting_change_password);
                imgList = new int[]{R.mipmap.crm_setting_change_password1, R.mipmap
                        .crm_setting_change_password2, R.mipmap.crm_setting_change_password3, R
                        .mipmap.crm_setting_change_password4};
                break;
            }
            case 16: {
                msgList = getResources().getStringArray(R.array.crm_setting_gesture_lock);
                imgList = new int[]{R.mipmap.crm_setting_gesture_lock1, R.mipmap
                        .crm_setting_gesture_lock2, R.mipmap.crm_setting_gesture_lock3, R.mipmap
                        .crm_setting_gesture_lock4, R.mipmap.crm_setting_gesture_lock5, R.mipmap
                        .crm_setting_gesture_lock6};
                break;
            }
        }
        if (msgList.length > 0 && imgList.length > 0) {
            for (int i = 0; i < msgList.length; i++) {
                GuidePage page = new GuidePage(title, msgList[i], imgList[i]);
                pageList.add(page);
            }
            return new GuideData(title, pageList);
        } else
            return null;

    }
}
