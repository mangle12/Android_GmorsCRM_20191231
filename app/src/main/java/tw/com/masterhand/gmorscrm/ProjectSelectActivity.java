package tw.com.masterhand.gmorscrm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import org.reactivestreams.Subscription;

import butterknife.BindView;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.activity.project.ProjectCreateActivity;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.Project;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.ProjectSelectCard;

public class ProjectSelectActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.container)
    LinearLayout container;

    String customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_select);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        DatabaseHelper.getInstance(this).getAuthorityProjectByCustomer(TokenManager.getInstance().getUser().getId(),customerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FlowableSubscriber<Project>() {

                    @Override
                    public void onSubscribe(@NonNull Subscription s) {
                        s.request(Integer.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Project project) {
                        container.addView(generateItem(project), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    }

                    @Override
                    public void onError(Throwable t) {
                        ErrorHandler.getInstance().setException(ProjectSelectActivity.this, t);
                    }

                    @Override
                    public void onComplete() {
                        if (container.getChildCount() == 0) {
                            //無工作項目
                            container.addView(getEmptyImageView(null));
                            showNoProjectDialog();
                        }
                    }
                });
    }

    private void init() {
        appbar.setTitle(getString(R.string.select_project));
        customerId = getIntent().getStringExtra(MyApplication.INTENT_KEY_CUSTOMER);
    }

    private void showNoProjectDialog() {
        mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(customerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Customer>() {

            @Override
            public void accept(final Customer customer) throws Exception {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProjectSelectActivity.this, MyApplication.DIALOG_STYLE);
                builder.setTitle(R.string.title_activity_project_create).setMessage(R.string.msg_add_project).setPositiveButton(R.string.confirm, new
                        DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(ProjectSelectActivity.this, ProjectCreateActivity.class);
                                intent.putExtra(MyApplication.INTENT_KEY_CUSTOMER, customer.getId());
                                startActivityForResult(intent, MyApplication.REQUEST_ADD_PROJECT);
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onBackPressed();
                    }
                });
                builder.create().show();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(ProjectSelectActivity.this,throwable);
            }
        }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MyApplication.REQUEST_ADD_PROJECT:
                    String project = data.getStringExtra(MyApplication.INTENT_KEY_PROJECT);
                    Intent intent = new Intent();
                    intent.putExtra(MyApplication.INTENT_KEY_PROJECT, project);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private View generateItem(final Project project) {
        ProjectSelectCard card = new ProjectSelectCard(this);
        card.setProject(project);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(MyApplication.INTENT_KEY_PROJECT, project.getId());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        return card;
    }
}
