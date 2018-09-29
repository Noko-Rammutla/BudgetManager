package com.noko_soft.budget.budgetmanager;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Date;
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

        final RepoTransactions repository = new RepoTransactions(getActivity().getApplication());

        Calendar cal = Calendar.getInstance();
        Date today = new Date(cal.getTimeInMillis());
        cal.add(Calendar.DAY_OF_MONTH, 14);
        Date endDate = new Date(cal.getTimeInMillis());

        LiveData<Float> RecurringIncome = repository.getTotalPositive(today, true);
        LiveData<Float> RecurringExpenses = repository.getTotalNegative(today, true);
        LiveData<Float> RecurringTotal = new LiveDataSum(RecurringIncome, RecurringExpenses);
        LiveData<Float> Income = repository.getTotalPositive(today, false);
        LiveData<Float> Expenses = repository.getTotalNegative(today, false);
        LiveData<Float> Total = new LiveDataSum(Income, Expenses);
        final LiveData<Float> Balance = new LiveDataSum(RecurringTotal, Total);
        LiveData<Float> FutureTotalIncome = repository.getTotalPositive(endDate);
        LiveData<Float> FutureTotalExpenses = repository.getTotalNegative(endDate);
        LiveData<Float> FutureIncome = new LiveDataDifference(FutureTotalIncome, new LiveDataSum(RecurringIncome, Income));
        LiveData<Float> FutureExpenses = new LiveDataDifference(FutureTotalExpenses, new LiveDataSum(RecurringExpenses, Expenses));
        LiveData<Float> FutureTotal = new LiveDataSum(FutureIncome, FutureExpenses);
        LiveData<Float> FutureBalance = new LiveDataSum(Balance, FutureTotal);

        SetAmount(view, R.id.textView_RecurringIncome, RecurringIncome);
        SetAmount(view, R.id.textView_DebitOrders, RecurringExpenses);
        SetAmount(view, R.id.textView_RecurringTotal, RecurringTotal);
        SetAmount(view, R.id.textView_Income, Income);
        SetAmount(view, R.id.textView_Expenses, Expenses);
        SetAmount(view, R.id.textView_Total, Total);
        SetAmount(view, R.id.textView_Balance, Balance);
        SetAmount(view, R.id.textView_futureIncome, FutureIncome);
        SetAmount(view, R.id.textView_futureExpenses, FutureExpenses);
        SetAmount(view, R.id.textView_futureTotal, FutureTotal);
        SetAmount(view, R.id.textView_futureBalance, FutureBalance);

        Button mRecordBalanceButton = view.findViewById(R.id.button_record_balance);
        mRecordBalanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.record_balance);

                // Set up the input
                final EditText input = new EditText(getContext());

                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = input.getText().toString();
                        if (text != null && text.length() != 0) {
                            float newBalance = Float.parseFloat(text);
                            Float balance = Balance.getValue();
                            if (balance != null)
                            {
                                Date now = new Date(Calendar.getInstance().getTimeInMillis());

                                repository.insert(new Transaction("Record Balance", now, newBalance - balance, false));
                            }
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

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
