package com.mambo.rafiki.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.mambo.rafiki.data.entities.Choice;
import com.mambo.rafiki.utils.RoomUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Choice.class}, version = 3, exportSchema = false)
public abstract class ChoiceDatabase extends RoomDatabase {

    public abstract ChoiceDao decisionDao();

    private static volatile ChoiceDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static ChoiceDatabase getDatabase(Context context) {

        if (INSTANCE == null) {
            synchronized (ChoiceDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ChoiceDatabase.class, RoomUtils.TABLE_NAME_DECISION)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }

        return INSTANCE;
    }


}
