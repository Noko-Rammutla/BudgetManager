package com.noko_soft.budget.budgetmanager;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class Converters {
    @TypeConverter
    public Long toLong(Date date) {
        if (date == null)
            return null;
        else
            return date.getTime();
    }

    @TypeConverter
    public Date toDate(Long timestamp) {
        if (timestamp == null)
            return null;
        else
            return new Date(timestamp);
    }
}

