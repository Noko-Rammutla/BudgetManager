package com.noko_soft.budget.budgetmanager;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Transaction.class}, version = 1)
public abstract class BudgetManagerRoomDatabase extends RoomDatabase {
    public abstract DaoTransactions transactionDao();

    private static BudgetManagerRoomDatabase INSTANCE;

    public static BudgetManagerRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BudgetManagerRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BudgetManagerRoomDatabase.class,
                            "transaction_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
