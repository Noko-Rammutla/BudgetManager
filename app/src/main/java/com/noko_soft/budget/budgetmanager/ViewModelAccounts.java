package com.noko_soft.budget.budgetmanager;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.Calendar;
import java.util.List;

public class ViewModelAccounts extends AndroidViewModel {

    public final LiveData<List<Account>> accounts;
    public final LiveData<Float> totalDifference;
    public final LiveData<Float> totalTransactions;
    public final LiveData<Float> totalAccounts;
    private RepoAccounts repoAccounts;

    public ViewModelAccounts(Application application) {
        super(application);

        repoAccounts = new RepoAccounts(application);


        accounts = repoAccounts.accounts;

        totalAccounts = repoAccounts.totalAccounts;
        totalTransactions = repoAccounts.totalTransactions;
        totalDifference = repoAccounts.totalDifference;
    }

    public void insert(Account ... accounts) {
        repoAccounts.insert(accounts);
    }

    public void insert(Transaction ... transactions) {
        repoAccounts.insert(transactions);
    }

    public void update(Account ... accounts) {
        repoAccounts.update(accounts);
    }

    public void delete(Account ... accounts) {
        repoAccounts.delete(accounts);
    }
}
