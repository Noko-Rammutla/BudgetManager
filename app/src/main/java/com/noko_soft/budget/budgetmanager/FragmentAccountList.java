package com.noko_soft.budget.budgetmanager;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;


public class FragmentAccountList extends Fragment
        implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    ViewModelAccounts viewModelAccounts;
    ListAdapterAccounts listAdapterAccounts;
    private OnFragmentInteractionListener mListener;

    public FragmentAccountList() {
        // Required empty public constructor
    }

    public static FragmentAccountList newInstance() {
        FragmentAccountList fragment = new FragmentAccountList();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account_list, container, false);


        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.addAccount();
            }
        });

        final Button save = view.findViewById(R.id.button_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveClick();
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        listAdapterAccounts = new ListAdapterAccounts(getContext());
        recyclerView.setAdapter(listAdapterAccounts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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
                    mListener.setDifference(aFloat);
                else
                    mListener.setDifference(0.0f);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        Account account = listAdapterAccounts.getItemAt(position);
        if (account != null) {
            viewModelAccounts.delete(account);
        }
    }

    private void SaveClick() {
        List<Account> accounts = listAdapterAccounts.getAccounts();
        for (Account acount: accounts) {
            viewModelAccounts.update(acount);
        }
        Float difference = viewModelAccounts.totalDifference.getValue();
        if (difference != null) {
            Date date = new Date(Calendar.getInstance().getTimeInMillis());
            Transaction transaction = new Transaction("Balance Update", date, -difference, false, false, false);
            viewModelAccounts.insert(transaction);
        }
    }

    public interface OnFragmentInteractionListener {
        void setDifference(float amount);
        void addAccount();
    }
}
