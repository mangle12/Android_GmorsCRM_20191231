package tw.com.masterhand.gmorscrm.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.model.SampleList;
import tw.com.masterhand.gmorscrm.model.SampleWithConfig;
import tw.com.masterhand.gmorscrm.tools.Base64Utils;
import tw.com.masterhand.gmorscrm.tools.ImageTools;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;


public class SampleListAdapter extends RecyclerView.Adapter<SampleListAdapter.ViewHolder> {

    private Context mContext;
    private List<SampleList> data;
    private OnItemClickListener listener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.imageView_sample)
        ImageView ivSample;
        @BindView(R.id.ivIcon)
        ImageView ivIcon;
        @BindView(R.id.tvDate)
        TextView tvDate;
        @BindView(R.id.tvCustomer)
        TextView tvCustomer;
        @BindView(R.id.tvProject)
        TextView tvProject;
        @BindView(R.id.tvTitle)
        TextView tvTitle;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null)
                listener.onItemClick(data.get(getAdapterPosition()));
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(SampleList sample);
    }

    public SampleListAdapter(Context context) {
        mContext = context;
        data = new ArrayList<>();
    }

    public void clear() {
        data = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addData(SampleList data) {
        this.data.add(data);
        notifyDataSetChanged();
    }

    public void removeData(SampleList data) {
        this.data.remove(data);
        notifyDataSetChanged();
    }

    @Override
    public SampleListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_sample, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SampleList sample = data.get(position);
        if (sample.getDefault_photo() != null && !TextUtils.isEmpty(sample.getDefault_photo()
                .getFile_url()))
            ImageLoader.getInstance().displayImage(sample.getDefault_photo().getFile_url(), holder
                    .ivSample);
        holder.tvDate.setText(TimeFormater.getInstance().toDateFormat(sample.getUpdated_at()));
        if (sample.getTrip().getCustomer() != null) {
            holder.tvCustomer.setText(sample.getTrip().getCustomer().getName());
            if (!TextUtils.isEmpty(sample.getTrip().getCustomer().getLogo())) {
                Bitmap bitmap = Base64Utils.decodeToBitmapFromString(sample.getTrip().getCustomer
                        ().getLogo());
                holder.ivIcon.setImageDrawable(ImageTools
                        .getCircleDrawable(mContext.getResources(), bitmap));
            } else {
                holder.ivIcon.setImageResource(R.mipmap.common_small_customer_logo);
            }
        }
        if (sample.getTrip().getProject() != null)
            holder.tvProject.setText(sample.getTrip().getProject().getName());
        holder.tvTitle.setText(sample.getTrip().getName());
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }
}
