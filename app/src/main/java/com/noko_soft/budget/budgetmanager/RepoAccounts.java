package com.noko_soft.budget.budgetmanager;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.Calendar;
import java.util.List;

public class RepoAccounts {
    public final LiveData<List<Account>> accounts;
    public final LiveData<Float> totalDifference;
    public final LiveData<Float> totalTransactions;
    public final LiveData<Float> totalAccounts;
    private DaoAccounts daoAccounts;
    private DaoTransactions daoTransactions;

    public RepoAccounts(Application application) {
        BudgetManagerRoomDatabase db = BudgetManagerRoomDatabase.getDatabase(application);
        daoAccounts = db.accountsDao();
        daoTransactions = db.transactionDao();

        accounts = daoAccounts.getAccounts();
        Calendar monthStart = Calendar.getInstance();
        monthStart.set(Calendar.DAY_OF_MONTH, 1);
        Calendar monthEnd = (Calendar) monthStart.clone();
        monthEnd.add(Calendar.MONTH, 1);

        totalAccounts = daoAccounts.accountTotal();
        totalTransactions = daoTransactions.getFinalTotal(monthStart.getTime(), monthEnd.getTime());
        totalDifference = new LiveDataDifference(totalTransactions, totalAccounts);
    }

    public void insert(Account ... accounts) {
        new insertAccountAsyncTask(daoAccounts).execute(accounts);
    }

    public void insert(Transaction ... transactions) {
        new insertTransactionAsyncTask(daoTransactions).execute(transactions);
    }

    public void update(Account ... accounts) {
        new updateAccountAsyncTask(daoAccounts).execute(accounts);
    }

    public void delete(Account ... accounts) {
        new deleteAccountAyncTask(daoAccounts).execute(accounts);
    }

    private static class insertTransactionAsyncTask extends AsyncTask<Transaction, Void, Void> {
        private DaoTransactions mAsyncTaskDao;

        insertTransactionAsyncTask(DaoTransactions dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Transaction... transactions) {
            mAsyncTaskDao.InsertTransactions(transactions);
            return null;
        }
    }

    private static class insertAccountAsyncTask extends AsyncTask<Account, Void, Void> {
        private DaoAccounts mAsyncTaskDao;

        insertAccountAsyncTask(DaoAccounts dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Account... accounts) {
            mAsyncTaskDao.InsertAccounts(accounts);
            return null;
        }
    }

    private static class updateAccountAsyncTask extends AsyncTask<Account, Void, Void> {
        private DaoAccounts mAsyncTaskDao;

        updateAccountAsyncTask(DaoAccounts dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Account... accounts) {
            mAsyncTaskDao.updateAccounts(accounts);
            return null;
        }
    }

    private static class deleteAccountAyncTask extends AsyncTask<Account, Void, Void> {
        private DaoAccounts mAsyncTaskDao;

        deleteAccountAyncTask(DaoAccounts dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Account... accounts) {
            mAsyncTaskDao.deleteAccounts(accounts);
            return null;
        }
    }


}
