package com.noko_soft.budget.budgetmanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class NewTransactionActivity extends AppCompatActivity
    implements DatePickerDialog.OnDateSetListener {

    public static final String EXTRA_NAME = "com.noko_soft.budget.budgetmanager.reply.NAME";
    public static final String EXTRA_DATE = "com.noko_soft.budget.budgetmanager.reply.DATE";
    public static final String EXTRA_AMOUNT = "com.noko_soft.budget.budgetmanager.reply.AMOUNT";
    public static final String EXTRA_MAJOR = "com.noko_soft.budget.budgetmanager.reply.MAJOR";
    public static final String EXTRA_RECURRING = "com.noko_soft.budget.budgetmanager.reply.RECURRING";
    public static final String EXTRA_BUDGET = "com.noko_soft.budget.budgetmanager.reply.BUDGET";
    public static final String EXTRA_POSITION = "com.noko_soft.budget.budgetmanager.reply.POSITION";

    private EditText mEditName;
    private EditText mEditAmount;
    private Switch mSwitchMajor;
    private Switch mSwitchRecurring;
    private Switch mSwitchBudget;
    private Calendar mDate;
    private Button mButtonDate;
    private int mPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transaction);
        mEditName = findViewById(R.id.edit_name);
        mEditAmount = findViewById(R.id.edit_amount);
        mSwitchMajor = findViewById(R.id.switch_major);
        mSwitchBudget = findViewById(R.id.switch_budget);
        mSwitchRecurring = findViewById(R.id.switch_recurring);
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
                    boolean major = mSwitchMajor.isChecked();
                    boolean budget = mSwitchBudget.isChecked();
                    boolean recurring = mSwitchRecurring.isChecked();

                    replyIntent.putExtra(EXTRA_NAME, name);
                    replyIntent.putExtra(EXTRA_AMOUNT, amount);
                    replyIntent.putExtra(EXTRA_MAJOR, major);
                    replyIntent.putExtra(EXTRA_BUDGET, budget);
                    replyIntent.putExtra(EXTRA_RECURRING, recurring);
                    replyIntent.putExtra(EXTRA_DATE, mDate.getTime());
                    replyIntent.putExtra(EXTRA_POSITION, mPosition);
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });

        mButtonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragmenet fragmenet = new DatePickerFragmenet();
                fragmenet.show(getSupportFragmentManager(), "date");
            }
        });
        
        Intent intent = getIntent();
        String name = intent.getStringExtra(EXTRA_NAME);
        if (name != null)
            mEditName.setText(name);
        if (intent.hasExtra(EXTRA_AMOUNT)) {
            float amount = intent.getFloatExtra(EXTRA_AMOUNT, 0);
            mEditAmount.setText(Float.toString(amount));
        }

        boolean major = intent.getBooleanExtra(EXTRA_MAJOR, false);
        boolean budget = intent.getBooleanExtra(EXTRA_BUDGET, false);
        boolean recurring = intent.getBooleanExtra(EXTRA_RECURRING, false);
        Date date = (Date) intent.getSerializableExtra(EXTRA_DATE);
        mPosition = intent.getIntExtra(EXTRA_POSITION, 0);
        if (date == null)
            date = Calendar.getInstance().getTime();

        mDate = Calendar.getInstance();
        mDate.setTime(date);
        setDate(mDate);

        mSwitchMajor.setChecked(major);
        mSwitchBudget.setChecked(budget);
        mSwitchRecurring.setChecked(recurring);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        mDate.set(year, month, day);
        setDate(mDate);
    }

    public void setDate(Calendar cal) {
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        Resources res = getResources();
        String text = res.getString(R.string.hint_date, dateFormat.format(mDate.getTime()));
        mButtonDate.setText(text);
    }

    public static class DatePickerFragmenet extends DialogFragment {

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
