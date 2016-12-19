package globalsoft.com.reversegeocode;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by adebowale.odulaja on 7/8/16.
 */
public class ReverseGeoCoder extends IntentService{

    protected ResultReceiver resultReceiver;
    Geocoder geocoder;
    String errorMessage = "";
    public static final String TAG = "ReverseGeoCoder";
    List<Address> addresses = null;
    Intent reverseIntent;

    //public static ArrayList<String> theAddress = new ArrayList<>();

    /*public ReverseGeoCoder() {
        //processGeocode();
    }*/

    public ReverseGeoCoder(){
        super("ReverseGeoCoder");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        reverseIntent = intent;
        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        geocoder = new Geocoder(ReverseGeoCoder.this, Locale.getDefault());
        //updateGPS();
        processGeocode();

    }

    private void deliverResultToReceiver(int resultCode, String message, Address address) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.RESULT_ADDRESS, address);
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        resultReceiver.send(resultCode, bundle);
    }


    public void processGeocode() {
        double latitude = reverseIntent.getDoubleExtra(Constants.LOCATION_LATITUDE_DATA_EXTRA, 0);
        double longitude = reverseIntent.getDoubleExtra(Constants.LOCATION_LONGITUDE_DATA_EXTRA, 0);

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException ioException) {
            errorMessage = "Service Not Available";
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            errorMessage = "Invalid Latitude or Longitude Used";
            Log.e(TAG, errorMessage + ". " + "Latitude = " + latitude + ", Longitude = " + longitude, illegalArgumentException);
        }


        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "No Address Found";
                Log.e(TAG, errorMessage);
            }
        } else {
            for (Address address : addresses) {
                String outputAddress = "";
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    outputAddress += " --- " + address.getAddressLine(i);
                }
                Log.e(TAG, outputAddress);
            }
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();

            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, "Address Found");
            deliverResultToReceiver(Constants.SUCCESS_RESULT, TextUtils.join(System.getProperty("line.separator"), addressFragments), address);
            //theAddress = addressFragments;
        }

        //return String.valueOf(theAddress);

    }


    //Getting the location automatically

}
