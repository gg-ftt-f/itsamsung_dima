package com.example.it_samsung_project_v1.api;

import com.example.it_samsung_project_v1.api.dto.BookDetail;
import com.example.it_samsung_project_v1.api.dto.SearchResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenLibraryApi {
    @GET("search.json")
    Call<SearchResponse> searchBooks_q(
            @Query("q") String query,
            @Query("limit") int limit,
            @Query("fields") String fields,
            @Query("sort") String rating
    );

    @GET("search.json")
    Call<SearchResponse> searchBooks(
            @Query("limit") int limit,
            @Query("fields") String fields
    );
    @GET("search.json")
    Call<SearchResponse> searchBooks(
            @Query("q") String query,
            @Query("limit") int limit,
            @Query("fields") String fields,
            @Query("sort") String rating,
            @Query("subject") String genres
    );
    @GET("search.json")
    Call<SearchResponse> searchBooks(
            @Query("limit") int limit,
            @Query("fields") String fields,
            @Query("sort") String rating,
            @Query("subject") String genres
    );
    @GET("api/books")
    Call<Map<String, BookDetail>> getBookDetails(
            @Query("bibkeys") String bibkeys,    // например "ISBN:0451526538,OLID:OL123M"
            @Query("format") String format,      // "json"
            @Query("jscmd") String jscmd         // "data"
    );
}
