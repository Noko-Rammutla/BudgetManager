package com.noko_soft.budget.budgetmanager;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.sql.Date;
import java.util.List;

public class ActivityMain extends AppCompatActivity implements
        FragmentTransactionList.OnFragmentInteractionListener{
    private ViewModelTransactions viewModelTransactions;
    private DrawerLayout mDrawerLayout;

    public static final int NEW_TRANSACTION_REQUEST_CODE = 1;
    public static final int EDIT_TRANSACTION_REQUEST_CODE = 2;

    private Transaction lastTransaction = null;
    private boolean defaultRecurring = false;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        CreateTransactionFragment(R.string.menu_Transactions, viewModelTransactions.Transactions);
        defaultRecurring = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        viewModelTransactions = ViewModelProviders.of(this).get(ViewModelTransactions.class);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        mDrawerLayout.closeDrawers();
                        item.setChecked(true);
                        switch (item.getItemId()) {
                            case R.id.menu_Transactions : {
                                CreateTransactionFragment(R.string.menu_Transactions, viewModelTransactions.Transactions);
                                defaultRecurring = false;
                                break;
                            }
                            case R.id.menu_Recurring : {
                                CreateTransactionFragment(R.string.menu_Recurring, viewModelTransactions.DebitOrders);
                                defaultRecurring = true;
                                break;
                            }
                            case R.id.menu_Summary : {
                                Fragment newFragment = FragmentSummary.newInstance();
                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, newFragment);
                                transaction.commit();
                                setTitle(R.string.menu_Summary);
                                break;
                            }
                        }
                        return true;
                    }
                }
        );

    }

    private void CreateTransactionFragment(int stringID, LiveData<List<Transaction>> transactions) {
        setTitle(stringID);

        // Create new fragment and transaction
        Fragment newFragment = FragmentTransactionList.newInstance(transactions);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.fragment_container, newFragment);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_TRANSACTION_REQUEST_CODE && resultCode == RESULT_OK) {
            String name = data.getStringExtra(ActivityTransactionEdit.EXTRA_NAME);
            float amount = data.getFloatExtra(ActivityTransactionEdit.EXTRA_AMOUNT, 0);
            Date date = (Date) data.getSerializableExtra(ActivityTransactionEdit.EXTRA_DATE);

            Transaction transaction = new Transaction(name, date, amount, defaultRecurring);
            viewModelTransactions.insert(transaction);
        } else if (requestCode == EDIT_TRANSACTION_REQUEST_CODE && resultCode == RESULT_OK) {
            String name = data.getStringExtra(ActivityTransactionEdit.EXTRA_NAME);
            float amount = data.getFloatExtra(ActivityTransactionEdit.EXTRA_AMOUNT, 0);
            Date date = (Date) data.getSerializableExtra(ActivityTransactionEdit.EXTRA_DATE);
            if (lastTransaction != null) {
                lastTransaction.name = name;
                lastTransaction.amount = amount;
                lastTransaction.timestamp = date;
                viewModelTransactions.update(lastTransaction);

                lastTransaction = null;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void addTransaction() {
        Intent intent = new Intent(ActivityMain.this, ActivityTransactionEdit.class);
        startActivityForResult(intent, NEW_TRANSACTION_REQUEST_CODE);
    }

    @Override
    public void deleteTransaction(Transaction transaction) {
        viewModelTransactions.delete(transaction);
    }

    @Override
    public void editTransaction(Transaction transaction) {
        lastTransaction = transaction;
        Intent intent = new Intent(ActivityMain.this, ActivityTransactionEdit.class);

        intent.putExtra(ActivityTransactionEdit.EXTRA_NAME, transaction.name);
        intent.putExtra(ActivityTransactionEdit.EXTRA_AMOUNT, transaction.amount);
        intent.putExtra(ActivityTransactionEdit.EXTRA_DATE, transaction.timestamp);

        startActivityForResult(intent, EDIT_TRANSACTION_REQUEST_CODE);
    }
}
