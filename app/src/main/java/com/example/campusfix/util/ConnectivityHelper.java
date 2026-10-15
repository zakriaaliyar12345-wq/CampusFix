package com.example.campusfix.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import com.example.campusfix.R;

/**
 * Connectivity checks (course topic 07.2).
 *
 * Uses the modern getActiveNetwork/getNetworkCapabilities pair instead of
 * the deprecated getActiveNetworkInfo. Remember the lesson from the topic:
 * "connected" is only a hint — captive portals and dead links still fail at
 * the application layer, so only a completed request proves reachability.
 */
public final class ConnectivityHelper {

    private ConnectivityHelper() {
    }

    /** True when the device currently has a network with internet access. */
    public static boolean isOnline(Context context) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) return false;

        Network active = manager.getActiveNetwork();
        if (active == null) return false;

        NetworkCapabilities capabilities =
                manager.getNetworkCapabilities(active);
        return capabilities != null
                && capabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    /** Human-readable status line for the demo screen. */
    public static String describe(Context context) {
        return context.getString(isOnline(context)
                ? R.string.demo_online
                : R.string.demo_offline);
    }
}
