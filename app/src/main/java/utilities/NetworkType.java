package utilities;

import android.net.ConnectivityManager;

public enum NetworkType {
    mobile(ConnectivityManager.TYPE_MOBILE),
    wifi(ConnectivityManager.TYPE_WIFI);
    int type;

    private NetworkType(int type) {
        this.type = type;
    }
}
