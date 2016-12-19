package globalsoft.com.testgps;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by adebowale.odulaja on 7/2/16.
 */
public class JSONParser {

    static InputStream is = null;
    static JSONArray jarray = null;
    static JSONObject jsonObject = null;
    //public String json = "";
    public String line;

    public JSONParser(){

    }

    public JSONArray getJSONFromUrl(String url){

        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        try{
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200){
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null){
                    builder.append(line);
                }
            }
            else {
                Log.e("==>","Failed to downlaod file");
            }
        }
        catch (ClientProtocolException cpe){
            cpe.printStackTrace();
        }
        catch (IOException ioexception){
            ioexception.printStackTrace();
        }
        finally {

        }

        //try parse the JSON to an Object
        try{
            jarray = new JSONArray(builder.toString());
        }
        catch (JSONException ex){ex.printStackTrace();}

        //return the JSON string gotten
        return jarray;
    }

    public String getJSONLoginFromUrl(String url){

        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        try{
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200){
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));

                while ((line = reader.readLine()) != null){
                    builder.append(line);
                }
            }
            else {
                Log.e("==>","Failed to connect to the parsed URL");
            }
        }
        catch (ClientProtocolException cpe){
            cpe.printStackTrace();
        }
        catch (IOException ioexception){
            ioexception.printStackTrace();
        }
        finally {

        }

        //return the JSON object
        return builder.toString();
    }

    public JSONArray getExistingOutletFromUrl(String url){
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        try{
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200){
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null){
                    builder.append(line);
                    //Log.i("OutletList",line+"\n");
                }
            }
            else {
                Log.e("==>","Failed load existing outlets");
            }
        }
        catch (ClientProtocolException cpe){
            cpe.printStackTrace();
        }
        catch (IOException ioexception){
            ioexception.printStackTrace();
        }
        finally {

        }

        //try parse the JSON to an Object
        try{
            jarray = new JSONArray(builder.toString().trim());
        }
        catch (JSONException ex){ex.printStackTrace();}

        //return the JSON string gotten
        return jarray;
    }

    public JSONArray getSearchedOutlet(String url){
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        try{
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200){
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null){
                    builder.append(line);
                }
            }
            else {
                Log.e("==>","Something went wrong \nUnable to complete search task");
            }
        }
        catch (ClientProtocolException cpe){
            cpe.printStackTrace();
        }
        catch (IOException ioexception){
            ioexception.printStackTrace();
        }
        finally {

        }

        //try parse the JSON to an Object
        try{
            jarray = new JSONArray(builder.toString().trim());
        }
        catch (JSONException ex){ex.printStackTrace();}

        //return the JSON string gotten
        return jarray;
    }

    public JSONArray getMTOQuestions(String url){
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        try{
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200){
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null){
                    builder.append(line);
                    //Log.i("OutletList",line+"\n");
                }
            }
            else {
                Log.e("MTOQUESTION","Failed load MTO questionnaire");
            }
        }
        catch (ClientProtocolException cpe){
            cpe.printStackTrace();
        }
        catch (IOException ioexception){
            ioexception.printStackTrace();
        }
        finally {

        }

        //try parse the JSON to an Object
        try{
            jarray = new JSONArray(builder.toString().trim());
        }
        catch (JSONException ex){ex.printStackTrace();}

        //return the JSON string gotten
        return jarray;
    }

    public JSONArray getFromURL(String url){
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        try{
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200){
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null){
                    builder.append(line);
                }
            }
            else {
                Log.e("Inside getFromURL","Something went wrong");
            }
        }
        catch (ClientProtocolException cpe){
            cpe.printStackTrace();
        }
        catch (IOException ioexception){
            ioexception.printStackTrace();
        }
        finally {

        }

        //try parse the JSON to an Object
        try{
            jarray = new JSONArray(builder.toString().trim());
        }
        catch (JSONException ex){ex.printStackTrace();}

        //return the JSON string gotten
        return jarray;
    }

}
