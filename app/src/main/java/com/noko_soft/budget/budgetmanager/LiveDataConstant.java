package com.noko_soft.budget.budgetmanager;

import android.arch.lifecycle.LiveData;

public class LiveDataConstant extends LiveData<Float> {

    public LiveDataConstant(float constant) {
        setValue(constant);
    }
}
