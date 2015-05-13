package com.xhr.Poem.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by xhrong on 2015/5/13.
 */
public class NetWorkUtil {

    public static final int CONNECTIVITY_TYPE_NONE = 0x001;

    public static final int CONNECTIVITY_TYPE_WIFI = 0x002;

    public static final int CONNECTIVITY_TYPE_OTHER = 0x003;

    public static int checkConnectionState(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = cm.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            if (activeInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return CONNECTIVITY_TYPE_WIFI;
            } else {
                return CONNECTIVITY_TYPE_OTHER;
            }
        } else {
            return CONNECTIVITY_TYPE_NONE;
        }
    }
}
