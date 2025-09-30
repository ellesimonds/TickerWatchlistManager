package com.example.tickerwatchlistmanager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;

public class TickerListFragment extends Fragment {

    public interface OnTickerClickListener {
        void onTickerClicked(String symbol);
    }

    private OnTickerClickListener listener;
    private ArrayList<String> tickers = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    public TickerListFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTickerClickListener) {
            listener = (OnTickerClickListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticker_list, container, false);

        ListView listView = view.findViewById(R.id.tickerListView);

        //start with 3 default
        tickers.add("NEE");
        tickers.add("AAPL");
        tickers.add("DIS");

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, tickers);
        listView.setAdapter(adapter);

        //handle clicks
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = tickers.get(position);
                if (listener != null) listener.onTickerClicked(selected);
            }
        });

        return view;
    }

    //call this to add a new ticker (replaces 6th if needed)
    public void addTicker(String symbol) {
        if (tickers.size() < 6) {
            tickers.add(symbol);
        } else {
            tickers.set(5, symbol); // replace 6th
        }
        adapter.notifyDataSetChanged();
    }
}
