package com.example.it_samsung_project_v1.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.it_samsung_project_v1.api.dto.SearchDoc;
import com.example.it_samsung_project_v1.models.Book;
import com.example.it_samsung_project_v1.models.Status;
import com.example.it_samsung_project_v1.repo.BookRepository;

import java.util.ArrayList;
import java.util.List;

public class BookViewModel extends AndroidViewModel {
    private final BookRepository repo;
    private final LiveData<List<Book>> allBooks;
    private final LiveData<List<Status>> allStatuses;
    private final LiveData<List<String>> allGenres;
    private final LiveData<List<SearchDoc>> searchResults;

    public static boolean gn_fantasy;
    public static boolean gn_science;
    public static boolean gn_romance;
    public static boolean gn_mystery;
    public static boolean gn_thriller;
    public static boolean gn_horror;
    public static boolean isEn;

    private static String lastSearchString;

    public BookViewModel(@NonNull Application app) {
        super(app);
        repo = new BookRepository(app);
        allBooks = repo.getAllBooks();
        allGenres = repo.getAllGenres();
        searchResults = repo.getSearchResults();
        allStatuses = repo.getAllStatuses();
    }

    // Room
    public LiveData<List<Book>> getAllBooks() {
        return allBooks;
    }
    public LiveData<List<Book>> getAllBooks(Status filter) {
        return repo.getAllBooks(filter);
    }

    public LiveData<List<Status>> getAllStatuses() {
        return allStatuses;
    }
    public LiveData<List<String>> getAllGenres() {
        return allGenres;
    }
    public void deleteBook(Book book) {
        repo.deleteBook(book);
    }

    public void deleteStatus(Status status){
        repo.deleteStatus(status);
    }

    public void addStatus(String caption){
        repo.addStatus(caption);
    }

    public void insertOrUpdate(Book book) {
        repo.insertOrUpdate(book);
    }

    // API
    public LiveData<List<SearchDoc>> getSearchResults() {
        return searchResults;
    }

    public String getGenres(){
        List<String> genresFilter = new ArrayList<>();
        if(gn_fantasy){
            genresFilter.add("fantasy");
        }
        if(gn_science){
            genresFilter.add("science");
        }
        if(gn_romance){
            genresFilter.add("romance");
        }
        if(gn_mystery){
            genresFilter.add("mystery");
        }
        if(gn_thriller){
            genresFilter.add("thriller");
        }
        if(gn_horror){
            genresFilter.add("horror");
        }
        String genres = String.join("+", genresFilter);

        return genres;
    }
    public void searchBooks(String query) {


        repo.searchBooks(query, getGenres());
        lastSearchString = query;
    }

    public void setLastSearchByAPIToList(){
        repo.setLastSearchByAPIToList();
    }

    public static String getLastSearchString(){
        return lastSearchString;
    }
}

