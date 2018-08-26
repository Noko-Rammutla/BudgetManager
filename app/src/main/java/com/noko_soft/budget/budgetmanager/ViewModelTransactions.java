package com.noko_soft.budget.budgetmanager;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.List;

public class ViewModelTransactions extends AndroidViewModel {
    private RepoTransactions mRepository;

    public final LiveData<List<Transaction>> MonthFinal, MonthBudget, MonthRecurring, MonthNonRecurring, Week, All;
    public final LiveData<Float> MonthFinalTotal, MonthBudgetTotal, MonthRecurringTotal, MonthNonRecurringTotal, WeekTotal;

    public ViewModelTransactions(Application application) {
        super(application);
        mRepository = new RepoTransactions(application);
        All = mRepository.All;

        MonthFinal = mRepository.MonthFinal;
        MonthBudget = mRepository.MonthBudget;
        MonthRecurring = mRepository.MonthRecurring;
        MonthNonRecurring = mRepository.MonthNonRecurring;
        Week = mRepository.Week;

        MonthFinalTotal = mRepository.MonthFinalTotal;
        MonthBudgetTotal = mRepository.MonthBudgetTotal;
        MonthRecurringTotal = mRepository.MonthRecurringTotal;
        MonthNonRecurringTotal = mRepository.MonthNonRecurringTotal;
        WeekTotal = mRepository.WeekTotal;
    }

    public void insert(Transaction ... transactions) {
        mRepository.insert(transactions);
    }

    public void delete(Transaction ... transactions) {
        mRepository.delete(transactions);
    }

    public void update(Transaction ... transactions) {
        mRepository.update(transactions);
    }
}
