package tw.com.masterhand.gmorscrm.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.enums.AcceptType;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;

public class ItemTripAccept extends RelativeLayout {
    @BindView(R.id.btnAcceptNo)
    Button btnAcceptNo;
    @BindView(R.id.btnAcceptMaybe)
    Button btnAcceptMaybe;
    @BindView(R.id.btnAcceptYes)
    Button btnAcceptYes;

    Activity context;
    Participant participant;
    OnSelectListener listener;

    public interface OnSelectListener {
        void onItemSelected(ItemTripAccept item, AcceptType acceptType);
    }

    public ItemTripAccept(Context context) {
        super(context);
        init(context);
    }

    public ItemTripAccept(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemTripAccept(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_trip_accept, this);
        ButterKnife.bind(this, view);
        if (isInEditMode())
            return;
        context = (Activity) mContext;
    }

    public void setParticipant(Participant people) {
        participant = people;
        switch (participant.getAccept()) {
            case YES:
                btnAcceptYes.setSelected(true);
                break;
            case MAYBE:
                btnAcceptMaybe.setSelected(true);
                break;
            case NO:
                btnAcceptNo.setSelected(true);
                break;
        }
    }

    //參加
    @OnClick(R.id.btnAcceptYes)
    void clickYes() {
        Logger.e(getClass().getSimpleName(), "clickYes");
        clearSelected();
        btnAcceptYes.setSelected(true);
        saveAccept(AcceptType.YES);
    }

    //可能
    @OnClick(R.id.btnAcceptMaybe)
    void clickMaybe() {
        Logger.e(getClass().getSimpleName(), "clickMaybe");
        clearSelected();
        btnAcceptMaybe.setSelected(true);
        saveAccept(AcceptType.MAYBE);
    }

    @OnClick(R.id.btnAcceptNo)
    void clickNo() {
        Logger.e(getClass().getSimpleName(), "clickNo");
        clearSelected();
        btnAcceptNo.setSelected(true);
        saveAccept(AcceptType.NO);
    }

    void saveAccept(final AcceptType acceptType) {
        participant.setAccept(acceptType);
        DatabaseHelper.getInstance(context).saveParticipant(participant)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (listener != null)
                        {
                            listener.onItemSelected(ItemTripAccept.this, acceptType);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(context, throwable);
                    }
                });
    }

    /**
     * 清除選擇狀態
     */
    void clearSelected() {
        btnAcceptYes.setSelected(false);
        btnAcceptMaybe.setSelected(false);
        btnAcceptNo.setSelected(false);
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        listener = onSelectListener;
    }
}
