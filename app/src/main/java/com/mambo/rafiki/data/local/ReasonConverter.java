package com.mambo.rafiki.data.local;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mambo.rafiki.data.entities.Reason;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ReasonConverter {

    private static Gson gson = new Gson();

    @TypeConverter
    public static List<Reason> stringToReasonsList(String reasonsString) {

        if (reasonsString == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Reason>>() {
        }.getType();

        return gson.fromJson(reasonsString, listType);
    }

    @TypeConverter
    public static String reasonsListToString(List<Reason> reasons) {
        return gson.toJson(reasons);
    }

}
