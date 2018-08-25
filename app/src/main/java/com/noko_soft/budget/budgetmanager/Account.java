package com.noko_soft.budget.budgetmanager;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Account {
    @PrimaryKey
    @NonNull
    public String name;

    public float amount;

    public Account(@NonNull String name, float amount) {
        this.name = name;
        this.amount = amount;
    }
}
