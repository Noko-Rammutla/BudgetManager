package com.noko_soft.budget.budgetmanager;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.Date;
import java.util.Calendar;
import java.util.List;

public class TransactionRepository {
    private final TransactionDao transactionDao;
    public final LiveData<List<Transaction>> MonthFinal,
            MonthBudget,
            MonthRecurring,
            MonthNonRecurring,
            Week;

    public final LiveData<Float> MonthFinalTotal,
            MonthBudgetTotal,
            MonthRecurringTotal,
            MonthNonRecurringTotal,
            WeekTotal;

    public final LiveData<Float> SummaryMonthlyIncome,
        SummaryDebitOrders,
        SummaryNetIncome,
        SummaryOtherIncome,
        SummaryOtherExpenses,
        SummaryAllowance,
        SummaryWeek1,
        SummaryWeek2,
        SummaryWeek3,
        SummaryWeek4,
        SummaryWeek5,
        SummaryRemainder;

    public TransactionRepository(Application application, Calendar cal) {
        TransactionRoomDatabase db = TransactionRoomDatabase.getDatabase(application);
        transactionDao = db.transactionDao();

        Calendar monthStart = (Calendar) cal.clone();
        monthStart.set(Calendar.DAY_OF_MONTH, 1);
        Calendar monthEnd = (Calendar) monthStart.clone();
        monthEnd.add(Calendar.MONTH, 1);

        Calendar weekStart = (Calendar) monthStart.clone();
        weekStart.set(Calendar.WEEK_OF_MONTH, cal.get(Calendar.WEEK_OF_MONTH));
        weekStart.add(Calendar.DAY_OF_MONTH, 1 - weekStart.get(Calendar.DAY_OF_WEEK));
        Calendar weekEnd = (Calendar) weekStart.clone();
        weekEnd.add(Calendar.DAY_OF_WEEK, 7);

        MonthFinal = transactionDao.getMonthFinal(monthStart.getTime(), monthEnd.getTime());
        MonthBudget = transactionDao.getMonth(monthStart.getTime(), monthEnd.getTime());
        MonthRecurring = transactionDao.getMonthRecurring(monthStart.getTime(), monthEnd.getTime());
        MonthNonRecurring = transactionDao.getMonthNonRecurring(monthStart.getTime(), monthEnd.getTime());
        Week = transactionDao.getWeek(weekStart.getTime(), weekEnd.getTime());

        MonthFinalTotal = transactionDao.getFinalTotal(monthStart.getTime(), monthEnd.getTime());
        MonthBudgetTotal = transactionDao.getTotal(monthStart.getTime(), monthEnd.getTime());
        MonthRecurringTotal = transactionDao.getMonthRecurringTotal(monthStart.getTime(), monthEnd.getTime());
        MonthNonRecurringTotal = transactionDao.getMonthNonRecurringTotal(monthStart.getTime(), monthEnd.getTime());
        WeekTotal = transactionDao.getWeekTotal(weekStart.getTime(), weekEnd.getTime());

        SummaryMonthlyIncome = transactionDao.getPositive(true, true, monthStart.getTime(), monthEnd.getTime());
        SummaryDebitOrders = transactionDao.getNegative(true, true, monthStart.getTime(), monthEnd.getTime());
        SummaryNetIncome = new LiveDataSum(SummaryMonthlyIncome, SummaryDebitOrders);
        SummaryOtherIncome = transactionDao.getPositive(true, false, monthStart.getTime(), monthEnd.getTime());
        SummaryOtherExpenses = transactionDao.getNegative(true, false, monthStart.getTime(), monthEnd.getTime());
        SummaryAllowance = new LiveDataSum(SummaryNetIncome, new LiveDataSum(SummaryOtherIncome, SummaryOtherExpenses));
        SummaryRemainder = transactionDao.getTotal(monthStart.getTime(), monthEnd.getTime());

        Calendar WeekCounter = (Calendar) monthStart.clone();
        WeekCounter.add(Calendar.DAY_OF_MONTH, 1 - WeekCounter.get(Calendar.DAY_OF_WEEK));

        Date Weeks[] = new Date[6];
        for (int k = 0; k < 6; k++) {
            Weeks[k] = WeekCounter.getTime();
            WeekCounter.add(Calendar.DAY_OF_MONTH, 7);
        }

        SummaryWeek1 = transactionDao.getWeekTotal(Weeks[0], Weeks[1]);
        SummaryWeek2 = transactionDao.getWeekTotal(Weeks[1], Weeks[2]);
        SummaryWeek3 = transactionDao.getWeekTotal(Weeks[2], Weeks[3]);
        SummaryWeek4 = transactionDao.getWeekTotal(Weeks[3], Weeks[4]);
        if (WeekCounter.get(Calendar.DAY_OF_MONTH) < 14) {
            SummaryWeek5 = transactionDao.getWeekTotal(Weeks[4], Weeks[5]);
        } else {
            SummaryWeek5 = new LiveDataConstant(0);
        }

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
