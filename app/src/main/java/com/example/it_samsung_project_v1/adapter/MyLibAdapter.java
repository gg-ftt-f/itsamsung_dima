package com.example.it_samsung_project_v1.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.it_samsung_project_v1.R;
import com.example.it_samsung_project_v1.api.dto.SearchDoc;
import com.example.it_samsung_project_v1.models.Book;
import com.example.it_samsung_project_v1.models.Status;

import java.util.ArrayList;
import java.util.List;

public class MyLibAdapter extends RecyclerView.Adapter<SearchAdapter.Holder> {
    private final List<Book> docs = new ArrayList<>();
    private final List<Status> statuses = new ArrayList<>();
    private final SearchAdapter.Callback cb;
    public MyLibAdapter(SearchAdapter.Callback cb) {
        this.cb = cb;
    }
    public void setItems(List<Book> list) {
        docs.clear();
        docs.addAll(list);
        notifyDataSetChanged();
    }

    public void setStatuses(List<Status> list) {
        statuses.clear();
        statuses.addAll(list);
    }

    @Override
    public int getItemCount() {
        return docs.size();
    }

    @NonNull
    @Override
    public SearchAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_book, parent, false);
        return new SearchAdapter.Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.Holder h, int position) {
        Book doc = docs.get(position);

        // 1) Title и author
        h.tvTitle.setText((doc.book_name != null ? doc.book_name : "Без названия") + "  " + doc.genre);
        String author = (doc.author != null )
                ? doc.author
                : "Автор неизвестен";
        h.tvAuthor.setText(author);

        h.tvStatus.setText(Book.StatusToText(doc, statuses));

        // 4) Строим URL обложки
        String url = null;
        if (doc.coverUrl != null) {
            url = doc.coverUrl;
        }


        // 5) Загружаем обложку
        if (url != null) {
            Glide.with(h.ivCover.getContext())
                    .load(url)
                    .placeholder(R.drawable.ic_book_placeholder)
                    .into(h.ivCover);
        } else {
            h.ivCover.setImageResource(R.drawable.ic_book_placeholder);
        }

        h.itemView.setOnLongClickListener(v -> {
            cb.onItemLongClick(null, doc, doc.idKey, v, doc.key);
            return true;
        });
    }
    static class Holder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle, tvAuthor, tvStatus ;

        Holder(View itemView) {
            super(itemView);
            ivCover  = itemView.findViewById(R.id.iv_cover);
            tvTitle  = itemView.findViewById(R.id.tv_title);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }
}
