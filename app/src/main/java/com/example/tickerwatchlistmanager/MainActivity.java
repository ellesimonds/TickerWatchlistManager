package com.example.tickerwatchlistmanager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.webkit.WebView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tickerwatchlistmanager.ui.SmsReceiver;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements TickerListFragment.OnTickerClickListener {

    private InfoWebFragment infoWebFragment;
    private static final int REQ_SMS_PERM = 1001;

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

        //request runtime SMS permission if needed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS},
                    REQ_SMS_PERM);
        }
        handleIncomingIntent(getIntent());
    }


    @Override
    public void onTickerClicked(String symbol) {
        infoWebFragment.loadTickerInfo(symbol);
        if (infoWebFragment != null) {
            //landscape mode: update existing fragment
            infoWebFragment.loadTickerInfo(symbol);
        } else {
            //portrait mode: replace container with InfoWebFragment
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

    //ticker list fragment
    public static class TickerListFragment extends Fragment {

        public interface OnTickerClickListener {
            void onTickerClicked(String symbol);
        }

        private OnTickerClickListener listener;




        @Override
        public void onAttach(android.content.Context context) {
            super.onAttach(context);
            if (context instanceof OnTickerClickListener) {
                listener = (OnTickerClickListener) context;
            }
        }

        private ArrayList<String> tickers = new ArrayList<>();
        private ArrayAdapter<String> adapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_ticker_list, container, false);
            ListView listView = view.findViewById(R.id.tickerListView);
            tickers.addAll(Arrays.asList("NEE", "AAPL", "DIS"));
            adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, tickers);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener((parent, v, position, id) -> {
                String symbol = tickers.get(position);
                if (listener != null) listener.onTickerClicked(symbol);
            });
            return view;
        }

        public void addTicker(String ticker) {
            if (tickers.size() < 6) tickers.add(ticker);
            else tickers.set(5, ticker);
            adapter.notifyDataSetChanged();
        }

        private void tickerSelected(String symbol) {
            if (listener != null) {
                listener.onTickerClicked(symbol);
            }
        }
    }

    //info web fragment
    public static class InfoWebFragment extends Fragment {

        private WebView webView;


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

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIncomingIntent(intent);
    }

    private void handleIncomingIntent(Intent intent) {
        if (intent == null) return;

        String toastText = intent.getStringExtra(SmsReceiver.EXTRA_TOAST);
        String ticker = intent.getStringExtra(SmsReceiver.EXTRA_TICKER);
        boolean openNow = intent.getBooleanExtra(SmsReceiver.EXTRA_OPEN_IMMEDIATE, false);

        if (toastText != null) {
            Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
        }

        if (ticker != null) {
            //add ticker to list fragment
            TickerListFragment listFrag = (TickerListFragment)
                    getSupportFragmentManager().findFragmentById(
                            R.id.fragment_container // portrait container
                    );
            if (listFrag == null) {
                listFrag = (TickerListFragment)
                        getSupportFragmentManager().findFragmentById(
                                R.id.ticker_list_container // landscape list container
                        );
            }

            if (listFrag != null) {
                listFrag.addTicker(ticker);
            }

            //if openNow, load the web info fragment
            if (openNow) {
                InfoWebFragment infoFrag = (InfoWebFragment)
                        getSupportFragmentManager().findFragmentById(
                                R.id.info_web_container // landscape web container
                        );

                if (infoFrag != null) {
                    infoFrag.loadTickerInfo(ticker);
                } else {
                    //if portrait mode, show the info web fragment
                    infoFrag = new InfoWebFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, infoFrag)
                            .addToBackStack(null)
                            .commit();
                    getSupportFragmentManager().executePendingTransactions();
                    infoFrag.loadTickerInfo(ticker);
                }
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQ_SMS_PERM) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS permission denied - receiver might not work", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}



