package com.noko_soft.budget.budgetmanager;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.noko_soft.budget.budgetmanager.R;

import java.util.Calendar;
import java.util.List;

public class ActivityAccounts extends AppCompatActivity implements
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{

    private final int NEW_ACOUNT_REQUEST_CODE = 1;

    ViewModelAccounts viewModelAccounts;
    ListAdapterAccounts listAdapterAccounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityAccounts.this, ActivityAccountNew.class);
                startActivityForResult(intent, NEW_ACOUNT_REQUEST_CODE);
            }
        });

        final Button save = findViewById(R.id.button_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveClick();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        listAdapterAccounts = new ListAdapterAccounts(this);
        recyclerView.setAdapter(listAdapterAccounts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModelAccounts = ViewModelProviders.of(this).get(ViewModelAccounts.class);
        viewModelAccounts.accounts.observe(this, new Observer<List<Account>>() {
            @Override
            public void onChanged(@Nullable List<Account> accounts) {
                listAdapterAccounts.setAccounts(accounts);
            }
        });

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        viewModelAccounts.totalDifference.observe(this, new Observer<Float>() {
            @Override
            public void onChanged(@Nullable Float aFloat) {
                final Resources res = getResources();
                final String MoneyFormat = res.getString(R.string.money_format);
                if (aFloat != null)
                    setTitle("Difference: " + String.format(MoneyFormat, aFloat));
                else
                    setTitle("Difference: " + String.format(MoneyFormat, 0.0));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_ACOUNT_REQUEST_CODE && resultCode == RESULT_OK) {
            String AccountName = data.getStringExtra(ActivityAccountNew.EXTRA_ACCOUNT_NAME);
            float amount = data.getFloatExtra(ActivityAccountNew.EXTRA_ACCOUNT_BALANCE, 0);

            Account account = new Account(AccountName, amount);
            viewModelAccounts.insert(account);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        Account account = listAdapterAccounts.getItemAt(position);
        if (account != null) {
            viewModelAccounts.delete(account);
        }
    }

    public void SaveClick() {
        List<Account> accounts = listAdapterAccounts.getAccounts();
        for (Account acount: accounts) {
            viewModelAccounts.update(acount);
        }
        Float difference = viewModelAccounts.totalDifference.getValue();
        if (difference != null) {
            Transaction transaction = new Transaction("Balance Update", Calendar.getInstance().getTime(), -difference, false, false, false);
            viewModelAccounts.insert(transaction);
        }
    }
}
