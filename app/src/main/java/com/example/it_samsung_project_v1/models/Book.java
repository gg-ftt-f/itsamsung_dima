package com.example.it_samsung_project_v1.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.List;


@Entity(tableName = "books")
public class Book {
    @PrimaryKey(autoGenerate = true)
    public int book_ID;

    public String book_name;
    public String author;
    public String genre;
    public String key;
    public int cnt_pages;
    public int status_ID;

    /** Единый ключ — ISBN или OLID или workKey */
    public String idKey;

    /** URL обложки */
    public String coverUrl;

    public static String StatusToText(Book b, List<Status> statuses){
        if(b==null){
            return "";

        } else {
            for(Status st:statuses){
                if(st.status_id==b.status_ID){
                    return st.caption;
                }
            }
            return "...";
        }

    }
}

