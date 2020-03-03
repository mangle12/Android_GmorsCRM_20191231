package tw.com.masterhand.gmorscrm.activity.task;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCompanyHiddenField;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.view.Appbar;

public class CreateActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.container_work)
    LinearLayout container_work;
    @BindView(R.id.container_apply)
    LinearLayout container_apply;

    List<TripType> hiddenTripType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
    }

    @Override
    protected void onUserChecked() {
        getHiddenField();
        appbar.invalidate();
    }

    /*
        * 隱藏功能
        */
    protected void getHiddenField() {
        hiddenTripType = new ArrayList<>();
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigCompanyHiddenField(TokenManager.getInstance().getUser().getCompany_id())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ConfigCompanyHiddenField>() {
            @Override
            public void accept(ConfigCompanyHiddenField configCompanyHiddenField) throws Exception {

                if (configCompanyHiddenField.getAdd_trip_hidden_absent()) {
                    hiddenTripType.add(TripType.ABSENT);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_contract()) {
                    hiddenTripType.add(TripType.CONTRACT);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_express()) {
                    hiddenTripType.add(TripType.EXPRESS);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_new_project_production()) {
                    hiddenTripType.add(TripType.PRODUCTION);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_nonstandard_inquiry()) {
                    hiddenTripType.add(TripType.NON_STANDARD_INQUIRY);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_office()) {
                    hiddenTripType.add(TripType.OFFICE);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_quotation()) {
                    hiddenTripType.add(TripType.QUOTATION);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_reimbursement()) {
                    hiddenTripType.add(TripType.REIMBURSEMENT);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_sample()) {
                    hiddenTripType.add(TripType.SAMPLE);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_special_price()) {
                    hiddenTripType.add(TripType.SPECIAL_PRICE);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_spring_ring_inquiry()) {
                    hiddenTripType.add(TripType.SPRING_RING_INQUIRY);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_task()) {
                    hiddenTripType.add(TripType.TASK);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_travel()) {
                    hiddenTripType.add(TripType.TRAVEL);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_visit()) {
                    hiddenTripType.add(TripType.VISIT);
                }

                hiddenTripType.add(TripType.REPAYMENT);//回款訊息
                hiddenTripType.add(TripType.SPECIAL_SHIP);//特批發貨
                updateList();

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(CreateActivity.this, throwable);
                updateList();
            }
        }));
    }

    private void updateList() {
        appbar.setTitle(getString(R.string.title_activity_create));//新建工作
        container_work.removeAllViews();
        container_apply.removeAllViews();

        // 產生功能列表
        for (final TripType workType : TripType.values()) {
            if (hiddenTripType.contains(workType))
            {
                continue;
            }

            View item = generateItem(container_work, workType.getIcon(), getString(workType.getTitle()));
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;
                    switch (workType) {
                        case VISIT://活動
                            intent = new Intent(CreateActivity.this, VisitCreateActivity.class);
                            break;
                        case OFFICE://內勤
                            intent = new Intent(CreateActivity.this, OfficeCreateActivity.class);
                            break;
                        case TASK://任務
                            intent = new Intent(CreateActivity.this, TaskCreateActivity.class);
                            break;
                        case ABSENT://請假
                            intent = new Intent(CreateActivity.this, AbsentCreateActivity.class);
                            break;
                        case QUOTATION://報價
                            intent = new Intent(CreateActivity.this, QuotationCreateActivity.class);
                            break;
                        case CONTRACT://合同
                            intent = new Intent(CreateActivity.this, ContractCreateActivity.class);
                            break;
                        case REIMBURSEMENT://報銷單
                            intent = new Intent(CreateActivity.this, ReimbursementCreateActivity.class);
                            break;
                        case SAMPLE://送樣
                            intent = new Intent(CreateActivity.this, SampleCreateActivity.class);
                            break;
                        case SPECIAL_PRICE://特價
                            intent = new Intent(CreateActivity.this, SpecialPriceCreateActivity.class);
                            break;
                        case PRODUCTION://新項目量產
                            intent = new Intent(CreateActivity.this, ProductionCreateActivity.class);
                            break;
                        case NON_STANDARD_INQUIRY://非標詢價
                            intent = new Intent(CreateActivity.this, NonStandardInquiryCreateActivity.class);
                            break;
                        case SPRING_RING_INQUIRY://彈簧蓄能圈
                            intent = new Intent(CreateActivity.this, SpringRingInquiryCreateActivity.class);
                            break;
                        case EXPRESS://快遞
                            intent = new Intent(CreateActivity.this, ExpressCreateActivity.class);
                            break;
                        case TRAVEL://出差
                            intent = new Intent(CreateActivity.this, TravelCreateActivity.class);
                            break;
                    }

                    if (intent != null) {
                        String customerId = getIntent().getStringExtra(MyApplication.INTENT_KEY_CUSTOMER);
                        String projectId = getIntent().getStringExtra(MyApplication.INTENT_KEY_PROJECT);
                        String date = getIntent().getStringExtra(MyApplication.INTENT_KEY_DATE);

                        if (!TextUtils.isEmpty(customerId))
                            intent.putExtra(MyApplication.INTENT_KEY_CUSTOMER, customerId);
                        if (!TextUtils.isEmpty(projectId))
                            intent.putExtra(MyApplication.INTENT_KEY_PROJECT, projectId);
                        if (!TextUtils.isEmpty(date))
                            intent.putExtra(MyApplication.INTENT_KEY_DATE, date);

                        startActivity(intent);
                    }
                }
            });

            switch (workType) {
                case VISIT://活動
                case OFFICE://內勤
                case TASK://任務
                case ABSENT://請假
                    container_work.addView(item);
                    break;

                case QUOTATION://報價
                case CONTRACT://合同
                case REIMBURSEMENT://報銷單
                case SAMPLE://送樣
                case SPECIAL_PRICE://特價
                case PRODUCTION://新項目量產
                case NON_STANDARD_INQUIRY://非標詢價
                case SPRING_RING_INQUIRY://彈簧蓄能圈
                case EXPRESS://快遞
                case SPECIAL_SHIP://特批發貨
                case REPAYMENT://回款訊息
                case TRAVEL://出差
                    container_apply.addView(item);
                    break;
            }
        }
    }

    /**
         * 產生列表項目畫面
         */
    private View generateItem(ViewGroup container, int imgResId, String title) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.item_list, container, false);
        TextView tvTitle = view.findViewById(R.id.textView_title);
        ImageView ivIcon = view.findViewById(R.id.imageView_icon);

        tvTitle.setText(title);
        ivIcon.setImageResource(imgResId);
        return view;
    }
}
