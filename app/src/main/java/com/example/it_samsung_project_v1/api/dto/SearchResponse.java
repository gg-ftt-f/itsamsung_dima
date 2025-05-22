package com.example.it_samsung_project_v1.api.dto;

import com.squareup.moshi.Json;

import java.util.List;

public class SearchResponse {
    @Json(name = "docs")
    public List<SearchDoc> docs;
}
