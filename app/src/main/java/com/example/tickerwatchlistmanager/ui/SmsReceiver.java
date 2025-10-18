package com.example.tickerwatchlistmanager.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.tickerwatchlistmanager.MainActivity;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";
    public static final String EXTRA_TICKER = "extra_ticker";
    public static final String EXTRA_OPEN_IMMEDIATE = "extra_open_immediate";
    public static final String EXTRA_TOAST = "extra_toast";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras == null) return;

            Object[] pdus = (Object[]) extras.get("pdus");
            if (pdus == null) return;

            StringBuilder sb = new StringBuilder();
            for (Object pdu : pdus) {
                SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdu);
                sb.append(msg.getMessageBody());
            }
            String fullMessage = sb.toString();
            Log.d(TAG, "Received SMS: " + fullMessage);

            //try to extract ticker
            String ticker = parseTickerFromMessage(fullMessage);
            Intent start = new Intent(context, MainActivity.class);
            start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            if (ticker == null) {
                //not in correct format -> launch activity and show toast
                start.putExtra(EXTRA_TOAST, "No valid watchlist entry found in SMS");
                context.startActivity(start);
            } else {
                //validate ticker content (letters only)
                String normalized = ticker.toUpperCase();
                if (isValidTicker(normalized)) {
                    start.putExtra(EXTRA_TICKER, normalized);
                    start.putExtra(EXTRA_OPEN_IMMEDIATE, true);
                    context.startActivity(start);
                } else {
                    start.putExtra(EXTRA_TOAST, "Invalid ticker received: " + ticker);
                    context.startActivity(start);
                }
            }
        }
    }


    private String parseTickerFromMessage(String msg) {
        if (msg == null) return null;

        String lower = msg.toLowerCase();
        int idx = lower.indexOf("ticker:");
        if (idx == -1) return null;

        int start = lower.indexOf("<<", idx);
        int end = lower.indexOf(">>", idx);
        if (start == -1 || end == -1 || end <= start + 2) return null;

        String inner = msg.substring(start + 2, end);
        inner = inner.trim();
        if (inner.isEmpty()) return null;
        return inner;
    }

    private boolean isValidTicker(String t) {
        return t.matches("^[A-Z]+$");
    }
}
