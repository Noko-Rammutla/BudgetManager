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
import java.util.Calendar;
import java.util.List;

@Dao
@TypeConverters({Converters.class})
public abstract class DaoTransactions {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void InsertTransactions(Transaction ... transactions);

    @Update
    abstract void updateTransactions(Transaction ... transactions);

    @Delete
    abstract void deleteTransactions(Transaction ... transactions);

    @Query("SELECT * FROM `Transaction`")
    abstract List<Transaction> dumpAll();

    @Query("SELECT * FROM `Transaction` WHERE (NOT archived) AND timestamp <= :endDate")
    abstract List<Transaction> getAll(Date endDate);

    @Query("SELECT * FROM `Transaction` WHERE (NOT archived) AND (NOT recurring) AND timestamp <= :endDate ORDER BY timestamp DESC")
    abstract LiveData<List<Transaction>> getTransactions(Date endDate);

    @Query("SELECT * FROM `Transaction` WHERE (NOT archived) AND recurring AND timestamp <= :endDate ORDER BY timestamp ASC")
    abstract LiveData<List<Transaction>> getDebitOrders(Date endDate);

    @Query("SELECT SUM(amount) FROM `Transaction` WHERE (NOT archived) AND (recurring = :recurring) AND amount >= 0.0 AND timestamp <= :endDate")
    abstract LiveData<Float> getTotalPositive(Date endDate, boolean recurring);

    @Query("SELECT SUM(amount) FROM `Transaction` WHERE (NOT archived) AND (recurring = :recurring) AND amount < 0.0 AND timestamp <= :endDate")
    abstract LiveData<Float> getTotalNegative(Date endDate, boolean recurring);

    @Query("SELECT SUM(amount) FROM `Transaction` WHERE (NOT archived) AND amount >= 0.0 AND timestamp <= :endDate")
    abstract LiveData<Float> getTotalPositive(Date endDate);

    @Query("SELECT SUM(amount) FROM `Transaction` WHERE (NOT archived) AND amount < 0.0 AND timestamp <= :endDate")
    abstract LiveData<Float> getTotalNegative(Date endDate);

    @android.arch.persistence.room.Transaction
    public void refreshData()
    {
        Calendar weekStart = Calendar.getInstance();
        weekStart.set(Calendar.HOUR_OF_DAY, 0);
        weekStart.set(Calendar.MINUTE, 0);
        weekStart.set(Calendar.MILLISECOND, 0);
        weekStart.add(Calendar.DAY_OF_MONTH, 1 - weekStart.get(Calendar.DAY_OF_WEEK));
        weekStart.add(Calendar.DAY_OF_MONTH, -7);
        List<Transaction> oldTransactions = getAll(new Date(weekStart.getTimeInMillis()));
        if (oldTransactions.size() == 0)
            return;
        float sum = 0;
        for (Transaction transaction: oldTransactions) {
            sum += transaction.amount;
            transaction.archived = true;
            if (transaction.recurring) {
                Calendar nextMonth = Calendar.getInstance();
                nextMonth.setTimeInMillis(transaction.timestamp.getTime());
                nextMonth.add(Calendar.MONTH, 1);
                Transaction newTransaction = new Transaction(transaction.name,
                        new Date(nextMonth.getTimeInMillis()),
                        transaction.amount,
                        transaction.recurring);
                InsertTransactions(newTransaction);
            }
            updateTransactions(transaction);
        }
        Transaction summary = new Transaction("Summary", new Date(weekStart.getTimeInMillis()), sum, false);
        InsertTransactions(summary);
    }
}
