package com.zjh.mail;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Created by ZJH on 2016-11-02-0002.
 */

public class NetWorkUtils {

    public  static  boolean isNetWorkConnected(Context context)
    {
        if(context==null)
        {
            return  false;
        }
        if(isMobileConnect(context)||isWifiConnect(context))
        {
            return  true;
        }
        else
            return false;
    }
    public static boolean isWifiConnect(Context context)
    {
        if(context==null)
        {
            return  false;
        }
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo info=connectivityManager.getNetworkInfo(connectivityManager.TYPE_WIFI);
        if(info!=null)
        {
            return  info.isAvailable();
        }

        return  false;
    }

    public static  boolean isMobileConnect(Context context)
    {
        if(context==null)
        {
            return  false;
        }
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo info=connectivityManager.getNetworkInfo(connectivityManager.TYPE_MOBILE);
        if(info!=null)
        {
            return  info.isAvailable();
        }
        return  false;
    }

    public static int getConnectedType(Context context)
    {
        if(context==null)
        {
            return  -1;
        }
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo info=connectivityManager.getActiveNetworkInfo();
        if(info!=null&&info.isAvailable())
        {
            return  info.getType();
        }
        return  -1;
    }

}
