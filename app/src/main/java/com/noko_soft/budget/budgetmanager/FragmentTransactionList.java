package com.noko_soft.budget.budgetmanager;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public class FragmentTransactionList extends Fragment
    implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{

    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private ListAdapterTransactions mAdapter;

    private LiveData<List<Transaction>> transactionsLiveData;
    private LiveData<Float> amountLiveData;

    private OnFragmentInteractionListener mListener;

    public FragmentTransactionList() {
        // Required empty public constructor
        transactionsLiveData = null;
        amountLiveData = null;
    }

    public static FragmentTransactionList newInstance(LiveData<List<Transaction>> transactions, LiveData<Float> amount) {
        FragmentTransactionList fragment = new FragmentTransactionList();
        fragment.transactionsLiveData = transactions;
        fragment.amountLiveData = amount;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction_list, container, false);

        floatingActionButton = view.findViewById(R.id.fab);
        recyclerView = view.findViewById(R.id.recyclerview);

        final Context context = getContext();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.addTransaction();
            }
        });

        mAdapter = new ListAdapterTransactions(context);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        if (transactionsLiveData != null) {
            transactionsLiveData.observe(this, new Observer<List<Transaction>>() {
                @Override
                public void onChanged(@Nullable List<Transaction> transactions) {
                    mAdapter.setTransactions(transactions);
                }
            });
        };
        if (amountLiveData != null) {
            amountLiveData.observe(this, new Observer<Float>() {
                @Override
                public void onChanged(@Nullable Float aFloat) {
                    if (aFloat != null)
                        mListener.setBalance(aFloat);
                    else
                        mListener.setBalance(0.0f);
                }
            });
        }

        List<Transaction> transactions = transactionsLiveData.getValue();

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        return view;
    }

    @Override
    public void onAttach(final Context context) {
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
        if (viewHolder instanceof ListAdapterTransactions.TransactionViewHolder) {
            if (direction == ItemTouchHelper.LEFT) {
                // delete item
                Transaction transaction = mAdapter.getItemAt(position);
                if (transaction != null)
                    mListener.deleteTransaction(transaction);
            } else if (direction == ItemTouchHelper.RIGHT) {
                // edit item
                Transaction transaction = mAdapter.getItemAt(position);
                if (transaction != null)
                    mListener.editTransaction(transaction);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public interface OnFragmentInteractionListener {
        void setBalance(float amount);
        void addTransaction();
        void deleteTransaction(Transaction transaction);
        void editTransaction(Transaction transaction);

    }
}
