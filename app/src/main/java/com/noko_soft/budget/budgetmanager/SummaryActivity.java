package com.noko_soft.budget.budgetmanager;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;


public class SummaryActivity extends AppCompatActivity {

    private String MoneyFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Resources res = getApplication().getResources();
        MoneyFormat = res.getString(R.string.money_format);

        TransactionRepository repository = new TransactionRepository(getApplication(), Calendar.getInstance());

        SetAmount(R.id.textView_monthlyIncome, repository.SummaryMonthlyIncome);
        SetAmount(R.id.textView_debitOrders, repository.SummaryDebitOrders);
        SetAmount(R.id.textView_netIncome, repository.SummaryNetIncome);
        SetAmount(R.id.textView_otherIncome, repository.SummaryOtherIncome);
        SetAmount(R.id.textView_otherExpenses, repository.SummaryOtherExpenses);
        SetAmount(R.id.textView_allowance, repository.SummaryAllowance);
        SetAmount(R.id.textView_week1, repository.SummaryWeek1);
        SetAmount(R.id.textView_week2, repository.SummaryWeek2);
        SetAmount(R.id.textView_week3, repository.SummaryWeek3);
        SetAmount(R.id.textView_week4, repository.SummaryWeek4);
        SetAmount(R.id.textView_week5, repository.SummaryWeek5);
        SetAmount(R.id.textView_remainder, repository.SummaryRemainder);
    }

    public void SetAmount(int id, double amount) {
        TextView textView = findViewById(id);
        String text = String.format(MoneyFormat, amount);
        textView.setText(text);
    }

    public void SetAmount(int id, LiveData<Float> amount) {
        final TextView textView = findViewById(id);
        final Observer<Float> observer = new Observer<Float>() {
            @Override
            public void onChanged(@Nullable Float aFloat) {
                if (aFloat != null)
                    textView.setText(String.format(MoneyFormat, aFloat));
                else
                    textView.setText(String.format(MoneyFormat, 0.0));
            }
        };
        amount.observe(this, observer);
    }
}
