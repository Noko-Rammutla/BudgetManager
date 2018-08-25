package com.noko_soft.budget.budgetmanager;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActivityMain extends AppCompatActivity implements
        FragmentTransactionList.OnFragmentInteractionListener,
        FragmentAccountList.OnFragmentInteractionListener{
    private ViewModelTransactions viewModelTransactions;
    private ViewModelAccounts viewModelAccounts;
    private DrawerLayout mDrawerLayout;

    public static final int NEW_TRANSACATION_REQUEST_CODE = 1;
    public static final int EDIT_TRANSACATION_REQUEST_CODE = 2;
    public static final int NEW_ACCOUNT_REQUEST_CODE = 3;

    private boolean majorDefault = false;
    private boolean budgetDefault = false;
    private boolean recurringDefault = false;
    private Transaction lastTransaction = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);


        viewModelTransactions = ViewModelProviders.of(this,
                new ViewModelTransactions.ViewModelFactory(getApplication(), Calendar.getInstance()))
                .get(ViewModelTransactions.class);
        viewModelAccounts = ViewModelProviders.of(this).get(ViewModelAccounts.class);

        CreateTransactionFragment(viewModelTransactions.Week, viewModelTransactions.WeekTotal);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        mDrawerLayout.closeDrawers();
                        item.setChecked(true);
                        switch (item.getItemId()) {
                            case R.id.menu_MonthRecurring : {
                                CreateTransactionFragment(viewModelTransactions.MonthRecurring, viewModelTransactions.MonthRecurringTotal);
                                SetDefaults(true, true, true);
                                break;
                            }
                            case R.id.menu_MonthFinal : {
                                CreateTransactionFragment(viewModelTransactions.MonthFinal, viewModelTransactions.MonthFinalTotal);
                                SetDefaults(true, false, false);
                                break;
                            }
                            case R.id.menu_MonthAll : {
                                CreateTransactionFragment(viewModelTransactions.MonthBudget, viewModelTransactions.MonthBudgetTotal);
                                SetDefaults(true, true, false);
                                break;
                            }
                            case R.id.menu_MonthNonRecurring : {
                                CreateTransactionFragment(viewModelTransactions.MonthNonRecurring, viewModelTransactions.MonthNonRecurringTotal);
                                SetDefaults(true, true, false);
                                break;
                            }
                            case R.id.menu_Week : {
                                CreateTransactionFragment(viewModelTransactions.Week, viewModelTransactions.WeekTotal);
                                SetDefaults(false, false, false);
                                break;
                            }
                            case R.id.menu_Summary : {
                                Fragment newFragment = FragmentSummary.newInstance();
                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, newFragment);
                                transaction.commit();
                                setTitle("Summary");
                                break;
                            }
                            case R.id.menu_Accounts : {
                                Fragment newFragment = FragmentAccountList.newInstance();
                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, newFragment);
                                transaction.commit();
                                setTitle("Summary");
                                break;
                            }
                        }
                        return true;
                    }
                }
        );

    }

    private void CreateTransactionFragment(LiveData<List<Transaction>> transactions, LiveData<Float> amount) {
        // Create new fragment and transaction
        Fragment newFragment = FragmentTransactionList.newInstance(transactions, amount);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.fragment_container, newFragment);

        // Commit the transaction
        transaction.commit();
    }

    private void SetDefaults(boolean major, boolean budget, boolean recurring) {
        majorDefault = major;
        budgetDefault = budget;
        recurringDefault = recurring;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_TRANSACATION_REQUEST_CODE && resultCode == RESULT_OK) {
            String name = data.getStringExtra(ActivityTransactionEdit.EXTRA_NAME);
            float amount = data.getFloatExtra(ActivityTransactionEdit.EXTRA_AMOUNT, 0);
            boolean major = data.getBooleanExtra(ActivityTransactionEdit.EXTRA_MAJOR, false);
            boolean budget = data.getBooleanExtra(ActivityTransactionEdit.EXTRA_BUDGET, false);
            boolean recurring = data.getBooleanExtra(ActivityTransactionEdit.EXTRA_RECURRING, false);
            Date date = (Date) data.getSerializableExtra(ActivityTransactionEdit.EXTRA_DATE);

            Transaction transaction = new Transaction(name, date, amount, major, recurring, budget);
            viewModelTransactions.insert(transaction);
        } else if (requestCode == EDIT_TRANSACATION_REQUEST_CODE && resultCode == RESULT_OK) {
            String name = data.getStringExtra(ActivityTransactionEdit.EXTRA_NAME);
            float amount = data.getFloatExtra(ActivityTransactionEdit.EXTRA_AMOUNT, 0);
            boolean major = data.getBooleanExtra(ActivityTransactionEdit.EXTRA_MAJOR, false);
            boolean budget = data.getBooleanExtra(ActivityTransactionEdit.EXTRA_BUDGET, false);
            boolean recurring = data.getBooleanExtra(ActivityTransactionEdit.EXTRA_RECURRING, false);
            Date date = (Date) data.getSerializableExtra(ActivityTransactionEdit.EXTRA_DATE);
            if (lastTransaction != null) {
                lastTransaction.name = name;
                lastTransaction.amount = amount;
                lastTransaction.major = major;
                lastTransaction.budget = budget;
                lastTransaction.recurring = recurring;
                lastTransaction.date = date;
                viewModelTransactions.update(lastTransaction);
                lastTransaction = null;
            }
        } else if (requestCode == NEW_ACCOUNT_REQUEST_CODE && resultCode == RESULT_OK) {
            String AccountName = data.getStringExtra(ActivityAccountNew.EXTRA_ACCOUNT_NAME);
            float amount = data.getFloatExtra(ActivityAccountNew.EXTRA_ACCOUNT_BALANCE, 0);

            Account account = new Account(AccountName, amount);
            viewModelAccounts.insert(account);
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
    public void setBalance(float amount) {
        final Resources res = getResources();
        final String MoneyFormat = res.getString(R.string.money_format);
        setTitle("Balance: " + String.format(MoneyFormat, amount));
    }

    @Override
    public void setDifference(float amount) {
        final Resources res = getResources();
        final String MoneyFormat = res.getString(R.string.money_format);
        setTitle("Difference: " + String.format(MoneyFormat, amount));
    }

    @Override
    public void addAccount() {
        Intent intent = new Intent(ActivityMain.this, ActivityAccountNew.class);
        startActivityForResult(intent, NEW_ACCOUNT_REQUEST_CODE);
    }

    @Override
    public void addTransaction() {
        Intent intent = new Intent(ActivityMain.this, ActivityTransactionEdit.class);
        startActivityForResult(intent, NEW_TRANSACATION_REQUEST_CODE);
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
        intent.putExtra(ActivityTransactionEdit.EXTRA_DATE, transaction.date);

        intent.putExtra(ActivityTransactionEdit.EXTRA_MAJOR, transaction.major);
        intent.putExtra(ActivityTransactionEdit.EXTRA_BUDGET, transaction.budget);
        intent.putExtra(ActivityTransactionEdit.EXTRA_RECURRING, transaction.recurring);

        startActivityForResult(intent, EDIT_TRANSACATION_REQUEST_CODE);
    }
}
