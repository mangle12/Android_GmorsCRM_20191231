package tw.com.masterhand.gmorscrm.activity.setting;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.OnClick;
import tw.com.masterhand.gmorscrm.BaseActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.model.GuideData;
import tw.com.masterhand.gmorscrm.model.GuidePage;

public class GuideActivity extends BaseActivity {
    @BindView(R.id.btnPrevious)
    Button btnPrevious;
    @BindView(R.id.btnNext)
    Button btnNext;
    @BindView(R.id.ivContent)
    ImageView ivContent;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvMsg)
    TextView tvMsg;
    @BindView(R.id.progress)
    View progress;

    GuideData data;
    int stepWidth = 0;
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        init();
    }

    private void init() {
        String dataString = getIntent().getStringExtra(MyApplication.INTENT_KEY_GUIDE);
        if (TextUtils.isEmpty(dataString)) {
            finish();
            return;
        }
        data = new Gson().fromJson(dataString, GuideData.class);
        WindowManager windowManager = ((WindowManager) getSystemService(Context.WINDOW_SERVICE));
        if (windowManager != null) {
            Point size = new Point();
            windowManager.getDefaultDisplay().getSize(size);
            stepWidth = size.x / data.getPageList().size();
        }
        updateContent();
    }

    private void updateContent() {
        progress.getLayoutParams().width = stepWidth * (index + 1);
        progress.invalidate();
        GuidePage page = data.getPageList().get(index);
        ivContent.setImageResource(page.getImgResId());
        tvTitle.setText(page.getTitle());
        tvMsg.setText(page.getMsg());
    }

    @OnClick(R.id.btnClose)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.btnPrevious)
    void previous() {
        if (index == 0)
            return;
        index--;
        btnNext.setText(R.string.next);
        if (index == 0)
            btnPrevious.setVisibility(View.GONE);
        updateContent();
    }

    @OnClick(R.id.btnNext)
    void next() {
        if (index == data.getPageList().size() - 1) {
            //完成
            finish();
        } else {
            index++;
            btnPrevious.setVisibility(View.VISIBLE);
            if (index == data.getPageList().size() - 1)
                btnNext.setText(R.string.complete);
            updateContent();
        }
    }


}
