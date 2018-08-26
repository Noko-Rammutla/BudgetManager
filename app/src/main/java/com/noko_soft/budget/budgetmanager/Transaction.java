package com.noko_soft.budget.budgetmanager;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import java.sql.Date;


@Entity(indices = {@Index("name")})
@TypeConverters({Converters.class})
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public float amount;

    public boolean major;

    public boolean recurring;

    public boolean budget;

    @NonNull
    public String name;

    @NonNull
    public Date timestamp;

    public Transaction(@NonNull String name, @NonNull Date timestamp, float amount, boolean major, boolean recurring, boolean budget) {
        this.name = name;
        this.timestamp = timestamp;
        this.amount = amount;
        this.major = major;
        this.recurring = recurring;
        this.budget = budget;
    }
}

