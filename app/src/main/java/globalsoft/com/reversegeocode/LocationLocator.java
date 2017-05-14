package globalsoft.com.reversegeocode;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import globalsoft.com.dialoginterface.AlertDialogBuilder;

/**
 * Created by adebowale.odulaja on 7/9/16.
 */
public class LocationLocator extends Activity {

    public double latitude = 0, longitude = 0;
    public LocationManager locationManager;
    //static int MY_ACCESS_FINE_LOCATION;
    Context context;
    String provider_info;
    public String bestProvider;
    public Criteria criteria;
    AlertDialogBuilder alert = new AlertDialogBuilder();

    public LocationLocator(Context contxt) {
        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //updateGPS();
        context = contxt;
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " + "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void updateGPS() {
        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true));

        Location location = locationManager.getLastKnownLocation(bestProvider);//provider_info
        if (!checkLocation()) return;
        // provider_info = LocationManager.GPS_PROVIDER;
        // provider_info = LocationManager.NETWORK_PROVIDER;
        if (location != null) {//locationManager != null
            //Location location = locationManager.getLastKnownLocation(provider_info);//This was where I have this b4
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        } else {
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 10, locationListenerGPS);//60 * 1000
            locationManager.requestLocationUpdates(bestProvider, 10000, 10, locationListenerGPS);//60 * 1000
        }
    }

    public void forceGPS() {
        criteria = new Criteria();
        bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true));

        //Location location = locationManager.getLastKnownLocation(bestProvider);//provider_info

        if (!checkLocation())
            return;
        locationManager.requestLocationUpdates(bestProvider, 5000, 10, locationListenerGPS);//60 * 1000
        Toast.makeText(context, longitude + "  " + latitude, Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //locationManager.removeUpdates(locationListenerGPS);

    }


    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            /*if (longitude != 0 && latitude != 0) {
                locationManager.removeUpdates(locationListenerGPS);//Remove the update to reduce batery life.
            }*/

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //longitudeValueGPS.setText(longitudeGPS + "");
                    //latitudeValueGPS.setText(latitudeGPS + "");
                    Log.e("LONGITUDE", "" + longitude);
                    Log.e("LATITUDE", "" + latitude);
                    //Toast.makeText(context, "GPS Provider update " + "Longitude: " + longitude + "Latitude: " + latitude, Toast.LENGTH_SHORT).show();
                }
            });

            //String jj = "" + longitude, gh = "" + latitude;
            //Toast.makeText(context, jj + "  " + gh, Toast.LENGTH_LONG).show();

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

}
