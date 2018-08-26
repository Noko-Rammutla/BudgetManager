package com.noko_soft.budget.budgetmanager;

import android.arch.persistence.room.TypeConverter;

import java.sql.Date;

public class Converters {
    @TypeConverter
    public Long toLong(Date timestamp) {
        if (timestamp == null)
            return null;
        else {
            return timestamp.getTime();
        }
    }

    @TypeConverter
    public Date toDate(Long timestamp) {
        if (timestamp == null)
            return null;
        else
            return new Date(timestamp);
    }
}

