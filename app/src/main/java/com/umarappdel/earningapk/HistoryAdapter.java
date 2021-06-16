package com.umarappdel.earningapk;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.umarappdel.earningapk.model.HistoryModel;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder> {

    private List<HistoryModel> list;

    public HistoryAdapter(List<HistoryModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_items,
                        parent,
                        false);

        return new HistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryHolder holder, int position) {

        holder.phoneTv.setText(list.get(position).getPhone());
        holder.statusTv.setText(list.get(position).getStatus());

        Glide.with(holder.itemView.getContext().getApplicationContext())
                .load(list.get(position).getImage())
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class HistoryHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView phoneTv, statusTv;

        public HistoryHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image);
            phoneTv = itemView.findViewById(R.id.phone);
            statusTv = itemView.findViewById(R.id.status);

        }
    }

}
