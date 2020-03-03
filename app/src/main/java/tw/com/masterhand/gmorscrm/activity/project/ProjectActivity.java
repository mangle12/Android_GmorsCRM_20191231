package tw.com.masterhand.gmorscrm.activity.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.reactivestreams.Subscription;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.ContacterListActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.UserListActivity;
import tw.com.masterhand.gmorscrm.enums.ProjectStatus;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCompanyHiddenField;
import tw.com.masterhand.gmorscrm.room.setting.ConfigProjectSource;
import tw.com.masterhand.gmorscrm.room.setting.ConfigQuotationProductCategory;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSalesOpportunityType;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.NumberFormater;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;
import tw.com.masterhand.gmorscrm.view.Appbar;

public class ProjectActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.container_tab)
    LinearLayout containerTab;
    @BindView(R.id.btnParticipant)
    Button btnParticipant;
    @BindView(R.id.btnContacter)
    Button btnContacter;
    @BindView(R.id.container_photo)
    LinearLayout containerPhoto;
    @BindView(R.id.progress_black)
    View statusProgress;
    @BindView(R.id.textView_title)
    TextView tvTitle;
    @BindView(R.id.tvCategory)
    TextView tvCategory;
    @BindView(R.id.containerType)
    LinearLayout containerType;
    @BindView(R.id.tvType)
    TextView tvType;
    @BindView(R.id.containerSource)
    LinearLayout containerSource;
    @BindView(R.id.tvSource)
    TextView tvSource;
    @BindView(R.id.tvCheckTime)
    TextView tvCheckTime;
    @BindView(R.id.textView_amount)
    TextView tvAmount;
    @BindView(R.id.textView_user)
    TextView tvUser;
    @BindView(R.id.textView_note)
    TextView tvNote;
    @BindView(R.id.textView_time)
    TextView tvTime;
    @BindView(R.id.tvTab1)
    TextView tvTab1;
    @BindView(R.id.tvTab2)
    TextView tvTab2;
    @BindView(R.id.tvTab3)
    TextView tvTab3;
    @BindView(R.id.tvTab4)
    TextView tvTab4;

    ProjectWithConfig project;
    String projectID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        getHiddenField();
        if (!TextUtils.isEmpty(projectID))
            updateDetail(projectID);
    }

    private void init() {
        appbar.setTitle(getString(R.string.title_activity_project));
        appbar.addFunction(R.mipmap.common_edit, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ProjectCreateActivity.class);
                intent.putExtra(MyApplication.INTENT_KEY_PROJECT, project.getProject().getId());
                startActivityForResult(intent, MyApplication.REQUEST_EDIT);
            }
        });

        projectID = getIntent().getStringExtra(MyApplication.INTENT_KEY_PROJECT);
        if (TextUtils.isEmpty(projectID)) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MyApplication.REQUEST_EDIT:
                    if (project != null)
                        updateDetail(project.getProject().getId());
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void getHiddenField() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigCompanyHiddenField(TokenManager.getInstance().getUser().getCompany_id())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ConfigCompanyHiddenField>() {
            @Override
            public void accept(ConfigCompanyHiddenField configCompanyHiddenField) throws Exception {
                if (configCompanyHiddenField.getProject_hidden_sales_opportunity_type())
                    containerType.setVisibility(View.GONE);
                if (configCompanyHiddenField.getProject_hidden_department_project_source())
                    containerSource.setVisibility(View.GONE);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(ProjectActivity.this, throwable);
            }
        }));
    }

    void updateDetail(final String projectID) {
        Logger.e(TAG, "project id:" + projectID);
        mDisposable.add(DatabaseHelper.getInstance(this).getProjectById(projectID, TokenManager.getInstance().getUser().getDepartment_id(), TokenManager.getInstance().getUser().getCompany_id())
//                .getAuthorityProjectById(TokenManager
//                        .getInstance()
//                        .getUser().getId(), projectID, TokenManager.getInstance().getUser()
//                        .getDepartment_id(),
//                TokenManager.getInstance().getUser().getCompany_id())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ProjectWithConfig>() {
                    @Override
                    public void accept(ProjectWithConfig result) throws Exception {
                        project = result;
                        if (project.getDepartmentSalesOpportunity() != null) {
                            tvTab1.setText(project.getDepartmentSalesOpportunity().getStage_1_name());
                            tvTab2.setText(project.getDepartmentSalesOpportunity().getStage_2_name());
                            tvTab3.setText(project.getDepartmentSalesOpportunity().getStage_3_name());
                            tvTab4.setText(project.getDepartmentSalesOpportunity().getStage_4_name());
                        }

                        tvTitle.setText(project.getProject().getName());
                        if (result.getConfigCurrency() != null) {
                            tvAmount.setText(result.getConfigCurrency().getName() + " " + NumberFormater.getMoneyFormat(project.getProject().getExpect_amount()));
                        } else {
                            tvAmount.setText(NumberFormater.getMoneyFormat(project.getProject().getExpect_amount()));
                        }
                        if (project.getProject().getFrom_date() != null)
                            tvTime.setText(TimeFormater.getInstance().toDateFormat(project.getProject().getFrom_date()));
                        if (project.getProject().getCheck_date() != null)
                            tvCheckTime.setText(TimeFormater.getInstance().toDateFormat(project.getProject().getCheck_date()));
                        if (!TextUtils.isEmpty(project.getProject().getDescription())) {
                            tvNote.setText(project.getProject().getDescription());
                        }
                        if (!TextUtils.isEmpty(project.getProject().getUser_id())) {
                            // 取得建立人
                            mDisposable.add(DatabaseHelper.getInstance(ProjectActivity.this).getUserById(project.getProject().getUser_id())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<User>() {

                                        @Override
                                        public void accept(User user) throws Exception {
                                            tvUser.setText(user.getShowName());
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            ErrorHandler.getInstance().setException(ProjectActivity.this, throwable);
                                        }
                                    }));
                        }

                        //j專案進度
                        setProjectStatus(project.getSalesOpportunity().getStage());

                        if (project.getContacters() != null && project.getContacters().size() > 0)
                        {
                            btnContacter.setText(getString(R.string.contact) + " " + project.getContacters().size());
                        }

                        if (project.getParticipants() != null && project.getParticipants().size() > 0)
                        {
                            btnParticipant.setText(getString(R.string.participant) + " " + project.getParticipants().size());
                        }

                        updateFile();
                        getProductCategory();
                        getSalesSource();
                        getSalesType();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(ProjectActivity.this, throwable);
                        Toast.makeText(ProjectActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    }
                }));
    }

    void getProductCategory() {
        if (project.getProject().getProduct_category_id().size() > 0)
            mDisposable.add(DatabaseHelper.getInstance(this).getConfigQuotationProductCategory().toList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<ConfigQuotationProductCategory>>() {

                        @Override
                        public void accept(List<ConfigQuotationProductCategory>
                                                   configQuotationProductCategories) throws
                                Exception {
                            boolean first = true;
                            StringBuilder show = new StringBuilder();

                            for (ConfigQuotationProductCategory config : configQuotationProductCategories) {
                                if (project.getProject().getProduct_category_id().contains(config.getId())) {
                                    if (first) {
                                        show.append(config.getName());
                                        first = false;
                                    } else {
                                        show.append("\n").append(config.getName());
                                    }
                                }
                            }
                            tvCategory.setText(show.toString());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ErrorHandler.getInstance().setException(ProjectActivity.this, throwable);
                        }
                    }));
    }

    void getSalesType() {
        if (!TextUtils.isEmpty(project.getProject().getSales_opportunity_type())) {
            mDisposable.add(DatabaseHelper.getInstance(this).getConfigSalesOpportunityTypeById
                    (project.getProject().getSales_opportunity_type())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ConfigSalesOpportunityType>() {

                @Override
                public void accept(ConfigSalesOpportunityType configSalesOpportunityType) throws
                        Exception {
                    tvType.setText(configSalesOpportunityType.getName());
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    ErrorHandler.getInstance().setException(ProjectActivity.this, throwable);
                }
            }));
        }
    }

    void getSalesSource() {
        if (!TextUtils.isEmpty(project.getProject().getDepartment_project_source())) {
            mDisposable.add(DatabaseHelper.getInstance(this).getConfigProjectSourceById(project.getProject().getDepartment_project_source())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ConfigProjectSource>() {

                @Override
                public void accept(ConfigProjectSource configProjectSource) throws Exception {
                    tvSource.setText(configProjectSource.getName());
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    ErrorHandler.getInstance().setException(ProjectActivity.this, throwable);
                }
            }));
        }
    }

    @OnClick(R.id.btnHistory)
    void showHistory() {
        Intent intent = new Intent(this, ProjectHistoryActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_PROJECT, project.getProject().getId());
        startActivity(intent);
    }

    @OnClick(R.id.btnContacter)
    void showContacter() {
        if (project.getContacters() == null || project.getContacters().size() == 0)
            return;
        Intent intent = new Intent(this, ContacterListActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_PARENT, project.getProject().getId());
        startActivity(intent);
    }

    @OnClick(R.id.btnParticipant)
    void showParticipant() {
        if (project.getParticipants() == null || project.getParticipants().size() == 0)
            return;
        Intent intent = new Intent(this, UserListActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_PARENT, project.getProject().getId());
        startActivity(intent);
    }

    private void updateFile() {
        containerPhoto.removeAllViews();
        DatabaseHelper.getInstance(this).getFileByParent(project.getProject().getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FlowableSubscriber<File>() {

            @Override
            public void onSubscribe(@NonNull Subscription s) {
                s.request(Integer.MAX_VALUE);
            }

            @Override
            public void onNext(File file) {
                containerPhoto.addView(generatePhotoItem(file));
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                if (containerPhoto.getChildCount() == 0)
                    containerPhoto.addView(getEmptyTextView(null));
            }
        });
    }

    /**
     * 設定專案進度顯示
     */
    private void setProjectStatus(ProjectStatus status) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        int unitWidth = screenWidth / 5;
        int index = 0;
        String title = "";
        if (project.getDepartmentSalesOpportunity() != null) {
            switch (status) {
                case START:
                    return;
                case DESIGN:
                    title = project.getDepartmentSalesOpportunity().getStage_1_name();
                    index = 0;
                    break;
                case QUOTE:
                    title = project.getDepartmentSalesOpportunity().getStage_2_name();
                    index = 1;
                    break;
                case SAMPLE:
                    title = project.getDepartmentSalesOpportunity().getStage_3_name();
                    index = 2;
                    break;
                case NEGOTIATION:
                    title = project.getDepartmentSalesOpportunity().getStage_4_name();
                    index = 3;
                    break;
                case WIN:
                case LOSE:
                    title = getString(status.getTitle());
                    index = 4;
                    break;
            }
        }

        int progressWidth = unitWidth * (index + 1) + UnitChanger.dpToPx(30);
        RelativeLayout.LayoutParams progressParams = (RelativeLayout.LayoutParams) statusProgress.getLayoutParams();
        progressParams.width = progressWidth;
        statusProgress.setLayoutParams(progressParams);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        View selected = getLayoutInflater().inflate(R.layout.tab_project_status, containerTab, false);
        TextView tvTitle = selected.findViewById( R.id.textView_title);
        TextView tvPercent = selected.findViewById( R.id.textView_percent);
        tvTitle.setText(title);

        if (project.getSalesOpportunity().getPercentage() < 0)
            tvPercent.setText("0");
        else
            tvPercent.setText(String.valueOf(project.getSalesOpportunity().getPercentage()));

        selected.setLayoutParams(params);
        containerTab.removeViewAt(index);
        containerTab.addView(selected, index);
    }
}
