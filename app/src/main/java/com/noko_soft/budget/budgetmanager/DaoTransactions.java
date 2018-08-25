package com.noko_soft.budget.budgetmanager;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;

import java.util.Date;
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

    @Query("SELECT * FROM `Transaction` ORDER BY date ASC")
    LiveData<List<Transaction>> getAll();

    @Query("SELECT * FROM `Transaction` WHERE major AND (NOT budget) AND date BETWEEN :start AND :end ORDER BY DATE ASC")
    LiveData<List<Transaction>> getMonthFinal(Date start, Date end);

    @Query("SELECT * FROM `Transaction` WHERE major AND DATE BETWEEN :start AND :end ORDER BY DATE ASC")
    LiveData<List<Transaction>> getMonth(Date start, Date end);

    @Query("SELECT * FROM `Transaction` WHERE major AND recurring AND date BETWEEN :start AND :end ORDER BY DATE ASC")
    LiveData<List<Transaction>> getMonthRecurring(Date start, Date end);

    @Query("SELECT * FROM `Transaction` WHERE major AND (Not recurring) AND date BETWEEN :start AND :end ORDER BY DATE ASC")
    LiveData<List<Transaction>> getMonthNonRecurring(Date start, Date end);

    @Query("SELECT * FROM `Transaction` WHERE (NOT major) AND date BETWEEN :start AND :end ORDER BY DATE ASC")
    LiveData<List<Transaction>> getWeek(Date start, Date end);

    @Query("SELECT SUM(amount) FROM `Transaction` WHERE (NOT major) AND date BETWEEN :start AND :end")
    LiveData<Float> getWeekTotal(Date start, Date end);

    @Query("SELECT SUM(amount) FROM `Transaction` WHERE (NOT budget) AND date BETWEEN :start AND :end")
    LiveData<Float> getFinalTotal(Date start, Date end);

    @Query("SELECT SUM(amount) FROM `Transaction` WHERE DATE BETWEEN :start AND :end")
    LiveData<Float> getTotal(Date start, Date end);

    @Query("SELECT SUM(amount) FROM `Transaction` WHERE major AND (NOT recurring) AND date BETWEEN :start AND :end")
    LiveData<Float> getMonthNonRecurringTotal(Date start, Date end);

    @Query("SELECT SUM(amount) FROM `Transaction` WHERE major AND recurring AND date BETWEEN :start AND :end")
    LiveData<Float> getMonthRecurringTotal(Date start, Date end);

    @Query("SELECT SUM(amount) FROM `Transaction` WHERE major = :bMajor AND recurring = :bRecurring AND date BETWEEN :start AND :end")
    LiveData<Float> getSum(boolean bMajor, boolean bRecurring, Date start, Date end);

    @Query("SELECT SUM(amount) FROM `Transaction` WHERE amount > 0 AND major = :bMajor AND recurring = :bRecurring AND date BETWEEN :start AND :end")
    LiveData<Float> getPositive(boolean bMajor, boolean bRecurring, Date start, Date end);

    @Query("SELECT SUM(amount) FROM `Transaction` WHERE amount < 0 AND major = :bMajor AND recurring = :bRecurring AND date BETWEEN :start AND :end")
    LiveData<Float> getNegative(boolean bMajor, boolean bRecurring, Date start, Date end);
}
