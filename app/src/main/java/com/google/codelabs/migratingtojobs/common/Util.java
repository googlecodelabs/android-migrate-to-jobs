package com.google.codelabs.migratingtojobs.common;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class Util {
    private Util() {}

    public static boolean isNetworkActive(ConnectivityManager connManager) {
        NetworkInfo netInfo = connManager.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
