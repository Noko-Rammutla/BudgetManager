package com.noko_soft.budget.budgetmanager;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;

import java.sql.Date;
import java.util.List;

@Dao
@TypeConverters({Converters.class})
public interface DaoTransactions {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertTransactions(Transaction ... transactions);

    @Update
    void updateTransactions(Transaction ... transactions);

    @Delete
    void deleteTransactions(Transaction ... transactions);

    @Query("DELETE FROM `Transaction`")
    void deleteAll();

    @Query("SELECT * FROM `Transaction` WHERE (NOT archived) AND (NOT recurring) AND timestamp <= :endDate ORDER BY timestamp ASC")
    LiveData<List<Transaction>> getTransactions(Date endDate);

    @Query("SELECT * FROM `Transaction` WHERE (NOT archived) AND recurring AND timestamp <= :endDate ORDER BY timestamp ASC")
    LiveData<List<Transaction>> getDebitOrders(Date endDate);

    @Query("SELECT SUM(amount) FROM `Transaction` WHERE (NOT archived) AND (recurring = :recurring) AND amount >= 0.0 AND timestamp <= :endDate")
    LiveData<Float> getTotalPositive(Date endDate, boolean recurring);

    @Query("SELECT SUM(amount) FROM `Transaction` WHERE (NOT archived) AND (recurring = :recurring) AND amount < 0.0 AND timestamp <= :endDate")
    LiveData<Float> getTotalNegative(Date endDate, boolean recurring);

    @Query("SELECT SUM(amount) FROM `Transaction` WHERE (NOT archived) AND amount >= 0.0 AND timestamp <= :endDate")
    LiveData<Float> getTotalPositive(Date endDate);

    @Query("SELECT SUM(amount) FROM `Transaction` WHERE (NOT archived) AND amount < 0.0 AND timestamp <= :endDate")
    LiveData<Float> getTotalNegative(Date endDate);
}
