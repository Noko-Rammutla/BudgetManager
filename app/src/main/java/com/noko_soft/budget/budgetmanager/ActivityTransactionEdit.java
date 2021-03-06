package com.noko_soft.budget.budgetmanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;

import java.sql.Date;
import java.text.DateFormat;
import java.util.Calendar;

public class ActivityTransactionEdit extends AppCompatActivity
    implements DatePickerDialog.OnDateSetListener {

    public static final String EXTRA_NAME = "com.noko_soft.budget.budgetmanager.reply.NAME";
    public static final String EXTRA_DATE = "com.noko_soft.budget.budgetmanager.reply.DATE";
    public static final String EXTRA_AMOUNT = "com.noko_soft.budget.budgetmanager.reply.AMOUNT";

    private EditText mEditName;
    private EditText mEditAmount;
    private Switch mSwitchIncome;
    private Calendar mDate;
    private Button mButtonDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transaction);
        mEditName = findViewById(R.id.edit_name);
        mEditAmount = findViewById(R.id.edit_amount);
        mSwitchIncome = findViewById(R.id.switch_income);
        mButtonDate = findViewById(R.id.button_date);

        final Button save = findViewById(R.id.button_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(mEditName.getText()) || TextUtils.isEmpty(mEditAmount.getText())) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    String name = mEditName.getText().toString();
                    float amount = Float.valueOf(mEditAmount.getText().toString());
                    boolean income = mSwitchIncome.isChecked();
                    if (!income)
                    {
                        amount = -amount;
                    }

                    replyIntent.putExtra(EXTRA_NAME, name);
                    replyIntent.putExtra(EXTRA_AMOUNT, amount);
                    replyIntent.putExtra(EXTRA_DATE, new Date(mDate.getTimeInMillis()));
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });

        mButtonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment fragment = new DatePickerFragment();
                fragment.show(getSupportFragmentManager(), "timestamp");
            }
        });
        
        Intent intent = getIntent();
        String name = intent.getStringExtra(EXTRA_NAME);
        if (name != null)
            mEditName.setText(name);
        if (intent.hasExtra(EXTRA_AMOUNT)) {
            float amount = intent.getFloatExtra(EXTRA_AMOUNT, 0);
            if (amount < 0) {
                amount = - amount;
            } else {
                mSwitchIncome.setChecked(true);
            }
            mEditAmount.setText(Float.toString(amount));
        }

        Date date = (Date) intent.getSerializableExtra(EXTRA_DATE);
        if (date == null)
            date = new Date(Calendar.getInstance().getTimeInMillis());

        mDate = Calendar.getInstance();
        mDate.setTime(date);
        setDate(mDate);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        mDate.set(year, month, day);
        setDate(mDate);
    }

    public void setDate(Calendar cal) {
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        Resources res = getResources();
        String text = res.getString(R.string.hint_date, dateFormat.format(cal.getTime()));
        mButtonDate.setText(text);
    }

    public static class DatePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle saveInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(),
                    (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
        }
    }
}
