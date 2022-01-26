package com.mambo.rafiki.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mambo.rafiki.data.entities.Choice;
import com.mambo.rafiki.utils.RoomUtils;

import java.util.List;

@Dao
public interface ChoiceDao {

    @Insert
    void insert(Choice choice);

    @Update
    void update(Choice choice);

    @Delete
    void delete(Choice choice);

    @Query("SELECT * FROM " + RoomUtils.TABLE_NAME_DECISION + " WHERE id = :id")
    Choice get(int id);

    @Query("SELECT * FROM " + RoomUtils.TABLE_NAME_DECISION + " WHERE isArchived = 0 ORDER BY id DESC")
    LiveData<List<Choice>> getAllDecisions();

    @Query("SELECT * FROM " + RoomUtils.TABLE_NAME_DECISION + " WHERE isArchived = 1 ORDER BY id DESC")
    LiveData<List<Choice>> getAllArchivedDecisions();

}
