package com.noko_soft.budget.budgetmanager;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

public class LiveDataDifference extends LiveData<Float>{
    private LiveData<Float> liveDataF1;
    private  LiveData<Float> liveDataF2;

    private float f1;
    private float f2;

    private void setF1(float aFloat) {
        f1 = aFloat;
        setValue(f1 - f2);
    }

    private void setF2(float aFloat) {
        f2 = aFloat;
        setValue(f1 - f2);
    }

    public LiveDataDifference(LiveData<Float> f1, LiveData<Float> f2) {
        this.liveDataF1 = f1;
        this.liveDataF2 = f2;

        Float value1 = f1.getValue();
        Float value2 = f2.getValue();

        if (value1 != null)
            this.f1 = value1;
        else
            this.f1 = 0;
        if (value2 != null)
            this.f1 = value2;
        else
            this.f2 = 0;

        final Observer<Float> f1Observer = new Observer<Float>() {
            @Override
            public void onChanged(@Nullable Float aFloat) {
                if (aFloat != null)
                    setF1(aFloat);
            }
        };
        f1.observeForever(f1Observer);

        final  Observer<Float> f2Observer = new Observer<Float>() {
            @Override
            public void onChanged(@Nullable Float aFloat) {
                if (aFloat != null)
                    setF2(aFloat);
            }
        };
        f2.observeForever(f2Observer);

        setValue(this.f1 - this.f2);
    }
}