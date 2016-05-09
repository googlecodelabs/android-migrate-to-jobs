package com.google.codelabs.migratingtojobs;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Util {
    private Util() {}

    static boolean isNetworkActive(ConnectivityManager connManager) {
        NetworkInfo netInfo = connManager.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
