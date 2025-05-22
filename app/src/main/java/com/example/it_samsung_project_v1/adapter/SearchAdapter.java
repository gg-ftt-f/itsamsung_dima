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
import com.example.it_samsung_project_v1.repo.BookRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchAdapter
        extends RecyclerView.Adapter<SearchAdapter.Holder> {

    public interface Callback {
        /**
         * @param doc       — объект из API
         * @param savedBook — уже сохранённая в БД книга (или null)
         * @param idKey     — единый ключ (ISBN/OLID/etc) для этой книги
         * @param anchor    — view для PopupMenu
         */
        void onItemLongClick(SearchDoc doc, Book savedBook, String idKey, View anchor, String key);
    }

    private final List<SearchDoc> docs = new ArrayList<>();
    private final List<Status> statuses = new ArrayList<>();
    private final Map<String, Book> savedMap = new HashMap<>();
    private final List<String> savedGenres = new ArrayList<>();
    private final Callback cb;

    public SearchAdapter(Callback cb) {
        this.cb = cb;
    }

    /** Обновляем мапу уже сохранённых книг по idKey */
    public void setSavedBooks(List<Book> savedBooks) {
        savedMap.clear();
        for (Book b : savedBooks) {
            if (b.idKey != null) {
                savedMap.put(b.idKey, b);
            }
        }
        BookRepository.cashedBook.clear();
        BookRepository.cashedBook.addAll(savedBooks);
        notifyDataSetChanged();
    }
    public void setSavedGenres(List<String> _savedGenres) {
        savedGenres.clear();
        for (String b : _savedGenres) {

            savedGenres.add(b);

        }
        notifyDataSetChanged();
    }

    public void setStatuses(List<Status> list) {
        statuses.clear();
        statuses.addAll(list);
    }
    /** Обновляем список результатов поиска */
    public void setItems(List<SearchDoc> list) {
        docs.clear();
        docs.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return docs.size();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_book, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        SearchDoc doc = docs.get(position);

        // 1) Title и author
        h.tvTitle.setText(doc.title != null ? doc.title : "Без названия");
        String author = (doc.authors != null && !doc.authors.isEmpty())
                ? doc.authors.get(0)
                : "Автор неизвестен";
        h.tvAuthor.setText(author);

        // 2) Логируем исходные поля
        Log.d("SearchAdapter", "DOC cover_i=" + doc.coverId
                + " isbns=" + doc.isbns
                + " editionKeys=" + doc.editionKeys
                + " workKey=" + doc.workKey);

        // 3) Строим единый ключ idKey
        String idKey;
        if (doc.isbns != null && !doc.isbns.isEmpty()) {
            idKey = doc.isbns.get(0);
        } else if (doc.editionKeys != null && !doc.editionKeys.isEmpty()) {
            idKey = doc.editionKeys.get(0);
        } else if (doc.workKey != null) {
            idKey = doc.workKey.replace("/works/", "");
        } else {
            idKey = null;
        }

        String key = doc.workKey;

        Log.d("SearchAdapter", "Computed idKey=" + idKey);

        // 4) Строим URL обложки
        String url = null;
        if (doc.coverId != null) {
            url = "https://covers.openlibrary.org/b/id/" + doc.coverId + "-M.jpg";
        }
        if (url == null && doc.isbns != null && !doc.isbns.isEmpty()) {
            url = "https://covers.openlibrary.org/b/isbn/" + doc.isbns.get(0) + "-M.jpg";
        }
        if (url == null && doc.editionKeys != null && !doc.editionKeys.isEmpty()) {
            url = "https://covers.openlibrary.org/b/olid/" + doc.editionKeys.get(0) + "-M.jpg";
        }
        Log.d("SearchAdapter", "Cover URL → " + url);

        // 5) Загружаем обложку
        if (url != null) {
            Glide.with(h.ivCover.getContext())
                    .load(url)
                    .placeholder(R.drawable.ic_book_placeholder)
                    .into(h.ivCover);
        } else {
            h.ivCover.setImageResource(R.drawable.ic_book_placeholder);
        }

        // 6) Проверяем, сохранена ли уже эта книга
        Book saved = (idKey != null) ? savedMap.get(idKey) : null;

        h.tvStatus.setText(Book.StatusToText(saved, statuses));

        // 7) LongClick: отдадим doc, savedBook и idKey
        h.itemView.setOnLongClickListener(v -> {
            cb.onItemLongClick(doc, saved, idKey, v, key);
            return true;
        });
    }

    static class Holder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView  tvTitle, tvAuthor, tvStatus;

        Holder(View itemView) {
            super(itemView);
            ivCover  = itemView.findViewById(R.id.iv_cover);
            tvTitle  = itemView.findViewById(R.id.tv_title);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }
}
