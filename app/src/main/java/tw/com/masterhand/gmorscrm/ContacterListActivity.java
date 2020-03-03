package tw.com.masterhand.gmorscrm;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.LinearLayout;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.ContactPerson;
import tw.com.masterhand.gmorscrm.model.ContacterWithPerson;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.ItemContacterList;

public class ContacterListActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.container)
    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacter_list);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
    }

    private void init() {
        appbar.setTitle(getString(R.string.contact));
        String parentId = getIntent().getStringExtra(MyApplication.INTENT_KEY_PARENT);
        if (TextUtils.isEmpty(parentId))
            finish();
        mDisposable.add(DatabaseHelper.getInstance(this).getContacterByParent(parentId).observeOn
                (AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ContacterWithPerson>() {
                    @Override
                    public void accept(ContacterWithPerson contacterWithPerson) throws Exception {
                        container.addView(generateItem(contacterWithPerson.getContactPerson()));
                    }
                }));
    }

    private ItemContacterList generateItem(final ContactPerson contactPerson) {
        ItemContacterList item = new ItemContacterList(this);
        item.setContactPerson(contactPerson);
        return item;
    }
}
