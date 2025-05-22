package com.example.it_samsung_project_v1.api.dto;

import com.squareup.moshi.Json;
import java.util.List;

public class SearchDoc {
    @Json(name = "title")
    public String title;

    @Json(name = "author_name")
    public List<String> authors;

    @Json(name = "first_publish_year")
    public Integer firstPublishYear;

    /** id картинки-обложки */
    @Json(name = "cover_i")
    public Integer coverId;

    /** ISBN-ы, если есть */
    @Json(name = "isbn")
    public List<String> isbns;

    @Json(name = "subject_key")
    public List<String> subject_key;

    /** Ключи издания (OLID), если ISBN нет */
    @Json(name = "edition_key")
    public List<String> editionKeys;

    /** Ключ работы (work key), на крайний случай */
    @Json(name = "key")
    public String workKey;
}
