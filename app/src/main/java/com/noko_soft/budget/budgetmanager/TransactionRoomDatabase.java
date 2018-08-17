package com.noko_soft.budget.budgetmanager;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.Date;

@Database(entities = {Transaction.class}, version = 1)
public abstract class TransactionRoomDatabase extends RoomDatabase {
    public abstract TransactionDao transactionDao();

    private static TransactionRoomDatabase INSTANCE;

    public static TransactionRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TransactionRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TransactionRoomDatabase.class,
                            "transaction_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {


        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            new PopulateBbAsync(INSTANCE).execute();
        }
    };

    private static class PopulateBbAsync extends AsyncTask<Void, Void, Void> {
        private final TransactionDao mDao;

        PopulateBbAsync(TransactionRoomDatabase db) {
            mDao = db.transactionDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            mDao.deleteAll();
            Transaction t1 = new Transaction("Rent", new Date(), -5000, true, true, false);
            Transaction t2 = new Transaction("Salary", new Date(), 10000, true, true, false);
            Transaction t3 = new Transaction("Shadow of the Tomb Raider", new Date(), -1200, true, false, true);
            mDao.InsertTransactions(t1, t2, t3);
            return null;
        }
    }
}
