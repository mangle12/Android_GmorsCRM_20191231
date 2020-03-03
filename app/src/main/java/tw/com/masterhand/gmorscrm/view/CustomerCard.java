package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.UserListActivity;
import tw.com.masterhand.gmorscrm.activity.customer.CustomerDetailActivity;
import tw.com.masterhand.gmorscrm.activity.customer.CustomerInfoActivity;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.SalesOpportunity;
import tw.com.masterhand.gmorscrm.tools.Base64Utils;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.ImageTools;
import tw.com.masterhand.gmorscrm.tools.NumberFormater;

public class CustomerCard extends RelativeLayout {
    @BindView(R.id.textView_name)
    TextView tvName;
    @BindView(R.id.textView_area)
    TextView tvArea;
    @BindView(R.id.textView_full_name)
    TextView tvFullName;
    @BindView(R.id.textView_count)
    TextView tvCount;
    @BindView(R.id.textView_more)
    TextView tvMore;
    @BindView(R.id.textView_contract_amount)
    TextView tvContractAmount;
    @BindView(R.id.textView_quotation_count)
    TextView tvQuotationCount;
    @BindView(R.id.textView_invoice_amount)
    TextView tvInvoiceAmount;
    @BindView(R.id.textView_sample_count)
    TextView tvSampleCount;

    @BindView(R.id.imageView_icon)
    ImageView ivIcon;
    @BindView(R.id.imageView_important)
    ImageView ivImportant;
    @BindView(R.id.linearLayout_progress)
    LinearLayout containerProgress;
    @BindView(R.id.container_worker)
    RelativeLayout containerWorker;
    @BindView(R.id.rootView)
    RelativeLayout rootView;

    Context context;
    Customer customer;
    int workerCount;
    int contractCount;
    int itemCount;
    int winCount;
    int loseCount;
    ArrayList<String> worker;

    final CompositeDisposable mDisposable = new CompositeDisposable();

    public CustomerCard(Context context) {
        super(context);
        init(context);
    }

    public CustomerCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomerCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        if (isInEditMode())
            return;
        context = mContext;
        worker = new ArrayList<>();

        // 連接畫面
        View view = inflate(getContext(), R.layout.card_customer, this);
        ButterKnife.bind(this, view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rootView.setClipToOutline(true);
        }
    }


    /**
         * 設定公司資料
         */
    public void setCustomer(final Customer customer) {
        winCount = 0;
        loseCount = 0;
        itemCount = 0;
        contractCount = 0;
        setProgress(contractCount, itemCount, winCount, loseCount);
        this.customer = customer;
        if (!TextUtils.isEmpty(customer.getName())) {
            tvName.setText(customer.getName());
        }

        if (customer.getAddress() != null) {
            tvArea.setText("(" + customer.getAddress().getArea() + ")");
        }

        if (!TextUtils.isEmpty(customer.getFull_name())) {
            tvFullName.setText(customer.getFull_name());
        }

        if (customer.isType())
        {
            ivImportant.setVisibility(VISIBLE);
        }
        else
        {
            ivImportant.setVisibility(INVISIBLE);
        }

        if (!TextUtils.isEmpty(customer.getLogo())) {
            Bitmap bitmap = Base64Utils.decodeToBitmapFromString(customer.getLogo());
            ivIcon.setImageDrawable(ImageTools.getCircleDrawable(getResources(), bitmap));
        }
        //更多
        tvMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CustomerInfoActivity.class);
                intent.putExtra(MyApplication.INTENT_KEY_CUSTOMER, customer.getId());
                context.startActivity(intent);
            }
        });

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CustomerDetailActivity.class);
                intent.putExtra(MyApplication.INTENT_KEY_CUSTOMER, customer.getId());
                context.startActivity(intent);
            }
        });

        mDisposable.add(DatabaseHelper.getInstance(context).getContactPersonByCustomer(customer.getId()).count()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {

                    @Override
                    public void accept(Long aLong) throws Exception {
                        setContactPersonCount(aLong.intValue());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(context, throwable);
                    }
                }));

        tvInvoiceAmount.setText(NumberFormater.getMoneyFormat(customer.getInvoice_amount()));
        tvSampleCount.setText(NumberFormater.getNumberFormat(customer.getSample_count()));
        tvQuotationCount.setText(NumberFormater.getNumberFormat(customer.getQuotation_count()));
        tvContractAmount.setText(NumberFormater.getMoneyFormat(customer.getContract_amount()));
        contractCount = customer.getContract_count();
        setProgress(contractCount, itemCount, winCount, loseCount);
        mDisposable.add(DatabaseHelper.getInstance(context).getProjectByCustomer(customer.getId(), TokenManager.getInstance()
                .getUser().getDepartment_id(), TokenManager.getInstance().getUser().getCompany_id())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<ProjectWithConfig>() {

            @Override
            public void accept(ProjectWithConfig project) throws Exception {
                SalesOpportunity opportunity = project.getSalesOpportunity();

                if (opportunity != null) {
                    if (opportunity.getPercentage() == 100)
                        winCount++;
                    if (opportunity.getPercentage() < 0)
                        loseCount++;
                }
            }
        }).count()
                .subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                itemCount = aLong.intValue();
                setProgress(contractCount, itemCount, winCount, loseCount);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        }));
    }

    public void clearDisposable() {
        mDisposable.clear();
    }

    public void setCardListener(OnClickListener listener) {
        this.setOnClickListener(listener);
    }

    /**
     * 設定項目、贏單、輸單
     */
    public void setProgress(int contract, int item, int win, int lose) {
        int defaultColor = ContextCompat.getColor(context, R.color.gray);
        int contractColor = ContextCompat.getColor(context, R.color.black);
        int itemColor = ContextCompat.getColor(context, R.color.orange);
        int winColor = ContextCompat.getColor(context, R.color.green);
        int loseColor = ContextCompat.getColor(context, R.color.red);
        View vContract = containerProgress.getChildAt(0);
        View vItem = containerProgress.getChildAt(1);
        View vWin = containerProgress.getChildAt(2);
        View vLose = containerProgress.getChildAt(3);
        TextView tvContract = vContract.findViewById(R.id.textView_title);
        TextView tvItem = vItem.findViewById(R.id.textView_title);
        TextView tvWin = vWin.findViewById(R.id.textView_title);
        TextView tvLose = vLose.findViewById(R.id.textView_title);
        tvContract.setText(context.getString(R.string.contract_count) + "\n" + contract);
        tvItem.setText(context.getString(R.string.item) + "\n" + item);
        tvWin.setText(context.getString(R.string.win) + "\n" + win);
        tvLose.setText(context.getString(R.string.lose) + "\n" + lose);

        if (contract > 0) {
            changeProgressColor(vContract, contractColor);
        } else {
            changeProgressColor(vContract, defaultColor);
        }

        if (item > 0) {
            changeProgressColor(vItem, itemColor);
        } else {
            changeProgressColor(vItem, defaultColor);
        }

        if (win > 0) {
            changeProgressColor(vWin, winColor);
        } else {
            changeProgressColor(vWin, defaultColor);
        }

        if (lose > 0) {
            changeProgressColor(vLose, loseColor);
        } else {
            changeProgressColor(vLose, defaultColor);
        }
    }

    private void changeProgressColor(View item, int color) {
        TextView tvItem = (TextView) item.findViewById(R.id.textView_title);
        item.findViewById(R.id.left_line).setBackgroundColor(color);
        item.findViewById(R.id.bottom_line).setBackgroundColor(color);
        ImageView ivIcon = (ImageView) item.findViewById(R.id.imageView_icon);
        ivIcon.setColorFilter(color);
        tvItem.setTextColor(color);
    }

    /**
     * 設定客戶聯絡人數量
     */
    public void setContactPersonCount(int count) {
        tvCount.setText(String.valueOf(count));
        if (count > 0) {
            tvCount.setTextColor(ContextCompat.getColor(context, R.color.white));
            tvCount.setBackgroundResource(R.drawable.oval_orange);
            containerWorker.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, UserListActivity.class);
                    intent.putExtra(MyApplication.INTENT_KEY_CUSTOMER, customer.getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
