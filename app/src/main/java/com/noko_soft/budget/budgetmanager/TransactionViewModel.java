package com.noko_soft.budget.budgetmanager;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class TransactionViewModel extends AndroidViewModel {
    private TransactionRepository mRepository;

    public TransactionViewModel(Application application) {
        super(application);
        mRepository = new TransactionRepository(application);
    }

    public LiveData<List<Transaction>> getMonthAll() { return mRepository.getMonth(); }

    public LiveData<List<Transaction>> getAll() { return mRepository.getAll(); }

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
