package tw.com.masterhand.gmorscrm.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;
import tw.com.masterhand.gmorscrm.view.CustomerCard;
import tw.com.masterhand.gmorscrm.view.CustomerSelectCard;

public class CustomerSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final int TYPE_EMPTY = 999;
    private final Context mContext;
    private List<Customer> customers;
    private boolean isEmpty = false;

    public CustomerSelectListener listener;

    public interface CustomerSelectListener {
        void onCustomerSelected(Customer customer);
    }

    public void setListener(CustomerSelectListener listener) {
        this.listener = listener;
    }

    public CustomerSelectAdapter(Context context) {
        mContext = context;
        this.customers = new ArrayList<>();
    }

    public CustomerSelectAdapter(Context context, List<Customer> customers) {
        mContext = context;
        this.customers = customers;
    }

    public void showEmpty() {
        clear();
        isEmpty = true;
        customers.add(null);
        notifyItemInserted(0);
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        notifyDataSetChanged();
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
        notifyDataSetChanged();
    }

    public void clear() {
        isEmpty = false;
        int count = customers.size();
        customers = new ArrayList<>();
        notifyItemRangeRemoved(0, count);
    }

    @Override
    public int getItemViewType(int position) {
        if (isEmpty && position == 0) {
            return TYPE_EMPTY;
        } else {
            return 0;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_EMPTY) {
            TextView tvEmpty = new TextView(mContext);
            tvEmpty.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams
                    .MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            tvEmpty.setText(mContext.getString(R.string.error_msg_empty));
            tvEmpty.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.common_nodata, 0,
                    0);
            tvEmpty.setGravity(Gravity.CENTER);
            tvEmpty.setCompoundDrawablePadding(UnitChanger.dpToPx(8));
            tvEmpty.setPadding(0, UnitChanger.dpToPx(50), 0, UnitChanger.dpToPx(50));
            return new EmptyViewHolder(tvEmpty);
        }
        return new CustomerCardViewHolder(new CustomerSelectCard(mContext));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) != TYPE_EMPTY) {
            ((CustomerCardViewHolder) holder).card.setCustomer(customers.get(position));
            ((CustomerCardViewHolder) holder).card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onCustomerSelected(customers.get(position));
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return customers == null ? 0 : customers.size();
    }

    class CustomerCardViewHolder extends RecyclerView.ViewHolder {

        CustomerSelectCard card;

        CustomerCardViewHolder(CustomerSelectCard card) {
            super(card);
            this.card = card;
        }
    }

    class EmptyViewHolder extends RecyclerView.ViewHolder {

        TextView tvEmpty;

        EmptyViewHolder(TextView tvEmpty) {
            super(tvEmpty);
            this.tvEmpty = tvEmpty;
        }
    }
}
