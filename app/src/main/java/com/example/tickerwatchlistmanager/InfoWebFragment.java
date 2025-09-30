package com.example.tickerwatchlistmanager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import androidx.fragment.app.Fragment;

public class InfoWebFragment extends Fragment {

    private WebView webView;

    public InfoWebFragment() {}

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_web, container, false);
        webView = view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

        // Load default SeekingAlpha page
        webView.loadUrl("https://seekingalpha.com");

        return view;
    }

    public void loadTickerInfo(String symbol) {
        if (webView != null) {
            // Build the URL for the ticker (simple string concatenation)
            String url = "https://seekingalpha.com/symbol/" + symbol;
            webView.loadUrl(url);
        }
    }
}
