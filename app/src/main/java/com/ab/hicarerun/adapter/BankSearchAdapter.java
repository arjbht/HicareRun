package com.ab.hicarerun.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.hicarerun.R;

import java.util.ArrayList;
import java.util.List;

public class BankSearchAdapter extends RecyclerView.Adapter<BankSearchAdapter.ViewHolder>
        implements Filterable {
    private Context context;
    private List<String> items;
    private List<String> itemsFiltered;
    private BankAdapterListener listener;


    public BankSearchAdapter(Context context, List<String> items) {
        this.context = context;
        this.items = items;
        this.itemsFiltered = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bank_row_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final String name = itemsFiltered.get(position);
        holder.name.setText(name);
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelected(name,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();

                List<String> filtered = new ArrayList<>();

                if (query.isEmpty()) {
                    filtered = items;
                } else {
                    for (String movie : items) {
                        if (movie.toLowerCase().contains(query.toLowerCase())) {
                            filtered.add(movie);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.count = filtered.size();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                itemsFiltered = (ArrayList<String>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void onBankSelected(BankAdapterListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public LinearLayout layout;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tvName);
            layout = view.findViewById(R.id.lnrbank);

        }

    }


    public interface BankAdapterListener {
        void onSelected(String item, int position);
    }

}
