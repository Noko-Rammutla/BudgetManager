package com.noko_soft.budget.budgetmanager;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;
import java.util.Date;

@RunWith(JUnit4.class)
public class TransactionDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private TransactionDao transactionDao;
    private TransactionRoomDatabase DB;
    private String TAG = "TransactionDaoTest";

    @Before
    public void createDB() {
        Context context = InstrumentationRegistry.getTargetContext();
        DB = Room.inMemoryDatabaseBuilder(context, TransactionRoomDatabase.class)
                .allowMainThreadQueries()
                .build();
        transactionDao = DB.transactionDao();

        Calendar cal = Calendar.getInstance();
        Log.d(TAG, cal.toString());
        cal.set(2018, 1, 1);
        Transaction salary = new Transaction("Salary", cal.getTime(), 5000, true, true, false);
        cal.set(2018, 1, 1);
        Transaction rent = new Transaction("Rent", cal.getTime(), -2000, true, true, true);
        Transaction bread = new Transaction("Bread", cal.getTime(), -20, false, false, false);
        cal.set(2018, 1, 2);
        Transaction clothes = new Transaction("Clothes", cal.getTime(), -1500, true, false, true);
        transactionDao.InsertTransactions(salary, rent, bread, clothes);
    }

    @After
    public void closeDB() {
        DB.close();
    }

    @Test
    public void AllQueries() throws Exception {
         Log.d(TAG, "Get all values");
        for (Transaction t: LiveDataTestUtil.getValue(transactionDao.getAll()) ) {
            Log.d(TAG, t.name);
        }
    }
}

