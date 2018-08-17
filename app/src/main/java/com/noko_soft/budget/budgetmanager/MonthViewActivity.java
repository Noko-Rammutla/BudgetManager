package com.noko_soft.budget.budgetmanager;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MonthViewActivity extends AppCompatActivity implements
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{
    private TransactionViewModel mViewModel;
    private MonthAllAdapter mAdapter;
    private DrawerLayout mDrawerLayout;

    public static final int NEW_TRANSACATION_REQUEST_CODE = 1;
    public static final int EDIT_TRANSACATION_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MonthViewActivity.this, NewTransactionActivity.class);
                startActivityForResult(intent, NEW_TRANSACATION_REQUEST_CODE);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        mAdapter = new MonthAllAdapter(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mViewModel = ViewModelProviders.of(this,
                new TransactionViewModel.ViewModelFactory(getApplication(), Calendar.getInstance()))
                .get(TransactionViewModel.class);
        mViewModel.getAll().observe(this, new Observer<List<Transaction>>() {
            @Override
            public void onChanged(@Nullable List<Transaction> transactions) {
                mAdapter.setTransactions(transactions);
            }
        });

        final Observer<Float> totalObsever = new Observer<Float>() {
            @Override
            public void onChanged(@Nullable Float aFloat) {
                setTitle("Balance: " + aFloat.toString());
            }
        };
        mViewModel.getMonthTotal().observe(this, totalObsever);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                }
        );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_TRANSACATION_REQUEST_CODE && resultCode == RESULT_OK) {
            String name = data.getStringExtra(NewTransactionActivity.EXTRA_NAME);
            float amount = data.getFloatExtra(NewTransactionActivity.EXTRA_AMOUNT, 0);
            boolean major = data.getBooleanExtra(NewTransactionActivity.EXTRA_MAJOR, false);
            boolean budget = data.getBooleanExtra(NewTransactionActivity.EXTRA_BUDGET, false);
            boolean recurring = data.getBooleanExtra(NewTransactionActivity.EXTRA_RECURRING, false);
            Date date = (Date) data.getSerializableExtra(NewTransactionActivity.EXTRA_DATE);

            Transaction transaction = new Transaction(name, date, amount, major, recurring, budget);
            mViewModel.insert(transaction);
        } else if (requestCode == EDIT_TRANSACATION_REQUEST_CODE && resultCode == RESULT_OK) {
            String name = data.getStringExtra(NewTransactionActivity.EXTRA_NAME);
            float amount = data.getFloatExtra(NewTransactionActivity.EXTRA_AMOUNT, 0);
            boolean major = data.getBooleanExtra(NewTransactionActivity.EXTRA_MAJOR, false);
            boolean budget = data.getBooleanExtra(NewTransactionActivity.EXTRA_BUDGET, false);
            boolean recurring = data.getBooleanExtra(NewTransactionActivity.EXTRA_RECURRING, false);
            int position = data.getIntExtra(NewTransactionActivity.EXTRA_POSITION, -1);
            Date date = (Date) data.getSerializableExtra(NewTransactionActivity.EXTRA_DATE);
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
        if (viewHolder instanceof MonthAllAdapter.TransactionViewHolder) {
            if (direction == ItemTouchHelper.LEFT) {
                // delete item
                Transaction transaction = mAdapter.getItemAt(position);
                if (transaction != null)
                    mViewModel.delete(transaction);
            } else if (direction == ItemTouchHelper.RIGHT) {
                Intent intent = new Intent(MonthViewActivity.this, NewTransactionActivity.class);

                Transaction transaction = mAdapter.getItemAt(position);

                intent.putExtra(NewTransactionActivity.EXTRA_NAME, transaction.name);
                intent.putExtra(NewTransactionActivity.EXTRA_AMOUNT, transaction.amount);
                intent.putExtra(NewTransactionActivity.EXTRA_MAJOR, transaction.major);
                intent.putExtra(NewTransactionActivity.EXTRA_BUDGET, transaction.budget);
                intent.putExtra(NewTransactionActivity.EXTRA_RECURRING, transaction.recurring);
                intent.putExtra(NewTransactionActivity.EXTRA_DATE, transaction.date);
                intent.putExtra(NewTransactionActivity.EXTRA_POSITION, position);

                startActivityForResult(intent, EDIT_TRANSACATION_REQUEST_CODE);
            }
        }
    }
}
