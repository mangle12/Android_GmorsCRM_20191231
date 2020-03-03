package tw.com.masterhand.gmorscrm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.model.MainTrip;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.view.CustomerCard;
import tw.com.masterhand.gmorscrm.view.ItemMainInvite;

public class InviteTripAdapter extends RecyclerView.Adapter<InviteTripAdapter
        .ViewHolder> {
    private final Context mContext;
    private List<MainTrip> trips;

    public InviteTripAdapter(Context context) {
        mContext = context;
        this.trips = new ArrayList<>();
    }

    public InviteTripAdapter(Context context, List<MainTrip> trips) {
        mContext = context;
        this.trips = trips;
    }

    public void setTrip(List<MainTrip> trips) {
        this.trips = trips;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new ItemMainInvite(mContext));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MainTrip mainTrip = trips.get(position);
        for (Participant participant : mainTrip.getParticipants()) {
            if (participant.getUser_id().equals(TokenManager.getInstance().getUser().getId())) {
                holder.item.setTrip(mainTrip, participant);
                holder.item.showDate(mainTrip.getTrip().getFrom_date());
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return trips == null ? 0 : trips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemMainInvite item;

        ViewHolder(ItemMainInvite item) {
            super(item);
            this.item = item;
        }
    }
}
