package com.noko_soft.budget.budgetmanager;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.util.List;

public class ActivityMain extends AppCompatActivity implements
        FragmentTransactionList.OnFragmentInteractionListener{
    private ViewModelTransactions viewModelTransactions;
    private DrawerLayout mDrawerLayout;

    public static final int NEW_TRANSACTION_REQUEST_CODE = 1;
    public static final int EDIT_TRANSACTION_REQUEST_CODE = 2;
    public static final int EXPORT_JSON_REQUEST_CODE = 4;
    public static final int IMPORT_JSON_REQUEST_CODE = 5;

    private Transaction lastTransaction = null;
    private Transaction deletedTransaction = null;
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
        if (actionbar != null)
        {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

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
                            case R.id.menu_ExportJSON : {
                                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                                intent.setType("*/*");
                                startActivityForResult(intent, EXPORT_JSON_REQUEST_CODE);
                                break;
                            }
                            case R.id.menu_ImportJSON : {
                                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                intent.setType("*/*");
                                startActivityForResult(intent, IMPORT_JSON_REQUEST_CODE);
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
        } else if (requestCode == EXPORT_JSON_REQUEST_CODE && resultCode == RESULT_OK) {
            ExportJsonAsyncTask exportJsonAsyncTask = new ExportJsonAsyncTask();
            exportJsonAsyncTask.execute(data.getData());
        } else if (requestCode == IMPORT_JSON_REQUEST_CODE && resultCode == RESULT_OK) {
            ImportJsonAsyncTask importJsonAsyncTask = new ImportJsonAsyncTask();
            importJsonAsyncTask.execute(data.getData());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_Undo:
                undoDelete();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_menu, menu);
        return true;
    }

    public void undoDelete() {
        if (deletedTransaction != null) {
            viewModelTransactions.insert(deletedTransaction);
            deletedTransaction = null;
        } else {
            Snackbar.make(findViewById(R.id.main_coordinator), R.string.snackbar_nodelete,
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void addTransaction() {
        Intent intent = new Intent(ActivityMain.this, ActivityTransactionEdit.class);
        startActivityForResult(intent, NEW_TRANSACTION_REQUEST_CODE);
    }

    @Override
    public void deleteTransaction(Transaction transaction) {
        deletedTransaction = transaction;
        viewModelTransactions.delete(transaction);
        Snackbar undo = Snackbar.make(findViewById(R.id.main_coordinator), R.string.snackbar_delete,
                Snackbar.LENGTH_LONG);
        undo.setAction(R.string.action_undo, new UndoListener());
        undo.show();
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

    class UndoListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            undoDelete();
        }
    }

    class ExportJsonAsyncTask extends AsyncTask<Uri, Void, Void> {
        @Override
        protected Void doInBackground(final Uri ... uris) {
            for (Uri uri: uris) {

                try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                    JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    jsonWriter.setIndent("  ");

                    DaoTransactions daoTransactions = BudgetManagerRoomDatabase
                            .getDatabase(getApplicationContext()).transactionDao();
                    jsonWriter.beginArray();
                    for (Transaction transaction : daoTransactions.dumpAll()){
                        jsonWriter.beginObject();

                        jsonWriter.name("id").value(transaction.id);
                        jsonWriter.name("name").value(transaction.name);
                        jsonWriter.name("timestamp").value(transaction.timestamp.getTime());
                        jsonWriter.name("amount").value(transaction.amount);
                        jsonWriter.name("recurring").value(transaction.recurring);
                        jsonWriter.name("archived").value(transaction.archived);
                        jsonWriter.endObject();
                    }
                    jsonWriter.endArray();

                    jsonWriter.close();
                } catch (IOException e) {
                    Snackbar.make(findViewById(R.id.main_coordinator), R.string.snackbar_CannotCreateFile,
                            Snackbar.LENGTH_SHORT).show();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Snackbar.make(findViewById(R.id.main_coordinator), R.string.snackbar_done,
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    class ImportJsonAsyncTask extends AsyncTask<Uri, Void, Void> {
        boolean successful = true;

        @Override
        protected Void doInBackground(final Uri ... uris) {
            for (Uri uri: uris) {

                try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
                    JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));

                    DaoTransactions daoTransactions = BudgetManagerRoomDatabase
                            .getDatabase(getApplicationContext()).transactionDao();
                    jsonReader.beginArray();
                    while (jsonReader.hasNext()) {
                        jsonReader.beginObject();
                        String name = "";
                        Date date = null;
                        boolean recurring = false;
                        boolean archived = false;
                        float amount = 0;
                        while (jsonReader.hasNext()) {
                            String key = jsonReader.nextName();

                            if (key.equals("name")) {
                                name = jsonReader.nextString();
                            } else if (key.equals("amount")) {
                                amount = (float) jsonReader.nextDouble();
                            } else if (key.equals("recurring")) {
                                recurring = jsonReader.nextBoolean();
                            } else if (key.equals("archived")) {
                                archived = jsonReader.nextBoolean();
                            } else if (key.equals("timestamp")) {
                                date = new Date(jsonReader.nextLong());
                            } else {
                                jsonReader.skipValue();
                            }
                        }
                        if (date != null && !name.equals("")) {
                            Transaction transaction = new Transaction(name, date, amount, recurring);
                            transaction.archived = archived;
                            daoTransactions.InsertTransactions(transaction);
                        }

                        jsonReader.endObject();
                    }
                    jsonReader.endArray();
                    jsonReader.close();
                } catch (IOException e) {
                    Snackbar.make(findViewById(R.id.main_coordinator), R.string.snackbar_CannotReadFile,
                            Snackbar.LENGTH_SHORT).show();
                } catch (Exception e) {
                    successful = false;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (successful) {
                Snackbar.make(findViewById(R.id.main_coordinator), R.string.snackbar_done,
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(findViewById(R.id.main_coordinator), R.string.snackbar_InvalidInputFile,
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
