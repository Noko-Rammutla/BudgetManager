package com.noko_soft.budget.budgetmanager;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class ViewModelTransactions extends AndroidViewModel {
    private RepoTransactions mRepository;

    public final LiveData<List<Transaction>> Transactions, DebitOrders;
    public final LiveData<Float> TotalPlaceholder;
    public ViewModelTransactions(Application application) {
        super(application);
        mRepository = new RepoTransactions(application);
        Transactions = mRepository.Transactions;
        DebitOrders = mRepository.DebitOrders;

        TotalPlaceholder = new LiveDataConstant(0.0f);
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
