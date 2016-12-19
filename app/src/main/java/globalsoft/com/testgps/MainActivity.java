package globalsoft.com.testgps;

import android.Manifest;
import android.app.Activity;
//import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements LocationListener, View.OnClickListener {

    private LocationManager locationManager;
    private Button btnLocation;
    private double longitude, latitude;
    String placeName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /********** get Gps location service LocationManager object ***********/
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                /* CAL METHOD requestLocationUpdates */

        // Parameters :
        //   First(provider)    :  the name of the provider with which to register
        //   Second(minTime)    :  the minimum time interval for notifications,
        //                         in milliseconds. This field is only used as a hint
        //                         to conserve power, and actual time between location
        //                         updates may be greater or lesser than this value.
        //   Third(minDistance) :  the minimum distance interval for notifications, in meters
        //   Fourth(listener)   :  a {#link LocationListener} whose onLocationChanged(Location)
        //                         method will be called for each location update


        if (checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                3000,
                10, this);

        /********* After registration onLocationChanged method  ********/
        /********* called periodically after each 3 sec ***********/
        btnLocation = (Button)findViewById(R.id.btnGetLocation);
        btnLocation.setOnClickListener(this);

    }

    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();

        //String msg = "Latitude: "+location.getLatitude()+" Longitude: "+location.getLongitude();
        String msg = "Latitude: "+latitude+" Longitude: "+longitude;

        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        /******** Called when User off Gps *********/
        Toast.makeText(getBaseContext(), "Gps turned off ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        /******** Called when User on Gps  *********/
        Toast.makeText(getBaseContext(), "Gps turned on ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    public void onClick(View view){
        int btnClicked = view.getId();

        if(btnClicked == R.id.btnGetLocation){
            String msg = "Latitude: "+latitude+" Longitude: "+longitude;
            Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
            Log.i("LONGITUDE",""+longitude);
            Log.i("LATITUDE",""+latitude);
            reverseGeoCode(latitude,longitude);
            Intent in = new Intent(MainActivity.this, UserAddress.class);
            //in.putExtra("The address",placeName);
            startActivity(in);
        }

    }


    public String reverseGeoCode(double latitude, double longitude){
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        //PreferenceStorage p=new PreferenceStorage(context);
        //String placeName ="";
        try {
            List<Address> addrList = geoCoder.getFromLocation(latitude,longitude, 1);
            if(addrList.size() > 0){
                android.location.Address addr = addrList.get(0);

                StringBuffer sBuff = new StringBuffer();
                String decoded = addr.getAdminArea();
                if(decoded != null){
                    Log.i("ADMINAREA", decoded);
                }
                decoded = addr.getPremises();
                if(decoded != null){
                    Log.i("PREMISES", decoded);
                }
                decoded = addr.getFeatureName();
                if(decoded != null){
                    sBuff.append(decoded);
                    Log.i("FEATURENAME", decoded);
                }
                /*decoded = addr.getCountryName();
                if(decoded != null){
                    if(decoded.equalsIgnoreCase("United Kingdom")){
                        t.setCountry("United-Kingdom");
                        p.updateUserCountry("United-Kingdom");
                        Log.i("COUNTRYNAME", decoded);
                    }
                    else if(decoded.equalsIgnoreCase("United States")){
                        t.setCountry("United-States");
                        p.updateUserCountry("United-States");
                        Log.i("COUNTRYNAME", decoded);
                    }
                    else{
                        t.setCountry(decoded);
                        p.updateUserCountry(decoded);
                        Log.i("COUNTRYNAME", decoded);
                    }
                }*/
                /*decoded = addr.getSubAdminArea();
                if(decoded != null){
                    t.setDistrict(decoded.replace("District",""));
                    p.updateUserDistrict(t.getDistrict());
                    Log.i("SUBADMIN", decoded.replace("District",""));
                }*/
                decoded = addr.getLocality();
                Log.i("LOCALITY", decoded);
                /*if(decoded != null){
                    t.setTown(decoded);
                    if(t.getDistrict().equals("")){
                        p.updateUserDistrict(t.getTown());
                    }
                    Log.i("LOCALITY", decoded);
                }*/
                decoded = addr.getSubLocality();
                if(decoded != null){
                    Log.i("SUBLOCALITY", decoded);
                }
                decoded = addr.getThoroughfare();
                if(decoded != null){
                    sBuff.append(", "+ decoded );
                    Log.i("THOROGHFARE", decoded);
                }
                decoded = addr.getSubThoroughfare();
                if(decoded != null){
                    //sBuff.append(decoded );
                    Log.i("SUBTHOROUFARE", decoded);
                }
                placeName = new String(sBuff);
                //t.setStreet(placeName);
                Log.i("PLACENAME", placeName);
            }
        } catch (Exception e) {
            Log.i("GPS","Exception in reverse geocoding in StartUPGPS"+" "+e.getMessage());
            Toast.makeText(this,"Location services is not available at the moment. Please try again later !",Toast.LENGTH_LONG).show();
        }
        return placeName;
    }
}
