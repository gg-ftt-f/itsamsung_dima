package com.example.it_samsung_project_v1.fragments;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.it_samsung_project_v1.R;
import com.example.it_samsung_project_v1.adapter.SearchAdapter;
import com.example.it_samsung_project_v1.api.dto.SearchDoc;
import com.example.it_samsung_project_v1.models.Book;
import com.example.it_samsung_project_v1.repo.BookRepository;
import com.example.it_samsung_project_v1.vm.BookViewModel;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {

    private BookViewModel vm;
    private EditText etSearch;
    private Button btnSearch;
    private RecyclerView rvResults;
    private SearchAdapter adapter;
    public boolean itsRecomendation;
    private String genres;
    private TextView tv_searchProcess;


    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             @Nullable ViewGroup ct,
                             @Nullable Bundle ss) {
        View v = inf.inflate(R.layout.fragment_library, ct, false);

        etSearch  = v.findViewById(R.id.et_search);
        btnSearch = v.findViewById(R.id.btn_search);
        rvResults = v.findViewById(R.id.rv_results);
        tv_searchProcess = v.findViewById(R.id.tv_searchProcess);

        tv_searchProcess.setVisibility(INVISIBLE);

        vm = new ViewModelProvider(this).get(BookViewModel.class);

        if(! itsRecomendation) {
            etSearch.setText(vm.getLastSearchString());
            vm.setLastSearchByAPIToList();
        }

        // 1) Adapter + RecyclerView
        adapter = new SearchAdapter(this::onItemLongClick);
        rvResults.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        rvResults.setAdapter(adapter);

        // 2) Наблюдаем за локальной БД
        vm.getAllBooks().observe(getViewLifecycleOwner(), books -> {
            adapter.setSavedBooks(books);
        });

        vm.getAllStatuses().observe(getViewLifecycleOwner(), read_ststuses->{
            adapter.setStatuses(read_ststuses);
        });

        // 2.1) Получим все жанры
        vm.getAllGenres().observe(getViewLifecycleOwner(), genresList -> {
            adapter.setSavedGenres(genresList);
            if(itsRecomendation){
                genres = String.join("+", genresList);
            }
        });
        // 3) Поиск по API
        btnSearch.setOnClickListener(x -> {
            String q = etSearch.getText().toString().trim();
            if(q.isEmpty() && vm.getGenres().isEmpty()){
                tv_searchProcess.setVisibility(VISIBLE);
                tv_searchProcess.setText("Необходимо указать либо жанр в настройках либо строку поиска");
                return;
            }
            //if (!q.isEmpty()) {
                vm.searchBooks(q);
                tv_searchProcess.setVisibility(VISIBLE);
                tv_searchProcess.setText("Идет поиск...");
           // }

        });

        // 4) Отображаем результаты поиска
        vm.getSearchResults().observe(getViewLifecycleOwner(), docs -> {
            adapter.setItems(docs);
            if(BookRepository.lastSearchError.isEmpty()){
                tv_searchProcess.setVisibility(INVISIBLE);}
            else
            {
                tv_searchProcess.setVisibility(VISIBLE);
                tv_searchProcess.setText(BookRepository.lastSearchError);
            }
        });

        return v;
    }

    // 5) Long-click: тут уже знаем из adapter, что savedBook = null или Book
    private void onItemLongClick(SearchDoc doc, Book saved, String idKey, View anchor, String key) {
        PopupMenu pm = new PopupMenu(requireContext(), anchor);
        Menu menu = pm.getMenu();

        if (saved != null) {
            // 2) «Изменить статус»
            SubMenu sm = menu.addSubMenu("Изменить статус");
            sm.add("В планах").setOnMenuItemClickListener(i -> {
                saved.status_ID = 1;
                vm.insertOrUpdate(saved);
                return true;
            });
            sm.add("Читаю").setOnMenuItemClickListener(i -> {
                saved.status_ID = 2;
                vm.insertOrUpdate(saved);
                return true;
            });
            sm.add("Прочитано").setOnMenuItemClickListener(i -> {
                saved.status_ID = 3;
                vm.insertOrUpdate(saved);
                return true;
            });
            menu.add("Удалить из библиотеки").setOnMenuItemClickListener(i -> {
                vm.deleteBook(saved);
                return true;
            });

        } else {
            menu.add("Добавить в библиотеку").setOnMenuItemClickListener(i -> {
                // собираем новый Book
                Book b = new Book();
                b.book_name = doc.title;
                b.key = key;
                b.author    = (doc.authors != null && !doc.authors.isEmpty())
                        ? doc.authors.get(0) : "";
                b.genre     = (doc.subject_key != null && !doc.subject_key.isEmpty())
                        ? doc.subject_key.get(0) : "";;
                b.cnt_pages = 0;
                b.status_ID = 1;
                b.idKey     = idKey;            // теперь гарантированно ненулевой
                // coverUrl мы уже считали в адаптере: можно пересчитать так же
                if (doc.coverId != null) {
                    b.coverUrl = "https://covers.openlibrary.org/b/id/"
                            + doc.coverId + "-M.jpg";
                } else if (doc.isbns != null && !doc.isbns.isEmpty()) {
                    b.coverUrl = "https://covers.openlibrary.org/b/isbn/"
                            + doc.isbns.get(0) + "-M.jpg";
                } else {
                    b.coverUrl = "https://covers.openlibrary.org/b/olid/"
                            + doc.editionKeys.get(0) + "-M.jpg";
                }
                vm.insertOrUpdate(b);
                return true;
            });
        }
        pm.show();
    }



}

