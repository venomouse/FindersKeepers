package huji.ac.il.finderskeepers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;



/**
 * This class contains all the constants and helper methods
 * used by the application
 * Created by Maria on 9/8/2015.
 */
public class Common {

    //request codes
    public static final int ADD_ITEM_REQUEST_CODE = 345;
    public static final int CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE = 1777;

    //map constants
    public static final int DEFAULT_CAMERA_ZOOM = 15;
    public static final int DEFAULT_CAMERA_PADDING = 100;

    public static boolean checkInternetConnection(final Context context) {
        if (!isConnected(context)) {
            AlertDialog connectionDialog = new AlertDialog.Builder(context).create();
            connectionDialog.setTitle("No Internet Connection");
            connectionDialog.setCanceledOnTouchOutside(false);
            connectionDialog.setMessage("Make sure that the internet connection is enabled");
            connectionDialog.setButton("Proceed to wireless connection settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                }
            });
            connectionDialog.show();
            return false;
        }

        return true;
    }


    public static  boolean isConnected(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        if (ni != null && ni.isConnected())
            return true;
        else
            return false;
    }

    public static boolean checkLocationIsOn(final Context context){
        if (!isLocationOn(context)) {
            AlertDialog locationDialog = new AlertDialog.Builder(context).create();
            locationDialog.setTitle("Location Service Disabled");
            locationDialog.setCanceledOnTouchOutside(false);
            locationDialog.setMessage("Make sure that the location service is enabled");
            locationDialog.setButton("Proceed to location settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            locationDialog.show();
            return false;
        }
        return true;
    }

    public static boolean isLocationOn(Context c) {
        LocationManager lm = (LocationManager) c
                .getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
