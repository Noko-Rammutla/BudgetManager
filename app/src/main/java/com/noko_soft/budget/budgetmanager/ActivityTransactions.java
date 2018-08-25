package com.noko_soft.budget.budgetmanager;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActivityTransactions extends AppCompatActivity implements
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{
    private ViewModelTransactions mViewModel;
    private ListAdapterTransactions mAdapter;
    private DrawerLayout mDrawerLayout;

    public static final int NEW_TRANSACATION_REQUEST_CODE = 1;
    public static final int EDIT_TRANSACATION_REQUEST_CODE = 2;

    private MediatorLiveData<List<Transaction>> transactionsMediator;
    private LiveData<List<Transaction>> transactionsLiveData;
    private MediatorLiveData<Float> amountMediator;
    private LiveData<Float> amountLiveData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityTransactions.this, ActivityTransactionEdit.class);
                startActivityForResult(intent, NEW_TRANSACATION_REQUEST_CODE);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        mAdapter = new ListAdapterTransactions(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mViewModel = ViewModelProviders.of(this,
                new ViewModelTransactions.ViewModelFactory(getApplication(), Calendar.getInstance()))
                .get(ViewModelTransactions.class);

        transactionsMediator = new MediatorLiveData<>();
        amountMediator = new MediatorLiveData<>();
        ChangeDataSet(mViewModel.Week, mViewModel.WeekTotal);

        transactionsMediator.observe(this, new Observer<List<Transaction>>() {
            @Override
            public void onChanged(@Nullable List<Transaction> transactions) {
                mAdapter.setTransactions(transactions);
            }
        });
        amountMediator.observe(this, new Observer<Float>() {
            @Override
            public void onChanged(@Nullable Float aFloat) {
                final Resources res = getResources();
                final String MoneyFormat = res.getString(R.string.money_format);
                if (aFloat != null)
                    setTitle("Balance: " + String.format(MoneyFormat, aFloat));
                else
                    setTitle("Balance: " + String.format(MoneyFormat, 0.0));
            }
        });

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        mDrawerLayout.closeDrawers();
                        switch (item.getItemId()) {
                            case R.id.menu_MonthRecurring : {
                                ChangeDataSet(mViewModel.MonthRecurring, mViewModel.MonthRecurringTotal);
                                item.setChecked(true);
                                break;
                            }
                            case R.id.menu_MonthFinal : {
                                ChangeDataSet(mViewModel.MonthFinal, mViewModel.MonthFinalTotal);
                                item.setChecked(true);
                                break;
                            }
                            case R.id.menu_MonthAll : {
                                ChangeDataSet(mViewModel.MonthBudget, mViewModel.MonthBudgetTotal);
                                item.setChecked(true);
                                break;
                            }
                            case R.id.menu_MonthNonRecurring : {
                                ChangeDataSet(mViewModel.MonthNonRecurring, mViewModel.MonthNonRecurringTotal);
                                item.setChecked(true);
                                break;
                            }
                            case R.id.menu_Week : {
                                ChangeDataSet(mViewModel.Week, mViewModel.WeekTotal);
                                item.setChecked(true);
                                break;
                            }
                            case R.id.menu_Summary : {
                                Intent intent = new Intent(ActivityTransactions.this, ActivitySummary.class);
                                startActivity(intent);
                                break;
                            }
                            case R.id.menu_Accounts : {
                                Intent intent = new Intent(ActivityTransactions.this, ActivityAccounts.class);
                                startActivity(intent);
                                break;
                            }
                        }
                        return true;
                    }
                }
        );

    }

    private void ChangeDataSet(LiveData<List<Transaction>> transactions, LiveData<Float> amount) {
        if (transactionsLiveData != null)
            transactionsMediator.removeSource(transactionsLiveData);
        transactionsMediator.addSource(transactions, new Observer<List<Transaction>>() {
            @Override
            public void onChanged(@Nullable List<Transaction> lstTransactions) {
                transactionsMediator.setValue(lstTransactions);
            }
        });
        transactionsLiveData = transactions;

        if (amountLiveData != null)
            amountMediator.removeSource(amountLiveData);
        amountMediator.addSource(amount, new Observer<Float>() {
            @Override
            public void onChanged(@Nullable Float aFloat) {
                amountMediator.setValue(aFloat);
            }
        });
        amountLiveData = amount;
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
            mViewModel.insert(transaction);
        } else if (requestCode == EDIT_TRANSACATION_REQUEST_CODE && resultCode == RESULT_OK) {
            String name = data.getStringExtra(ActivityTransactionEdit.EXTRA_NAME);
            float amount = data.getFloatExtra(ActivityTransactionEdit.EXTRA_AMOUNT, 0);
            boolean major = data.getBooleanExtra(ActivityTransactionEdit.EXTRA_MAJOR, false);
            boolean budget = data.getBooleanExtra(ActivityTransactionEdit.EXTRA_BUDGET, false);
            boolean recurring = data.getBooleanExtra(ActivityTransactionEdit.EXTRA_RECURRING, false);
            int position = data.getIntExtra(ActivityTransactionEdit.EXTRA_POSITION, -1);
            Date date = (Date) data.getSerializableExtra(ActivityTransactionEdit.EXTRA_DATE);
            Transaction transaction = mAdapter.getItemAt(position);
            if (transaction != null) {
                transaction.name = name;
                transaction.amount = amount;
                transaction.major = major;
                transaction.budget = budget;
                transaction.recurring = recurring;
                transaction.date = date;
                mViewModel.update(transaction);
            }

        }
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ListAdapterTransactions.TransactionViewHolder) {
            if (direction == ItemTouchHelper.LEFT) {
                // delete item
                Transaction transaction = mAdapter.getItemAt(position);
                if (transaction != null)
                    mViewModel.delete(transaction);
            } else if (direction == ItemTouchHelper.RIGHT) {
                Intent intent = new Intent(ActivityTransactions.this, ActivityTransactionEdit.class);

                Transaction transaction = mAdapter.getItemAt(position);

                intent.putExtra(ActivityTransactionEdit.EXTRA_NAME, transaction.name);
                intent.putExtra(ActivityTransactionEdit.EXTRA_AMOUNT, transaction.amount);
                intent.putExtra(ActivityTransactionEdit.EXTRA_MAJOR, transaction.major);
                intent.putExtra(ActivityTransactionEdit.EXTRA_BUDGET, transaction.budget);
                intent.putExtra(ActivityTransactionEdit.EXTRA_RECURRING, transaction.recurring);
                intent.putExtra(ActivityTransactionEdit.EXTRA_DATE, transaction.date);
                intent.putExtra(ActivityTransactionEdit.EXTRA_POSITION, position);

                startActivityForResult(intent, EDIT_TRANSACATION_REQUEST_CODE);
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
}
