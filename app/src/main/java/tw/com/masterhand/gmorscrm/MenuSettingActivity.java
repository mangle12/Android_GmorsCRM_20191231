package tw.com.masterhand.gmorscrm;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import butterknife.BindView;
import tw.com.masterhand.gmorscrm.enums.MainMenu;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.ItemMenuSetting;

public class MenuSettingActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.linearLayout_fixed_item)
    LinearLayout container_fixed;
    @BindView(R.id.linearLayout_added_item)
    LinearLayout container_added;
    @BindView(R.id.linearLayout_not_add_item)
    LinearLayout container_not_add;

    ArrayList<MainMenu> addList;// 已加入項目

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_setting);
        addList = preferenceHelper.getMenuSetting();
        connectLayout();
        updateList();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
    }

    private void connectLayout() {
        appbar.setTitle(getString(R.string.title_activity_menu_setting));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 點擊完成
                if (addList.size() > 0) {
                    preferenceHelper.saveMenuSetting(addList);
                }
                setResult(RESULT_OK);
                onBackPressed();
            }
        });
    }

    /**
     * 更新主選單項目列表
     */
    private void updateList() {
        container_fixed.removeAllViews();
        container_added.removeAllViews();
        container_not_add.removeAllViews();

        MainMenu[] menus = MainMenu.values();
        for (MainMenu menu : menus) {
            final ItemMenuSetting item = new ItemMenuSetting(this);
            item.setMenu(menu);
            if (menu.isFixed()) {
                container_fixed.addView(item);
            } else {
                if (addList.contains(item.getMenu())) {
                    item.setFunctionListener(true, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // 從已加入項目中移除
                            addList.remove(item.getMenu());
                            updateList();
                        }
                    });
                    container_added.addView(item);
                } else {
                    item.setFunctionListener(false, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // 加入項目
                            addList.add(item.getMenu());
                            updateList();
                        }
                    });
                    container_not_add.addView(item);
                }
            }
        }
    }
}
