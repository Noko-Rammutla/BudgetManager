package com.noko_soft.budget.budgetmanager;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TransactionRepository {
    private final TransactionDao transactionDao;
    private LiveData<List<Transaction>> MonthData;
    private LiveData<List<Transaction>> MonthFinal;
    private LiveData<List<Transaction>> WeekData;
    private LiveData<Float> MonthTotal;
    private LiveData<Float> MonthFinalTotal;
    private LiveData<Float> WeekTotal;

    public TransactionRepository(Application application, Calendar cal) {
        TransactionRoomDatabase db = TransactionRoomDatabase.getDatabase(application);
        transactionDao = db.transactionDao();

        Calendar monthStart = (Calendar) cal.clone();
        monthStart.set(Calendar.DAY_OF_MONTH, 1);
        Calendar monthEnd = (Calendar) monthStart.clone();
        monthEnd.add(Calendar.MONTH, 1);

        Calendar weekStart = (Calendar) monthStart.clone();
        weekStart.set(Calendar.WEEK_OF_MONTH, cal.get(Calendar.WEEK_OF_MONTH));
        Calendar weekEnd = (Calendar) weekStart.clone();
        weekEnd.add(Calendar.DAY_OF_WEEK, 7);

        MonthData = transactionDao.getMonth(monthStart.getTime(), monthEnd.getTime());
        MonthFinal = transactionDao.getMonthFinal(monthStart.getTime(), monthEnd.getTime());
        WeekData = transactionDao.getWeek(weekStart.getTime(), monthEnd.getTime());

        MonthTotal = transactionDao.getTotal(monthStart.getTime(), monthEnd.getTime());
        MonthFinalTotal = transactionDao.getFinalTotal(monthStart.getTime(), monthEnd.getTime());
        WeekTotal = transactionDao.getWeekTotal(weekStart.getTime(), weekEnd.getTime());
    }

    public LiveData<List<Transaction>> getAll() {
        return transactionDao.getAll();
    }

    public LiveData<List<Transaction>> getMonth() {
        return MonthData;
    }

    public LiveData<List<Transaction>> getMonth(Calendar cal) {
        Calendar monthStart = (Calendar) cal.clone();
        monthStart.set(Calendar.DAY_OF_MONTH, 1);
        Calendar monthEnd = (Calendar) monthStart.clone();
        monthEnd.add(Calendar.MONTH, 1);
        return transactionDao.getMonth(monthStart.getTime(), monthEnd.getTime());
    }

    public LiveData<List<Transaction>> getWeek() {
        return WeekData;
    }

    public LiveData<List<Transaction>> getWeek(Calendar cal) {
        Calendar weekStart = (Calendar) cal.clone();
        weekStart.set(Calendar.DAY_OF_MONTH, 1);
        weekStart.set(Calendar.WEEK_OF_MONTH, cal.get(Calendar.WEEK_OF_MONTH));
        Calendar weekEnd = (Calendar) weekStart.clone();
        weekEnd.add(Calendar.DAY_OF_WEEK, 7);
        return transactionDao.getMonth(weekStart.getTime(), weekEnd.getTime());
    }

    public LiveData<List<Transaction>> getMonthFinal() {
        return MonthFinal;
    }

    public LiveData<Float> getMonthFinalTotal() {
        return MonthFinalTotal;
    }

    public LiveData<Float> getMonthTotal() {
        return MonthTotal;
    }

    public LiveData<Float> getWeekTotal() {
        return WeekTotal;
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
