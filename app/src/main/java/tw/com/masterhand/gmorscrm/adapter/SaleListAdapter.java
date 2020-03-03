package tw.com.masterhand.gmorscrm.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.view.ItemSale;

public class SaleListAdapter extends RecyclerView.Adapter<SaleListAdapter
        .ItemViewHolder> {
    private final Context mContext;
    private List<ProjectWithConfig> projects;

    public SaleListAdapter(Context context) {
        mContext = context;
        this.projects = new ArrayList<>();
    }

    public SaleListAdapter(Context context, List<ProjectWithConfig> projects) {
        mContext = context;
        this.projects = projects;
    }

    public void addProject(ProjectWithConfig project) {
        projects.add(project);
        notifyDataSetChanged();
    }

    public void setProjects(List<ProjectWithConfig> projects) {
        this.projects = projects;
        notifyDataSetChanged();
    }

    public void clear() {
        int count = projects.size();
        projects = new ArrayList<>();
        notifyItemRangeRemoved(0, count);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(new ItemSale(mContext));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.item.setProject(projects.get(position));
    }

    @Override
    public int getItemCount() {
        return projects == null ? 0 : projects.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ItemSale item;

        ItemViewHolder(ItemSale card) {
            super(card);
            this.item = card;
        }
    }
}
