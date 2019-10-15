package wifidirect.wifidirect;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Location permission is necessary to use Wifi Direct for android 6 and later
 */
public class LocationPermission extends AppCompatActivity {
    MainActivity mainActivity;
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    static final String TAG = "MyApp: LocationPermission: ";

    public LocationPermission(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    /**
     * The permission isn’t granted and use requestPermissions to ask the user to grant.
     * The response from the user is captured in the onRequestPermissionsResult callback.
     */
    public void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                != mainActivity.getPackageManager().PERMISSION_GRANTED) {
            Log.d(TAG, "Location permission is not granted.");
            ActivityCompat.requestPermissions(mainActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            Log.d(TAG, "Location permission is granted.");
        }
    }

    /**
     * Callback for the result from requesting permissions.
     * This method is invoked for every call on requestPermissions(android.app.Activity, String[], int).
     *
     * @param requestCode The request code passed in requestPermissions(android.app.Activity, String[], int)
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions which is
     *                     either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == getPackageManager().PERMISSION_GRANTED) {
                    Log.d(TAG, "Location permission is granted.");
                } else {
                    Log.d(TAG, "Location permission is denied.");
                }
            }
        }
    }
}
