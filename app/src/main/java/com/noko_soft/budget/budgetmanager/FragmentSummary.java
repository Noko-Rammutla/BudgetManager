package com.noko_soft.budget.budgetmanager;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;


public class FragmentSummary extends Fragment {

    private String MoneyFormat;

    public FragmentSummary() {
        // Required empty public constructor
    }

    public static FragmentSummary newInstance() {
        FragmentSummary fragment = new FragmentSummary();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

        Resources res = getActivity().getApplication().getResources();
        MoneyFormat = res.getString(R.string.money_format);

        RepoTransactions repository = new RepoTransactions(getActivity().getApplication(), Calendar.getInstance());

        SetAmount(view, R.id.textView_monthlyIncome, repository.SummaryMonthlyIncome);
        SetAmount(view, R.id.textView_debitOrders, repository.SummaryDebitOrders);
        SetAmount(view, R.id.textView_netIncome, repository.SummaryNetIncome);
        SetAmount(view, R.id.textView_otherIncome, repository.SummaryOtherIncome);
        SetAmount(view, R.id.textView_otherExpenses, repository.SummaryOtherExpenses);
        SetAmount(view, R.id.textView_allowance, repository.SummaryAllowance);
        SetAmount(view, R.id.textView_week1, repository.SummaryWeek1);
        SetAmount(view, R.id.textView_week2, repository.SummaryWeek2);
        SetAmount(view, R.id.textView_week3, repository.SummaryWeek3);
        SetAmount(view, R.id.textView_week4, repository.SummaryWeek4);
        SetAmount(view, R.id.textView_week5, repository.SummaryWeek5);
        SetAmount(view, R.id.textView_remainder, repository.SummaryRemainder);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void SetAmount(View view, int id, LiveData<Float> amount) {
        final TextView textView = view.findViewById(id);
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
