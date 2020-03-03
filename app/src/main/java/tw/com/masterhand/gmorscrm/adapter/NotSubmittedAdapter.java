package tw.com.masterhand.gmorscrm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.enums.SubmitStatus;
import tw.com.masterhand.gmorscrm.model.MainTrip;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.view.ItemMain;
import tw.com.masterhand.gmorscrm.view.ItemMainInvite;

public class NotSubmittedAdapter extends RecyclerView.Adapter<NotSubmittedAdapter.ViewHolder> {
    private final Context mContext;
    private List<MainTrip> trips;

    public NotSubmittedAdapter(Context context) {
        mContext = context;
        this.trips = new ArrayList<>();
    }

    public NotSubmittedAdapter(Context context, List<MainTrip> trips) {
        mContext = context;
        this.trips = trips;
    }

    public void setTrip(List<MainTrip> trips) {
        this.trips = trips;
        notifyDataSetChanged();
    }

    public List<MainTrip> getTrip() {
        return trips;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new ItemMain(mContext));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MainTrip mainTrip = trips.get(position);
        holder.item.setTrip(mainTrip);
        holder.item.setSelectMode(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemMain item = (ItemMain) v;
                if (mainTrip.getTrip().getSubmit() == SubmitStatus.NONE) {
                    mainTrip.getTrip().setSubmit(SubmitStatus.SUBMITTED);
                    item.selected();
                } else {
                    mainTrip.getTrip().setSubmit(SubmitStatus.NONE);
                    item.unselected();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return trips == null ? 0 : trips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemMain item;//畫面

        ViewHolder(ItemMain item) {
            super(item);
            this.item = item;
        }
    }
}
