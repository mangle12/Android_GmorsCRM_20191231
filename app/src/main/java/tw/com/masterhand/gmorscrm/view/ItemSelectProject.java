package tw.com.masterhand.gmorscrm.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.ProjectSelectActivity;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.activity.task.ContractDetailActivity;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;

import static android.app.Activity.RESULT_OK;

public class ItemSelectProject extends RelativeLayout implements ItemSelectCustomer.CustomerSelectListener {
    @BindView(R.id.btnAdd)
    Button btnAdd;

    ItemSelectCustomer itemSelectCustomer;
    Activity context;
    ProjectWithConfig project;
    ItemProject itemProject;

    public ItemSelectProject(Context context) {
        super(context);
        init(context);
    }

    public ItemSelectProject(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemSelectProject(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_select_project, this);
        ButterKnife.bind(this, view);
        if (isInEditMode())
            return;
        context = (Activity) mContext;
    }

    public void setItemSelectCustomer(ItemSelectCustomer customer) {
        itemSelectCustomer = customer;
        itemSelectCustomer.addCustomerSelectedListener(this);
    }

    @OnClick(R.id.btnAdd)
    void selectProject() {
        if (itemSelectCustomer.getCustomer() == null) {
            Toast.makeText(context, R.string.error_msg_no_customer, Toast.LENGTH_LONG).show();//尚未選擇客戶
            return;
        }

        Intent intent = new Intent(context, ProjectSelectActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_CUSTOMER, itemSelectCustomer.getCustomer().getId());
        context.startActivityForResult(intent, MyApplication.REQUEST_SELECT_PROJECT);
    }

    public void reset() {
        if (itemProject != null) {
            removeView(itemProject);
            itemProject = null;
        }
        btnAdd.setVisibility(VISIBLE);
    }

    public void setProject(ProjectWithConfig project) {
        this.project = project;
        btnAdd.setVisibility(GONE);
        itemProject = new ItemProject(context);
        itemProject.setProject(project);
        itemProject.hideHistory();
        itemProject.hideIndex();
        itemProject.disableOpportunity();
        itemProject.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProject();
            }
        });
        addView(itemProject, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    public ProjectWithConfig getProject() {
        return project;
    }

    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // 選擇工作項目
                case MyApplication.REQUEST_SELECT_PROJECT:
                    String projectId = data.getStringExtra(MyApplication.INTENT_KEY_PROJECT);
                    if (!TextUtils.isEmpty(projectId)) {
                        DatabaseHelper.getInstance(context).getProjectById(projectId, TokenManager.getInstance().getUser().getDepartment_id(),
                                TokenManager.getInstance().getUser().getCompany_id())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<ProjectWithConfig>() {

                            @Override
                            public void accept(ProjectWithConfig project) throws
                                    Exception {
                                setProject(project);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                ErrorHandler.getInstance().setException(context,throwable);
                            }
                        });
                    }
                    break;
                default:

            }
        }
    }

    @Override
    public void onCustomerSelected(Customer customer) {
        reset();
    }
}
