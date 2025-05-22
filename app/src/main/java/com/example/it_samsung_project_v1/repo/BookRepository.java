package com.example.it_samsung_project_v1.repo;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.it_samsung_project_v1.api.OpenLibraryApi;
import com.example.it_samsung_project_v1.api.OpenLibraryClient;
import com.example.it_samsung_project_v1.api.dto.SearchDoc;
import com.example.it_samsung_project_v1.api.dto.SearchResponse;
import com.example.it_samsung_project_v1.dao.BookDao;
import com.example.it_samsung_project_v1.db.BookDatabase;
import com.example.it_samsung_project_v1.models.Book;
import com.example.it_samsung_project_v1.models.Status;
import com.example.it_samsung_project_v1.vm.BookViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class BookRepository {
    private final BookDao bookDao;
    private final ExecutorService executor;
    private final MutableLiveData<List<SearchDoc>> searchResults = new MutableLiveData<>();

    private static final MutableLiveData<List<SearchDoc>> lastSearchByApi = new MutableLiveData<>();

    public static String lastSearchError = "";

    private final OpenLibraryApi api;

    public static List<Book> cashedBook = new ArrayList<>();

    public BookRepository(Application app) {
        BookDatabase db = BookDatabase.getInstance(app);
        bookDao = db.bookDao();
        executor = Executors.newSingleThreadExecutor();
        api = OpenLibraryClient.getApi();
    }

    // --- Room ---
    public LiveData<List<Book>> getAllBooks() {
        return bookDao.getAll();
    }

    public LiveData<List<Book>> getAllBooks(Status filter) {
        return bookDao.getAll(filter.status_id);
    }

    public LiveData<List<Status>> getAllStatuses() {
        return bookDao.getAllStatuses();
    }
    public LiveData<List<String>> getAllGenres() {
        return bookDao.getAllGenres();
    }
    public void deleteBook(Book book) {
        executor.execute(() -> bookDao.delete(book));
    }
    public void deleteStatus(Status status){
        executor.execute(()->bookDao.deleteStatus(status));
    }
    public void addStatus(String caption){
        Status status = new Status();
        status.caption = caption;
        executor.execute(()->bookDao.insertNewStatus(status));
    }



    public void insertOrUpdate(Book book) {
        executor.execute(() -> {
            long newId = bookDao.insertWithIdReturn(book);
            if (newId == -1) {
                // конфликт по idKey — обновляем
                Book existing = bookDao.findByIdKey(book.idKey);
                if (existing != null) {
                    book.book_ID = existing.book_ID;
                    bookDao.update(book);
                }
            } else {
                book.book_ID = (int)newId;
            }
        });
    }


    // --- Open Library API ---
    public LiveData<List<SearchDoc>> getSearchResults() {
        return searchResults;
    }

    public void setLastSearchByAPIToList(){
        if(lastSearchByApi.getValue() != null)
            searchResults.postValue(lastSearchByApi.getValue());
    }

    public void searchBooks(String query, String genres) {

        List<String> idfilter = new ArrayList<>();
        for(Book b:cashedBook){
            if(b.key.isEmpty()) {
                continue;
            }
            idfilter.add(b.key);

        }

        String filterIdString = String.join(" OR ", idfilter);

        executor.execute(() -> {
            lastSearchError = "";
            String langFilter = "(language:eng OR language:rus)";
            if(! BookViewModel.isEn){
                langFilter = "(language:rus)";
            }
            String fullQuery  = query.isEmpty() ? langFilter : query + " AND  " + langFilter;
            if(!filterIdString.isEmpty()) {
                fullQuery = fullQuery + " AND NOT (" + filterIdString + ")";
            }
            try {
                Response<SearchResponse> resp = null;
                boolean isGanres = genres != null & !genres.isEmpty();


                if(isGanres){
                    resp = api
                            .searchBooks(fullQuery, 50, "subject_key,title,isbn,cover_i,author_name,first_publish_year,key","rating", genres).execute();
                }else{
                    resp = api.searchBooks_q(fullQuery, 50, "subject_key,title,isbn,cover_i,author_name,first_publish_year,key","rating").execute();
                }

                if (resp.isSuccessful() && resp.body() != null) {
                    searchResults.postValue(resp.body().docs);
                    lastSearchByApi.postValue(resp.body().docs);
                } else {
                    searchResults.postValue(Collections.emptyList());
                    lastSearchByApi.postValue(Collections.emptyList());
                }
            } catch (IOException e) {
                e.printStackTrace();
                lastSearchError = e.getMessage();
                searchResults.postValue(Collections.emptyList());
                lastSearchByApi.postValue(Collections.emptyList());
            }
        });
    }

}

