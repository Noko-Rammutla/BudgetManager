package com.noko_soft.budget.budgetmanager;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.List;

public class TransactionViewModel extends AndroidViewModel {
    private TransactionRepository mRepository;

    public TransactionViewModel(Application application, Calendar cal) {
        super(application);
        mRepository = new TransactionRepository(application, cal);
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

    public LiveData<Float> getMonthTotal() {
        return mRepository.getMonthTotal();
    }

    public static class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
        private Calendar cal;
        private Application application;

        public ViewModelFactory(Application application, Calendar cal) {
            this.cal = cal;
            this.application = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new TransactionViewModel(this.application, this. cal);
        }
    }
}
