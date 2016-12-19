package globalsoft.com.testgps;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by adebowale.odulaja on 7/2/16.
 */
public class JsonTest extends ListActivity {

    private static final String url = "http://docs.blackberry.com/sampledata.json";

    private static final String TAG_VTYPE = "vehicleType";
    private static final String TAG_VCOLOR = "vehicleColor";
    private static final String TAG_FUEL = "fuel";
    private static final String TAG_TREAD = "treadType";

    ArrayList <HashMap<String, String>> jsonlist = new ArrayList<>();

    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.json_test);
        new ProgressTask(JsonTest.this).execute();
    }


    private class ProgressTask extends AsyncTask<String, Void, Boolean>{
        private ProgressDialog pd;

        public ProgressTask(ListActivity listActivity){
            context = listActivity;
            pd = new ProgressDialog(context);
        }//Progress dialog task

        //application context
        private final Context context;

        protected void onPreExecute(){
            this.pd.setMessage("Starting download");
            //this.pd.show();
        }

        @Override
        protected void onPostExecute(final Boolean success){
            if(pd.isShowing()) this.pd.dismiss();

            ListAdapter adapter = new SimpleAdapter(context, jsonlist,R.layout.list_item_test,
                    new String[]{TAG_VTYPE, TAG_VCOLOR, TAG_FUEL, TAG_TREAD},
                    new int[]{R.id.vehicleType,R.id.vehicleColor,R.id.fuel,R.id.treadType});
            setListAdapter(adapter);

            //Selecting single ListView item
            lv = getListView();


        }

        protected Boolean doInBackground(final String... args){
            JSONParser jsonParser = new JSONParser();

            //get JSON string from URL
            JSONArray json = jsonParser.getJSONFromUrl(url);

            for(int i =0; i<json.length(); i++){

                try{
                    JSONObject c = json.getJSONObject(i);
                    String vtype = c.getString(TAG_VTYPE);
                    String vcolor = c.getString(TAG_VCOLOR);
                    String vfuel = c.getString(TAG_FUEL);
                    String vtread = c.getString(TAG_TREAD);

                    HashMap<String, String> map = new HashMap<>();

                    map.put(TAG_VTYPE, vtype);
                    map.put(TAG_VCOLOR, vcolor);
                    map.put(TAG_FUEL, vfuel);
                    map.put(TAG_TREAD, vtread);
                    jsonlist.add(map);
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
                finally {

                }
            }
            return null;
        }

    }
}
