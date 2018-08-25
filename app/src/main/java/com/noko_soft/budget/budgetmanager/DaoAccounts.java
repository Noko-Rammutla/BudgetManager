package com.noko_soft.budget.budgetmanager;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface DaoAccounts {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertAccounts(Account ... accounts);

    @Query("SELECT * FROM Account ORDER BY name ASC")
    LiveData<List<Account>> getAccounts();

    @Delete
    void deleteAccounts(Account ... accounts);

    @Update
    void updateAccounts(Account ... accounts);

    @Query("SELECT SUM(amount) FROM Account")
    LiveData<Float> accountTotal();
}
