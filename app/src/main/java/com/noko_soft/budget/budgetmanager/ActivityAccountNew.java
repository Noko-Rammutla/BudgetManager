package com.noko_soft.budget.budgetmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ActivityAccountNew extends AppCompatActivity {

    public static final String EXTRA_ACCOUNT_NAME = "com.noko_soft.budget.budgetmanager.reply.ACCOUNT_NAME";
    public static final String EXTRA_ACCOUNT_BALANCE = "com.noko_soft.budget.budgetmanager.reply.ACCOUNT_BALANCE";

    EditText mEditAccountName;
    EditText mEditBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_account);

        mEditAccountName = findViewById(R.id.editText_AccountName);
        mEditBalance = findViewById(R.id.edit_amount);

        final Button save = findViewById(R.id.button_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveClick(v);
            }
        });
    }

    public void SaveClick(View v) {
        Intent result = new Intent();
        if (TextUtils.isEmpty(mEditAccountName.getText()) || TextUtils.isEmpty(mEditBalance.getText())) {
            setResult(RESULT_CANCELED, result);
        } else {
            result.putExtra(EXTRA_ACCOUNT_NAME, mEditAccountName.getText().toString());
            float balance = Float.valueOf(mEditBalance.getText().toString());
            result.putExtra(EXTRA_ACCOUNT_BALANCE, balance);
            setResult(RESULT_OK, result);
        }
        finish();
    }
}
