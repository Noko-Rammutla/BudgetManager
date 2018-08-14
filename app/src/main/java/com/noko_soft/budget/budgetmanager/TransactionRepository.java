package com.noko_soft.budget.budgetmanager;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TransactionRepository {
    private final TransactionDao transactionDao;

    public TransactionRepository(Application application) {
        TransactionRoomDatabase db = TransactionRoomDatabase.getDatabase(application);
        transactionDao = db.transactionDao();
    }

    public LiveData<List<Transaction>> getAll() {
        return transactionDao.getAll();
    }

    public LiveData<List<Transaction>> getMonth() {
        return transactionDao.getMonth();
    }

    public void insert(Transaction ... transactions) {
        new insertAsyncTask(transactionDao).execute(transactions);
    }

    public void update(Transaction ... transactions) {
        new updateAsyncTask(transactionDao).execute(transactions);
    }

    public void delete(Transaction ... transactions) {
        new deleteAyncTask(transactionDao).execute(transactions);
    }

    public void deleteAll() {
        new deleteAllAsyncTask(transactionDao).execute();
    }

    private static class insertAsyncTask extends AsyncTask<Transaction, Void, Void> {
        private TransactionDao mAsyncTaskDao;

        insertAsyncTask(TransactionDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Transaction... transactions) {
            mAsyncTaskDao.InsertTransactions(transactions);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<Transaction, Void, Void> {
        private TransactionDao mAsyncTaskDao;

        updateAsyncTask(TransactionDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Transaction... transactions) {
            mAsyncTaskDao.updateTransactions(transactions);
            return null;
        }
    }

    private static class deleteAyncTask extends AsyncTask<Transaction, Void, Void> {
        private TransactionDao mAsyncTaskDao;

        deleteAyncTask(TransactionDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Transaction... transactions) {
            mAsyncTaskDao.deleteTransactions(transactions);
            return null;
        }
    }

    private static class deleteAllAsyncTask extends AsyncTask<Void, Void, Void> {
        private TransactionDao mAsyncTaskDao;

        deleteAllAsyncTask(TransactionDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Void ... params) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }
}
