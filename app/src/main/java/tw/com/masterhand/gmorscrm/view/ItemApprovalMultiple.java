package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;
import tw.com.masterhand.gmorscrm.model.Approval;

public class ItemApprovalMultiple extends ItemApprovalTab {
    OnApprovalSelectListener listener;

    public ItemApprovalMultiple(Context context) {
        super(context);
    }

    public ItemApprovalMultiple(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemApprovalMultiple(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void setStatus(ApprovalStatus status, Approval approval) {
        tvStatus.setText(R.string.not_selected);
        tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.common_small_pick, 0, 0, 0);
    }

    @Override
    public void onClick(View view) {
        setSelected(!isSelected());
        if (isSelected()) {
            tvStatus.setText(R.string.selected);
            tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.common_small_vpicked, 0, 0, 0);
        } else {
            tvStatus.setText(R.string.not_selected);
            tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.common_small_pick, 0, 0, 0);
        }
        if (listener != null) {
            listener.onApprovalSelect(approval, isSelected());
        }
    }

    public void setListener(OnApprovalSelectListener mListener) {
        listener = mListener;
    }

    public interface OnApprovalSelectListener {
        void onApprovalSelect(Approval approval, boolean isSelected);
    }
}
