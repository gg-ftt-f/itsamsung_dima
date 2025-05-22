package com.example.it_samsung_project_v1.api.dto;

import com.squareup.moshi.Json;

import java.util.List;

public class BookDetail {
    @Json(name = "title")
    public String title;

    @Json(name = "authors")
    public List<Author> authors;

    @Json(name = "publish_date")
    public String publishDate;

    @Json(name = "cover")
    public Cover cover;
}
