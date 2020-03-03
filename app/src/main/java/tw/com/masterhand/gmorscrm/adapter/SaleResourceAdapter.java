package tw.com.masterhand.gmorscrm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import tw.com.masterhand.gmorscrm.enums.DownloadStatus;
import tw.com.masterhand.gmorscrm.model.SaleResource;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.ItemSaleResource;


public class SaleResourceAdapter extends RecyclerView.Adapter<SaleResourceAdapter.ViewHolder> {

    private Context mContext;
    private List<SaleResource> data;

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemSaleResource item;

        ViewHolder(ItemSaleResource v) {
            super(v);
            item = v;
        }

    }

    public SaleResourceAdapter(Context context) {
        mContext = context;
        data = new ArrayList<>();
    }

    public void clear() {
        data = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addData(SaleResource data) {
        this.data.add(data);
        notifyDataSetChanged();
    }

    public void removeData(SaleResource data) {
        this.data.remove(data);
        notifyDataSetChanged();
    }

    public void complete(long completeId) {
        for (SaleResource resource : data) {
            if (resource.getDownloadId() == completeId) {
                Logger.e(getClass().getSimpleName(), "completeId:" + completeId);
                resource.setStatus(DownloadStatus.SUCCESSFUL);
                notifyItemChanged(data.indexOf(resource));
                break;
            }
        }
    }

    @Override
    public SaleResourceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        return new ViewHolder(new ItemSaleResource(mContext));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SaleResource resource = data.get(position);
        holder.item.setResource(resource);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }
}
