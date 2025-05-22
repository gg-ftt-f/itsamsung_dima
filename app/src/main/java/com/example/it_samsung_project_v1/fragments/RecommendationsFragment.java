package com.example.it_samsung_project_v1.fragments;

import static android.view.View.INVISIBLE;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.it_samsung_project_v1.R;
import com.example.it_samsung_project_v1.adapter.SearchAdapter;
import com.example.it_samsung_project_v1.api.dto.SearchDoc;
import com.example.it_samsung_project_v1.models.Book;
import com.example.it_samsung_project_v1.vm.BookViewModel;

import java.util.ArrayList;
import java.util.List;

public class RecommendationsFragment extends Fragment {

    private BookViewModel bookViewModel;
    private EditText etSearch;
    private Button btnSearch;
    private Button btnGenres;
    private TextView tvRecommendation;
    private RecyclerView rvResults;

    private SearchAdapter adapter;

    private String genres;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recomendations, container, false);

        // 1) Находим вьюшки
        etSearch         = view.findViewById(R.id.et_search);
        btnSearch        = view.findViewById(R.id.btn_search);
        tvRecommendation = view.findViewById(R.id.et_recomendation);
        btnGenres        = view.findViewById(R.id.bt_genresList);

        rvResults = view.findViewById(R.id.rv_recSearch);

        btnGenres.setOnClickListener(v->{
            GenresListFragment fragment = new GenresListFragment();
            FragmentTransaction trans = getParentFragmentManager().beginTransaction();
            trans.replace(R.id.nav_host_fragment, fragment);
            trans.commit();
        });

        // 2) Получаем ViewModel
        bookViewModel = new ViewModelProvider(requireActivity())
                .get(BookViewModel.class);


        adapter = new SearchAdapter(this::onItemLongClick);
        rvResults.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        rvResults.setAdapter(adapter);

        bookViewModel.getAllBooks().observe(getViewLifecycleOwner(), books -> {
            adapter.setSavedBooks(books);
        });

        // 3) По клику — запускаем поиск по API
        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            //if (!query.isEmpty()) {
                bookViewModel.searchBooks(query);
                tvRecommendation.setText("Идёт поиск…");
            //}
        });

        bookViewModel.getAllGenres().observe(getViewLifecycleOwner(), genresList -> {
            adapter.setSavedGenres(genresList);
            genres = String.join("+", genresList);
        });

        bookViewModel.getSearchResults().observe(getViewLifecycleOwner(), docs -> {
            adapter.setItems(docs);
            tvRecommendation.setVisibility(INVISIBLE);
        });

//        // 4) Наблюдаем за результатами API
//        bookViewModel.getSearchResults().observe(getViewLifecycleOwner(), docs -> {
//            if (docs == null || docs.isEmpty()) {
//                tvRecommendation.setText("Ничего не найдено");
//                return;
//            }
//            List<String> lines = new ArrayList<>();
//            for (SearchDoc doc : docs) {
//                String title  = doc.title != null ? doc.title : "Без названия";
//                String author = (doc.authors != null && !doc.authors.isEmpty())
//                        ? doc.authors.get(0)
//                        : "Автор неизвестен";
//                lines.add(title + " — " + author);
//            }
//            // соединяем через пустую строку для читабельности
//            tvRecommendation.setText(TextUtils.join("\n\n", lines));
//        });

        return view;
    }

    private void onItemLongClick(SearchDoc doc, Book saved, String idKey, View anchor, String key) {
        PopupMenu pm = new PopupMenu(requireContext(), anchor);
        Menu menu = pm.getMenu();

        if (saved != null) {
            // 2) «Изменить статус»
            SubMenu sm = menu.addSubMenu("Изменить статус");
            sm.add("В планах").setOnMenuItemClickListener(i -> {
                saved.status_ID = 1;
                bookViewModel.insertOrUpdate(saved);
                return true;
            });
            sm.add("Читаю").setOnMenuItemClickListener(i -> {
                saved.status_ID = 2;
                bookViewModel.insertOrUpdate(saved);
                return true;
            });
            sm.add("Прочитано").setOnMenuItemClickListener(i -> {
                saved.status_ID = 3;
                bookViewModel.insertOrUpdate(saved);
                return true;
            });
            menu.add("Удалить из библиотеки").setOnMenuItemClickListener(i -> {
                bookViewModel.deleteBook(saved);
                return true;
            });

        } else {
            menu.add("Добавить в библиотеку").setOnMenuItemClickListener(i -> {
                // собираем новый Book
                Book b = new Book();
                b.book_name = doc.title;
                b.key = doc.workKey;
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
                bookViewModel.insertOrUpdate(b);
                return true;
            });
        }
        pm.show();
    }

}
