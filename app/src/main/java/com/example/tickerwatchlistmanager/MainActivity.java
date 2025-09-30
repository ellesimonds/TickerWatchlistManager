package com.example.tickerwatchlistmanager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements TickerListFragment.OnTickerClickListener {

    private InfoWebFragment infoWebFragment; // Keep reference for landscape mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();


        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState == null) {
                TickerListFragment tickerListFragment = new TickerListFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, tickerListFragment)
                        .commit();
            }
        } else {

            if (savedInstanceState == null) {
                TickerListFragment tickerListFragment = new TickerListFragment();
                infoWebFragment = new InfoWebFragment(); // Keep reference

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.ticker_list_container, tickerListFragment);
                transaction.replace(R.id.info_web_container, infoWebFragment);
                transaction.commit();
            }
        }
    }


    @Override
    public void onTickerClicked(String symbol) {
        infoWebFragment.loadTickerInfo(symbol);
        if (infoWebFragment != null) {
            // Landscape mode: update existing fragment
            infoWebFragment.loadTickerInfo(symbol);
        } else {
            // Portrait mode: replace container with InfoWebFragment
            infoWebFragment = new InfoWebFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, infoWebFragment)
                    .addToBackStack(null)
                    .commit();
            getSupportFragmentManager().executePendingTransactions();
            infoWebFragment.loadTickerInfo(symbol);
        }
    }

    // -------------------- Inner Fragments --------------------

    // Ticker List Fragment
    public static class TickerListFragment extends Fragment {

        public interface OnTickerClickListener {
            void onTickerClicked(String symbol);
        }

        private OnTickerClickListener listener;

        public TickerListFragment() {}

        @Override
        public void onAttach(android.content.Context context) {
            super.onAttach(context);
            if (context instanceof OnTickerClickListener) {
                listener = (OnTickerClickListener) context;
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_ticker_list, container, false);
        }


        private void tickerSelected(String symbol) {
            if (listener != null) {
                listener.onTickerClicked(symbol);
            }
        }
    }

    //Info Web Fragment
    public static class InfoWebFragment extends Fragment {

        private WebView webView;

        public InfoWebFragment() {}

        @SuppressLint("SetJavaScriptEnabled")
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_info_web, container, false);
            webView = view.findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            return view;
        }

        public void loadTickerInfo(String symbol) {
            if (webView != null) {
                String url = "https://seekingalpha.com" + symbol;
                webView.loadUrl(url);
            }
        }
    }
}
