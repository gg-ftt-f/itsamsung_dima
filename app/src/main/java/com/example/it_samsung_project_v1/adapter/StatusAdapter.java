package com.example.it_samsung_project_v1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.it_samsung_project_v1.R;
import com.example.it_samsung_project_v1.models.Book;
import com.example.it_samsung_project_v1.models.Status;

import java.util.ArrayList;
import java.util.List;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.Holder> {

    private final List<Status> statuses = new ArrayList<>();
    private final StatusAdapter.Callback cb;
    public interface Callback {

        void onItemLongClick(Status status, View anchor);
    }
    public StatusAdapter(StatusAdapter.Callback cb) {
        this.cb = cb;
    }
    public void setItems(List<Status> list) {
        statuses.clear();
        statuses.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }


    @Override
    public void onBindViewHolder(@NonNull StatusAdapter.Holder h, int position) {
        Status status = statuses.get(position);
        h.tvTitle.setText(status.caption);

        h.itemView.setOnLongClickListener(v -> {
            cb.onItemLongClick(status, v);
            return true;
        });
    }
    @NonNull
    @Override
    public StatusAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resourse, parent, false);
        return new StatusAdapter.Holder(v);
    }

    static class Holder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle;

        Holder(View itemView) {
            super(itemView);

            tvTitle  = itemView.findViewById(R.id.tv_title);

        }
    }
}
