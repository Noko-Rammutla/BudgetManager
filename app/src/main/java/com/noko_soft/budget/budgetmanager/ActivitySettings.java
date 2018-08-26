package com.noko_soft.budget.budgetmanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.text.DateFormat;
import java.util.Calendar;

public class ActivitySettings extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {

    public static final String PREFERANCE_DATE = "com.noko_soft.budget.budgetmanager.reply.PREFERENCE_DATE";
    private Calendar mDate;
    private Button mButtonDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Resources res = getResources();
        setTitle(res.getString(R.string.menu_Settings));

        mButtonDate = findViewById(R.id.button_date);
        mDate = getCurrentDate(this);
        setDate(mDate);

        mButtonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment fragment = new DatePickerFragment();
                fragment.show(getSupportFragmentManager(), "date");
            }
        });
    }

    public static Calendar getCurrentDate(Context context) {
        Calendar calendar = Calendar.getInstance();
        long defualtDate = calendar.getTimeInMillis();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        long longDate = sharedPreferences.getLong(PREFERANCE_DATE, defualtDate);
        if (longDate == defualtDate) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(PREFERANCE_DATE, longDate);
            editor.apply();
        }
        calendar.setTimeInMillis(longDate);
        return calendar;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        mDate.set(year, month, day);
        setDate(mDate);
    }

    public void setDate(Calendar cal) {
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        Resources res = getResources();
        String text = res.getString(R.string.hint_start_date, dateFormat.format(mDate.getTime()));
        mButtonDate.setText(text);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        long longDate = cal.getTimeInMillis();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(PREFERANCE_DATE, longDate);
        editor.apply();
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
