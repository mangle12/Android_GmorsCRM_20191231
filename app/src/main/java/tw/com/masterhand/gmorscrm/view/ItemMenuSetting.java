package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.enums.MainMenu;

public class ItemMenuSetting extends RelativeLayout {
    @BindView(R.id.imageView_icon)
    ImageView ivIcon;
    @BindView(R.id.imageButton_function)
    ImageButton btnFunc;
    @BindView(R.id.textView_title)
    TextView tvTitle;

    MainMenu mainMenu;
    Context context;

    public ItemMenuSetting(Context context) {
        super(context);
        init(context);
    }

    public ItemMenuSetting(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemMenuSetting(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_menu_setting, this);
        ButterKnife.bind(this, view);
    }

    public void setMenu(MainMenu menu) {
        mainMenu = menu;
        tvTitle.setText(context.getString(mainMenu.getTitle()));
        ivIcon.setImageResource(menu.getIcon());
        if (menu.isFixed())
            btnFunc.setVisibility(GONE);
    }

    public MainMenu getMenu() {
        return mainMenu;
    }

    public void setFunctionListener(boolean isAdded, OnClickListener listener) {
        if (isAdded) {
            btnFunc.setImageResource(R.mipmap.common_added_red);
        } else {
            btnFunc.setImageResource(R.mipmap.common_add_green);
        }
        btnFunc.setOnClickListener(listener);
    }
}
