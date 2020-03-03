package tw.com.masterhand.gmorscrm.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;
import tw.com.masterhand.gmorscrm.view.CustomerCard;

public class CustomerAreaListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private List<Customer> customers;

    enum ViewType {
        CUSTOMER(0),
        LABEL(1);

        int code;

        ViewType(int code) {
            this.code = code;
        }

    }

    public CustomerAreaListAdapter(Context context, List<Customer> customers) {
        mContext = context;
        this.customers = customers;
    }

    public void setCustomers(ArrayList<Customer> customers) {
        this.customers = customers;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Customer customer = customers.get(position);
        if (TextUtils.isEmpty(customer.getId())) {
            return ViewType.LABEL.code;
        } else {
            return ViewType.CUSTOMER.code;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ViewType.CUSTOMER.code) {
            CustomerCard card = new CustomerCard(mContext);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView
                    .LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            params.leftMargin = UnitChanger.dpToPx(8);
            params.rightMargin = UnitChanger.dpToPx(8);
            card.setLayoutParams(params);
            return new CustomerViewHolder(card);
        } else {
            TextView tvLabel = new TextView(mContext);
            tvLabel.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams
                    .MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            int padding = UnitChanger.dpToPx(8);
            tvLabel.setPaddingRelative(padding, padding, padding, padding);
            tvLabel.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray_light));
            return new LabelViewHolder(tvLabel);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == ViewType.CUSTOMER.code) {
            CustomerViewHolder viewHolder = (CustomerViewHolder) holder;
            viewHolder.card.setCustomer(customers.get(position));
        } else {
            LabelViewHolder viewHolder = (LabelViewHolder) holder;
            viewHolder.tvLabel.setText(customers.get(position).getAddress().getCountry().getName());
        }
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }


    public class CustomerViewHolder extends RecyclerView.ViewHolder {

        CustomerCard card;

        CustomerViewHolder(CustomerCard item) {
            super(item);
            this.card = item;
        }
    }

    public class LabelViewHolder extends RecyclerView.ViewHolder {

        TextView tvLabel;

        LabelViewHolder(TextView item) {
            super(item);
            this.tvLabel = item;
        }
    }
}
