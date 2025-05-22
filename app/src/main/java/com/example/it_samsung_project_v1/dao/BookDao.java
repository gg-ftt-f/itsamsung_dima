package com.example.it_samsung_project_v1.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.it_samsung_project_v1.models.Book;
import com.example.it_samsung_project_v1.models.Status;

import java.util.List;

@Dao
public interface BookDao {
    @Query("SELECT * FROM books")
    LiveData<List<Book>> getAll();

    @Query("SELECT * FROM books where status_ID = :statusId")
    LiveData<List<Book>> getAll(int statusId);
    @Query("SELECT distinct genre FROM books")
    LiveData<List<String>> getAllGenres();
    @Query("SELECT * FROM statuses")
    LiveData<List<Status>> getAllStatuses();
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Book book);

    @Delete
    void delete(Book book);

    @Delete
    void deleteStatus(Status status);

    @Update
    void update(Book book);

    @Insert
    void insertNewStatus(Status status);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertWithIdReturn(Book book);

    @Query("SELECT * FROM books WHERE idKey = :idKey LIMIT 1")
    Book findByIdKey(String idKey);
}


