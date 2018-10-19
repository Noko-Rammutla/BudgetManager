package com.noko_soft.budget.budgetmanager;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

public class RepoTransactions {
    private final DaoTransactions daoTransactions;
    public final LiveData<List<Transaction>> Transactions, DebitOrders;

    public RepoTransactions(Application application) {
        BudgetManagerRoomDatabase db = BudgetManagerRoomDatabase.getDatabase(application);
        daoTransactions = db.transactionDao();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_WEEK, 14);

        Date endDate = new Date(cal.getTimeInMillis());

        Transactions = daoTransactions.getTransactions(endDate);
        DebitOrders = daoTransactions.getDebitOrders(endDate);

        new refreshAsyncTask(daoTransactions).execute();
     }

     public LiveData<Float> getTotalPositive(Date endDate, boolean recurring) {
        return  daoTransactions.getTotalPositive(endDate, recurring);
     }

    public LiveData<Float> getTotalNegative(Date endDate, boolean recurring) {
        return  daoTransactions.getTotalNegative(endDate, recurring);
    }

    public LiveData<Float> getTotalPositive(Date endDate) {
        return  daoTransactions.getTotalPositive(endDate);
    }

    public LiveData<Float> getTotalNegative(Date endDate) {
        return  daoTransactions.getTotalNegative(endDate);
    }


    public void insert(Transaction ... transactions) {
        new insertAsyncTask(daoTransactions).execute(transactions);
    }

    public void update(Transaction ... transactions) {
        new updateAsyncTask(daoTransactions).execute(transactions);
    }

    public void delete(Transaction ... transactions) {
        new deleteAyncTask(daoTransactions).execute(transactions);
    }

    private static class insertAsyncTask extends AsyncTask<Transaction, Void, Void> {
        private DaoTransactions mAsyncTaskDao;

        insertAsyncTask(DaoTransactions dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Transaction... transactions) {
            mAsyncTaskDao.InsertTransactions(transactions);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<Transaction, Void, Void> {
        private DaoTransactions mAsyncTaskDao;

        updateAsyncTask(DaoTransactions dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Transaction... transactions) {
            mAsyncTaskDao.updateTransactions(transactions);
            return null;
        }
    }

    private static class deleteAyncTask extends AsyncTask<Transaction, Void, Void> {
        private DaoTransactions mAsyncTaskDao;

        deleteAyncTask(DaoTransactions dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Transaction... transactions) {
            mAsyncTaskDao.deleteTransactions(transactions);
            return null;
        }
    }
    
    private static class refreshAsyncTask extends AsyncTask<Void, Void, Void> {
        private DaoTransactions mAsyncTaskDao;

        refreshAsyncTask(DaoTransactions dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Void ... params) {
            mAsyncTaskDao.refreshData();
            return null;
        }
    }
}
