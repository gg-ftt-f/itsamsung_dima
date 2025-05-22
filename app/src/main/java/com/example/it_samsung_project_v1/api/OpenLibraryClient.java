package com.example.it_samsung_project_v1.api;

import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class OpenLibraryClient {
    private static final String BASE_URL = "https://openlibrary.org/";

    private static Retrofit retrofit;
    private static OpenLibraryApi apiService;

    public static OpenLibraryApi getApi() {
        if (apiService == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build();
            apiService = retrofit.create(OpenLibraryApi.class);
        }
        return apiService;
    }
}

