package com.noko_soft.budget.budgetmanager;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

@Dao
@TypeConverters({Converters.class})
public interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertTransactions(Transaction ... transactions);

    @Update
    void updateTransactions(Transaction ... transactions);

    @Delete
    void deleteTransactions(Transaction ... transactions);

    @Query("DELETE FROM `Transaction`")
    void deleteAll();

    @Query("SELECT * FROM `Transaction` ORDER BY DATE ASC")
    LiveData<List<Transaction>> getAll();

    @Query("SELECT * FROM `Transaction` WHERE major ORDER BY DATE ASC")
    LiveData<List<Transaction>> getMonth();

    @Query("SELECT * FROM `Transaction` WHERE major AND (NOT budget) ORDER BY DATE ASC")
    LiveData<List<Transaction>> getMonthFinal();

    @Query("SELECT * FROM `Transaction` WHERE (NOT major) ORDER BY DATE ASC")
    LiveData<List<Transaction>> getWeek();

    @Query("SELECT * FROM `transaction` WHERE (NOT major) AND (NOT budget) ORDER BY DATE ASC")
    LiveData<List<Transaction>> getWeekFinal();
}
