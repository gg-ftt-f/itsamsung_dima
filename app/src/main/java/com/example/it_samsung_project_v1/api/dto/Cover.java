package com.example.it_samsung_project_v1.api.dto;

import com.squareup.moshi.Json;

public class Cover {
    @Json(name = "small")
    public String small;

    @Json(name = "medium")
    public String medium;

    @Json(name = "large")
    public String large;
}
