package globalsoft.com.testgps;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import globalsoft.com.dialoginterface.AlertDialogBuilder;

public class OutletPage extends AppCompatActivity {

    // private static final String outletURL = "http://populationassociationng.org/nbpage/nbapi.php?optype=getoutlets";
    private static String outletURL;
    String outletSearchURL;
    private static final String OUTLET_NAME = "outletname";
    private static final String OUTLET_TYPE = "outlettype";
    private static final String OUTLET_ID = "outletid";
    private static final String OUTLET_SEARCH_ID = "id";
    public String uname;

    Toolbar toolbar;
    Bundle bundle;
    //ProgressDialog pd;
    //ListActivity listAct = new ListActivity();
    //ActionBarActivity actionBarActivity;

    ArrayList<HashMap<String, String>> outletArray = new ArrayList<>();
    //ArrayList<HashMap<String, String>> outletStore = new ArrayList<>();
    ArrayList<HashMap<String, String>> outletSearch = new ArrayList<>();
    ListView outletHolder;
    SimpleAdapter adapter;
    AlertDialogBuilder alert = new AlertDialogBuilder();

    Button button;
    EditText editText;

    public OutletPage() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e("Error"+Thread.currentThread().getStackTrace()[2],paramThrowable.getLocalizedMessage());
            }
        });
        setContentView(R.layout.outletpage);
        //pd = new ProgressDialog(this);

        bundle = getIntent().getExtras();
        uname = bundle.getString("USERNAME");
        outletURL = "http://www.nbappserver.com/nbpage/nbapi.php?optype=assignoutlets&username="+uname+"";
        //outletURL = "http://populationassociationng.org/nbpage/nbapi.php?optype=assignoutlets&username="+uname+"";

        //actionBarActivity = new ActionBarActivity();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Trade Marketing Audit Soln");
        toolbar.setSubtitle("Welcome " + uname);
        toolbar.setNavigationIcon(R.mipmap.nb_launcher);

        editText = (EditText) findViewById(R.id.editSearch);

        button = (Button) findViewById(R.id.btnSearch);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SearchOutlet();
                if (editText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(OutletPage.this, "Search text can not be empty!", Toast.LENGTH_LONG).show();
                } else {
                    if (outletSearch.size() != 0) {
                        outletSearch.clear();
                    }
                    //else if (outletSearch.size() == 0){Toast.makeText(OutletPage.this, "No match for your search!", Toast.LENGTH_LONG).show();}
                    outletSearchURL = "http://www.nbappserver.com/nbpage/nbapi.php?optype=searchoutlet&searchvalue=" + editText.getText().toString().trim() + "";
                    new SearchOutlet(OutletPage.this).execute();
                }

            }
        });

        new ExistingOutletTask(OutletPage.this).execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_outlet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_about) {
            String version = "Version "+BuildConfig.VERSION_NAME;
            alert.showAlertDialog(OutletPage.this, version, "About", R.mipmap.nb_launcher);
        }

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_add) {
            Intent loadCreateOutlet = new Intent(this, CreateOutletActivity.class);
            loadCreateOutlet.putExtra("USERNAME", uname);
            startActivity(loadCreateOutlet);
            //return true;
        } else if (id == R.id.action_delete) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void SearchOutlet() {
        String sListName = "", keyToGet = "outletname", valueToSearch = "Daba Inn Garden";
        StringBuilder buildSearch = new StringBuilder();

        OutletPage.this.adapter.getFilter().filter(editText.getText().toString());

        /*for (HashMap<String, String> hashMap : outletStore) {
            String jj = hashMap.get("outletname");
            // Log.i("InsideJJ",jj);
            for (String key : hashMap.keySet()) {
                if (keyToGet.equals(key)) {
                    if (hashMap.get(key).contains(valueToSearch)) {
                        System.out.println("The result: " + hashMap.get("outlettype") + "\n");
                        //buildSearch.append(sListName);
                    }
                    //System.out.println("Found : " + key + " / value : " + hashMap.get(key));
                }
            }
            // For each hashmap, iterate over it
            /*for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                // Do something with your entrySet, for example get the key.
                sListName = entry.getKey();
            }*/
        //}*/
        //System.out.println("The result: "+buildSearch.toString()+"\n");

    }


    private class ExistingOutletTask extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog progressDialog;

        Context context;

        public ExistingOutletTask(Activity listActivity) {
            context = listActivity;
            progressDialog = new ProgressDialog(context);
        }

        //public ExisitngOutletTask(OutletPage outletPage) {

        //}

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...\nPlease wait");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            //SearchOutlet();
            //ListActivity listActivity = new ListActivity();
            if (progressDialog.isShowing()) progressDialog.dismiss();
            if (progressDialog != null) progressDialog = null;

            outletHolder = (ListView) findViewById(R.id.list);

            adapter = new SimpleAdapter(context, outletArray, R.layout.nb_outlet_list_item,
                    new String[]{OUTLET_NAME, OUTLET_TYPE},
                    new int[]{R.id.outletname, R.id.outlettype});
            outletHolder.setAdapter(adapter);

            outletHolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String outletID = outletArray.get(i).get("outletid");
                    //Toast.makeText(getApplicationContext(), "You've selected : " + outletArray.get(i), Toast.LENGTH_SHORT).show();
                    if (outletArray.get(i).get("outlettype").equalsIgnoreCase("MVO")) {
                        Intent intent = new Intent(OutletPage.this, MvoQuestionnaireActivity.class);
                        intent.putExtra("USERNAME", uname);
                        intent.putExtra("OUTLETID", outletID);
                        startActivity(intent);
                    } else {
                        //Log.e("OUTLETID",outletID);
                        Intent intent = new Intent(OutletPage.this, MtoQuestionnaireActivity.class);
                        intent.putExtra("USERNAME", uname);
                        intent.putExtra("OUTLETID", outletID);
                        startActivity(intent);
                    }
                }
            });
            //setListAdapter(adapter);
            //outletHolder = getListView();

            //Selecting single ListView item
            //outletHolder = listActivity.getListView();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            JSONParser jsonParser = new JSONParser();

            //get JSON string from URL
            JSONArray json = jsonParser.getExistingOutletFromUrl(outletURL);

            for (int i = 0; i < json.length(); i++) {
                try {
                    JSONObject c = json.getJSONObject(i);
                    String outletname = c.getString(OUTLET_NAME);
                    String outlettype = c.getString(OUTLET_TYPE);
                    String outletid = c.getString(OUTLET_ID);

                    HashMap<String, String> map = new HashMap<>();

                    map.put(OUTLET_NAME, outletname.trim());
                    map.put(OUTLET_TYPE, outlettype.trim());
                    map.put(OUTLET_ID, outletid.trim());
                    outletArray.add(map);
                    //outletStore.add(map);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            /*for (int i = 0; i < 10; i++) {

                try {
                    JSONObject c = json.getJSONObject(i);
                    String outletname = c.getString(OUTLET_NAME);
                    String outlettype = c.getString(OUTLET_TYPE);
                    String outletid = c.getString(OUTLET_ID);

                    HashMap<String, String> map = new HashMap<>();

                    map.put(OUTLET_NAME, outletname.trim());
                    map.put(OUTLET_TYPE, outlettype.trim());
                    map.put(OUTLET_ID, outletid.trim());
                    outletArray.add(map);
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                }
            }*/
            // Log.i("OutletStoreSize", "" + outletStore.size());
            return null;
        }
    }

    private class SearchOutlet extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;

        Context context;

        public SearchOutlet(Activity listActivity) {
            context = listActivity;
            progressDialog = new ProgressDialog(context);
        }


        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Searching...\nPlease wait");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String string) {
            //SearchOutlet();
            //ListActivity listActivity = new ListActivity();
            if (progressDialog.isShowing()) progressDialog.dismiss();

            outletHolder = (ListView) findViewById(R.id.list);

            adapter = new SimpleAdapter(context, outletSearch, R.layout.nb_outlet_list_item,
                    new String[]{OUTLET_NAME, OUTLET_TYPE},
                    new int[]{R.id.outletname, R.id.outlettype});
            outletHolder.setAdapter(adapter);

            outletHolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String outletID = outletSearch.get(i).get("outletId");
                    //Toast.makeText(getApplicationContext(), "You've selected : " + outletArray.get(i), Toast.LENGTH_SHORT).show();
                    if (outletSearch.get(i).get("outlettype").equalsIgnoreCase("MVO")) {
                        Intent intent = new Intent(OutletPage.this, MvoQuestionnaireActivity.class);
                        intent.putExtra("USERNAME", uname);
                        intent.putExtra("OUTLETID", outletID);
                        startActivity(intent);
                    } else {
                        //Log.e("OUTLETID",outletID);
                        Intent intent = new Intent(OutletPage.this, MtoQuestionnaireActivity.class);
                        intent.putExtra("USERNAME", uname);
                        intent.putExtra("OUTLETID", outletID);
                        startActivity(intent);
                    }
                }
            });
            //setListAdapter(adapter);
            //outletHolder = getListView();

            //Selecting single ListView item
            //outletHolder = listActivity.getListView();
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser = new JSONParser();

            //get JSON string from URL
            JSONArray json = jsonParser.getSearchedOutlet(outletSearchURL);

            for (int i = 0; i < json.length(); i++) {

                try {
                    JSONObject c = json.getJSONObject(i);
                    String outletname = c.getString(OUTLET_NAME);
                    String outlettype = c.getString(OUTLET_TYPE);
                    String outletid = c.getString(OUTLET_SEARCH_ID);

                    HashMap<String, String> map = new HashMap<>();

                    map.put(OUTLET_NAME, outletname.trim());
                    map.put(OUTLET_TYPE, outlettype.trim());
                    map.put(OUTLET_SEARCH_ID, outletid.trim());
                    outletSearch.add(map);
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                }
            }
            Log.i("OutletStoreSize", "" + outletSearch.size());
            return null;
        }
    }
}
