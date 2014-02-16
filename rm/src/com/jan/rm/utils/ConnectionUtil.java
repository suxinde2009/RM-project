package com.jan.rm.utils;

import com.jan.rm.R;
import com.jan.rm.widget.RMToast;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionUtil {
	
	public static boolean checkConnection(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isAvailable();
		}

		return false;
	}
	
	public static void checkConnectionAndReport(Context context){
		if(!checkConnection(context)){
			RMToast.showNegative(context, context.getString(R.string.connection_failure));
		}
	}

	public static boolean isConnectWithWIFI(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.getTypeName().equals("WIFI")) {
			return true;
		}

		return false;
	}
}
