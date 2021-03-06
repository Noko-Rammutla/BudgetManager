package com.noko_soft.budget.budgetmanager;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.sql.Date;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ListAdapterTransactions extends RecyclerView.Adapter<ListAdapterTransactions.TransactionViewHolder> {
    class TransactionViewHolder extends RecyclerView.ViewHolder
        implements ViewHolderCanSwipe{
        private final TextView titleItemView;
        private final TextView detailItemView;
        public LinearLayout viewForeground;
        public RelativeLayout viewBackground;

        private TransactionViewHolder(View itemView) {
            super(itemView);
            titleItemView = itemView.findViewById(R.id.textview_title);
            detailItemView = itemView.findViewById(R.id.textview_detail);
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
    private Date today;
    private List<Transaction> transactions; // Cached copy of words
    private Context context;

    ListAdapterTransactions(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        Calendar cal = Calendar.getInstance();
        // Ensure today is take as midnight
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        today = new Date(cal.getTimeInMillis());
    }

    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.view_item_transaction, parent, false);
        return new TransactionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TransactionViewHolder holder, int position) {
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        if (transactions != null) {
            Transaction current = transactions.get(position);
            String title = current.name + " - " + dateFormat.format(current.timestamp);
            String detail = String.format(Locale.getDefault(), " %1$.2f", current.amount);
            holder.titleItemView.setText(title);
            holder.detailItemView.setText(detail);


            if (current.timestamp.after(today)) {
                holder.titleItemView.setBackgroundColor(ContextCompat.getColor(context, R.color.transactionBudget));
                holder.detailItemView.setBackgroundColor(ContextCompat.getColor(context, R.color.transactionBudget));
            } else {
                holder.titleItemView.setBackgroundColor(ContextCompat.getColor(context, R.color.transactionReal));
                holder.detailItemView.setBackgroundColor(ContextCompat.getColor(context, R.color.transactionReal));
            }
        }
    }

    void setTransactions(List<Transaction> transactions){
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (transactions != null)
            return transactions.size();
        else return 0;
    }

    public Transaction getItemAt(int position) {
        if (position >= 0 && position < getItemCount()) {
            return transactions.get(position);
        }
        return null;
    }
}
