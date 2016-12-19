package globalsoft.com.testgps;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import globalsoft.com.dialoginterface.AlertDialogBuilder;
import globalsoft.com.reversegeocode.Constants;
import globalsoft.com.reversegeocode.LocationLocator;
import globalsoft.com.reversegeocode.ReverseGeoCoder;

/**
 * Created by adebowale.odulaja on 7/6/16.
 */
public class CreateOutletActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Toolbar toolbar;
    Spinner spinner, spinnerOutletType, spinnerState, spinnerLGA;
    TextView editAdd, outletname, gps;
    Button btnSaveOutlet;

    AddressResultReceiver resultReceiver;
    LocationLocator locationLocator;
    private String url, uname;
    AlertDialogBuilder alert = new AlertDialogBuilder();
    Bundle bundle;

    final String countrySpinner[] = {"Nigeria", "USA", "United Kingdom", "Canada" };
    final String outletType[] = {"---Select Type---","MVO","MTO"};
    final String state[] = {"---Select State---", "Abia", "Lagos"};
    final String lgaAbia[] = {"---Select LGA---","Aba North","Aba South","Arochukwu","Bende","Ikwuano","Isiala Ngwa North","Isiala Ngwa South","Isuikwuato","Obi Ngwa","Ohafia",
            "Osisioma","Ugwunagbo","Ukwa East","Ukwa West","Umuahia North","Umuahia South","Umu Nneochi"};

    private String OUTLET_TYPE,OUTLET_NAME,COUNTRY,STATE,LGA,ADDRESS,USERNAME;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_outlet);
        bundle=getIntent().getExtras();
        uname = bundle.getString("USERNAME");
        locationLocator = new LocationLocator(CreateOutletActivity.this);
        locationLocator.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //locationLocator.updateGPS();

        //pushInfo();
        //resultReceiver = new AddressResultReceiver(null);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Marketing Audit Solution");
        toolbar.setSubtitle("Create New Outlet");
        toolbar.setNavigationIcon(R.mipmap.nb_launcher);

        editAdd = (TextView)findViewById(R.id.editaddress);
        outletname = (TextView)findViewById(R.id.editOutletName);
        gps = (TextView)findViewById(R.id.editGPS);
        gps.setKeyListener(null);
        //Log.e("BEFORE_ON_CLICK",uname);


        btnSaveOutlet = (Button)findViewById(R.id.btnSave_Outlet);
        btnSaveOutlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkFields()){
                    alert.showAlertDialog(CreateOutletActivity.this, "Please all field are required!!!", "", 0);
                }
                else{
                    OUTLET_TYPE = spinnerOutletType.getSelectedItem().toString();
                    OUTLET_NAME = outletname.getText().toString().replace(" ","%20");
                    COUNTRY = spinner.getSelectedItem().toString().replace(" ","%20");
                    STATE = spinnerState.getSelectedItem().toString().replace(" ","%20");
                    LGA = spinnerLGA.getSelectedItem().toString().replace(" ","%20");
                    ADDRESS = editAdd.getText().toString().replace(" ","%20");
                    USERNAME = uname;
                    url = "http://www.nbappserver.com/nbpage/nbapi.php?optype=createoutlet&outlettype="+OUTLET_TYPE
                            +"&outletname="+OUTLET_NAME+"&country="+COUNTRY+"&state="+STATE+"&lga="+LGA+"&address="+ADDRESS+"&username="+USERNAME+"";
                    new SaveOutlet(CreateOutletActivity.this).execute(url);
                }
            }
        });

        spinner = (Spinner)findViewById(R.id.countryspinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateOutletActivity.this, android.R.layout.simple_list_item_1,countrySpinner);
        spinner.setAdapter(adapter);
        //spinner.setOnItemSelectedListener(this);

        spinnerOutletType = (Spinner)findViewById(R.id.outletspinner);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(CreateOutletActivity.this, android.R.layout.simple_list_item_1,outletType);
        spinnerOutletType.setAdapter(adapter1);

        spinnerState = (Spinner)findViewById(R.id.statespinner);
        ArrayAdapter<String> adapterState = new ArrayAdapter<>(CreateOutletActivity.this, android.R.layout.simple_list_item_1,state);
        spinnerState.setAdapter(adapterState);
        spinnerState.setOnItemSelectedListener(this);
    }

    private boolean checkFields(){
        if (editAdd.getText().toString().isEmpty() || outletname.getText().toString().isEmpty() || spinnerOutletType.getSelectedItem().equals("---Select Type---")
                || spinnerState.getSelectedItem().equals("---Select State---") || spinnerLGA.getSelectedItem().equals("---Select LGA---")){
            return true;
        }
        else{return false;}
        //return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {

        if(parent.getId() == R.id.statespinner) {
            String stateSelected = state[position];

            switch (stateSelected) {
                case "Abia":
                    //Toast.makeText(this, "Spinner value selected is: " + stateSelected, Toast.LENGTH_SHORT).show();
                    spinnerLGA = (Spinner)findViewById(R.id.lgaspinner);
                    ArrayAdapter<String> adapterLGA = new ArrayAdapter<>(CreateOutletActivity.this, android.R.layout.simple_list_item_1,lgaAbia);
                    spinnerLGA.setAdapter(adapterLGA);
                    break;
                default:
                    //spinnerLGA.setAdapter(null);

            }
        }
        else if (parent.getId() == R.id.countryspinner){
            String countrySelected = countrySpinner[position];

            switch (countrySelected) {
                case "Nigeria":
                   // Toast.makeText(this, "Spinner value selected is: " + countrySelected, Toast.LENGTH_SHORT).show();
                    break;
                default:

            }

        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void pushInfo(){
        Intent intent = new Intent(this, ReverseGeoCoder.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_LATITUDE_DATA_EXTRA, locationLocator.latitude);
        intent.putExtra(Constants.LOCATION_LONGITUDE_DATA_EXTRA, locationLocator.longitude);
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {
            if (resultCode == Constants.SUCCESS_RESULT) {
                final Address address = resultData.getParcelable(Constants.RESULT_ADDRESS);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        editAdd.setText(resultData.getString(Constants.RESULT_DATA_KEY));
                        //progressBar.setVisibility(View.GONE);
                        //infoText.setVisibility(View.VISIBLE);
                        //infoText.setText("Latitude: " + address.getLatitude() + "\n" +
                               // "Longitude: " + address.getLongitude() + "\n" +
                               // "Address: " + resultData.getString(Constants.RESULT_DATA_KEY));
                    }
                });
            }
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //progressBar.setVisibility(View.GONE);
                        //infoText.setVisibility(View.VISIBLE);
                        editAdd.setText(resultData.getString(Constants.RESULT_DATA_KEY));
                    }
                });
            }
        }
    }

    private class SaveOutlet extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;

        Context context;

        public SaveOutlet(Activity activity){
            context = activity;
            progressDialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Saving outlet...\nPlease wait");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String stringResult) {
            if(progressDialog.isShowing()) progressDialog.dismiss();

            alert.showAlertDialog(CreateOutletActivity.this, "New outlet has been \ncreated successfully", "",
                    R.mipmap.alerticon);
            Intent loadOutlet = new Intent(CreateOutletActivity.this, OutletPage.class);
            startActivity(loadOutlet);
        }

        @Override
        protected String doInBackground(String... params) {
            url = params[0];
            StringBuilder stringBuilder = new StringBuilder();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
            /*nameValuePairs.add(new BasicNameValuePair(OUTLET_TYPE, "MTO"));
            nameValuePairs.add(new BasicNameValuePair(OUTLET_NAME, "Muftao Lounge"));
            nameValuePairs.add(new BasicNameValuePair(COUNTRY, "Yankee"));
            nameValuePairs.add(new BasicNameValuePair(STATE, "New York"));
            nameValuePairs.add(new BasicNameValuePair(LGA, "Maryland"));
            nameValuePairs.add(new BasicNameValuePair(ADDRESS, "27, adetayo osho, akoka"));*/

            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
                StatusLine statusLine = httpResponse.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if(statusCode == 200) {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpEntity entity = httpResponse.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content, "UTF-8"));
                    String line = null;
                    while((line = reader.readLine()) != null){
                        stringBuilder.append(line).append("\n");
                    }
                }
                else {

                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return stringBuilder.toString();
        }
    }


}
