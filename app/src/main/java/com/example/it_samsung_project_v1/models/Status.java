package com.example.it_samsung_project_v1.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "statuses")
public class Status{
    @PrimaryKey(autoGenerate = true)
    public int status_id;
    public String caption;


    public String toString(){
        return caption;
    }
}
