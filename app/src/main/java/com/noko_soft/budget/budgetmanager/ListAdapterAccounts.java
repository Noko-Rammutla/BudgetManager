package com.noko_soft.budget.budgetmanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class ListAdapterAccounts extends RecyclerView.Adapter<ListAdapterAccounts.AccountViewHolder>{
    class AccountViewHolder extends RecyclerView.ViewHolder
        implements ViewHolderCanSwipe{
        private final TextView mTextViewAccountName;
        private final EditText mEditAccountBalance;
        public LinearLayout viewForeground;
        public RelativeLayout viewBackground;

        AccountViewHolder(View itemView) {
            super(itemView);
            mTextViewAccountName = itemView.findViewById(R.id.textview_account_name);
            mEditAccountBalance = itemView.findViewById(R.id.edit_account_balance);
            viewForeground = itemView.findViewById(R.id.view_foreground);
            viewBackground = itemView.findViewById(R.id.view_background);
        }

        @Override
        public View GetForeground() {
            return viewForeground;
        }

        @Override
        public View GetBackground() {
            return viewBackground;
        }
    }

    private final LayoutInflater mInflater;
    private List<Account> accounts;

    ListAdapterAccounts(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.view_item_account, parent, false);
        return new AccountViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final AccountViewHolder holder, int position) {
        if (accounts != null) {
            final Account current = accounts.get(position);
            holder.mTextViewAccountName.setText(current.name);
            holder.mEditAccountBalance.setText(Float.toString(current.amount));
            holder.mEditAccountBalance.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!(TextUtils.isEmpty(s))) {
                        float value = Float.parseFloat(s.toString());
                        current.amount = value;
                    } else {
                        holder.mEditAccountBalance.setText(Float.toString(current.amount));
                    }
                }
            });
        }
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (accounts == null) {
            return 0;
        } else {
            return accounts.size();
        }
    }

    public Account getItemAt(int position) {
        if (position >= 0 && position < getItemCount()) {
            return accounts.get(position);
        }
        return null;
    }

    public List<Account> getAccounts() {
        return accounts;
    }
}
