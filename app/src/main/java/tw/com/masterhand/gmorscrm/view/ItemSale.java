package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.activity.project.ProjectActivity;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.tools.Base64Utils;
import tw.com.masterhand.gmorscrm.tools.ImageTools;
import tw.com.masterhand.gmorscrm.tools.NumberFormater;

public class ItemSale extends RelativeLayout {
    @BindView(R.id.tvPercent)
    TextView tvPercent;
    @BindView(R.id.tvAmount)
    TextView tvAmount;
    @BindView(R.id.textView_full_name)
    TextView tvFullName;
    @BindView(R.id.textView_name)
    TextView tvName;
    @BindView(R.id.imageView_important)
    ImageView ivImportant;
    @BindView(R.id.imageView_icon)
    ImageView ivIcon;
    @BindView(R.id.rootView)
    RelativeLayout rootView;

    ProjectWithConfig project;
    Context context;

    public ItemSale(Context context) {
        super(context);
        init(context);
    }

    public ItemSale(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemSale(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_sale, this);
        ButterKnife.bind(this, view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rootView.setClipToOutline(true);
        }
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProjectActivity.class);
                intent.putExtra(MyApplication.INTENT_KEY_PROJECT, project.getProject().getId());
                context.startActivity(intent);
            }
        });
    }

    public void setProject(ProjectWithConfig project) {
        this.project = project;
        if (project.getCustomer() != null) {
            if (project.getCustomer().isType()) {
                ivImportant.setVisibility(VISIBLE);
            } else {
                ivImportant.setVisibility(INVISIBLE);
            }
            tvName.setText(project.getCustomer().getName());
            if (!TextUtils.isEmpty(project.getCustomer().getLogo())) {
                Bitmap bitmap = Base64Utils.decodeToBitmapFromString(project.getCustomer().getLogo());
                ivIcon.setImageDrawable(ImageTools.getCircleDrawable(getResources(), bitmap));
            }
        }

        if (project.getSalesOpportunity().getPercentage() < 0)
            tvPercent.setText("0%");
        else
            tvPercent.setText(String.valueOf(project.getSalesOpportunity().getPercentage()) + "%");

        tvFullName.setText(project.getProject().getName());
        if (project.getConfigCurrency() != null)
            tvAmount.setText(project.getConfigCurrency().getName() + " " + NumberFormater.getMoneyFormat(project.getProject().getExpect_amount()) + context.getString(R.string.money_unit));
        else
            tvAmount.setText(NumberFormater.getMoneyFormat(project.getProject().getExpect_amount()) + context.getString(R.string.money_unit));
    }
}
