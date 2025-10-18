package com.example.tickerwatchlistmanager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import java.util.ArrayList;

public class TickerListFragment extends Fragment {

    public interface OnTickerClickListener {
        void onTickerClicked(String symbol);

        void onNewIntent(Intent intent);
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
    
    public void addTicker(String symbol) {

        // TODO: Add the ticker to your list or adapter if you have one
        Toast.makeText(getContext(), "Ticker added: " + ticker, Toast.LENGTH_SHORT).show();
    }


}
