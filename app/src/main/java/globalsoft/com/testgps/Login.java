package globalsoft.com.testgps;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import globalsoft.com.dialoginterface.AlertDialogBuilder;
//import android.widget.Toolbar;


/**
 * Created by adebowale.odulaja on 6/28/16.
 */
public class Login extends AppCompatActivity implements View.OnClickListener {
    private Button buttonLogin;
    private EditText editUsername, editPassword;
    private String username, password;
    private String url;
    Toolbar toolbar;
    AlertDialogBuilder alert = new AlertDialogBuilder();

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Trade Marketing Audit Soln");
        toolbar.setSubtitle("Please login...");
        toolbar.setNavigationIcon(R.mipmap.nb_launcher);

        editUsername = (EditText) findViewById(R.id.editUsername);
        editPassword = (EditText) findViewById(R.id.editPassword);

        buttonLogin = (Button) findViewById(R.id.btnLogin);
        buttonLogin.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            return true;
        } else if (id == R.id.action_about) {
            String version = "Version "+BuildConfig.VERSION_NAME;
            alert.showAlertDialog(Login.this, version, "About", R.mipmap.nb_launcher);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        int btnClicked = view.getId();

        if (btnClicked == R.id.btnLogin) {
            username = editUsername.getText().toString();
            password = editPassword.getText().toString();
            url = "http://www.nbappserver.com/nbpage/nbapi.php?optype=anthenticate&username=" + username + "&password=" + password + "";
            //url = "http://populationassociationng.org/nbpage/nbapi.php?optype=anthenticate&username=" + username + "&password=" + password + "";
            //Intent in = new Intent(Login.this, OutletPage.class);
            if (!isConnected()) {
                alert.showAlertDialog(Login.this, "Hey! you're not connected to the internet", "No Connectivity", R.mipmap.alerticon);
            } else if (username.isEmpty() || password.isEmpty()) {
                alert.showAlertDialog(Login.this, "Please enter a valid username and/or password", "Invalid Login", R.mipmap.alerticon);
                return;
            } else {
                new LoginTask(Login.this).execute();
            }
        }

    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) Login.this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private class LoginTask extends AsyncTask<String, Void, String> {
        private ProgressDialog pd;
        //application context
        private final Context context;

        public LoginTask(Activity activity) {
            context = activity;
            pd = new ProgressDialog(context);
        }//Progress dialog task

        protected void onPreExecute() {
            //this.pd.setCancelable(false);
            this.pd.setMessage("Authenticating...");
            this.pd.show();
        }

        @Override
        protected void onPostExecute(String success) {
            if (pd.isShowing()) {
                this.pd.dismiss();
            }
            if (pd != null) pd = null;
            Log.i("OnPostExecute", success);
            success = success.replace("\"", "");

            //String success2 = success.replaceAll("\"","\\\"");
            //String value = " \"true\" ";

            if (success.equals("true")) {
                Intent in = new Intent(Login.this, OutletPage.class);
                in.putExtra("USERNAME", editUsername.getText().toString());
                startActivity(in);
            } else {
                alert.showAlertDialog(Login.this, "Wrong username and/or password", "Authentication Failed",
                        R.mipmap.alerticon);
            }

        }

        protected String doInBackground(String... args) {
            JSONParser jsonParser = new JSONParser();

            //get JSON string from URL
            Log.i("URL", url);
            String theValue = jsonParser.getJSONLoginFromUrl(url);

            // Log.i("JSON", theValue);

            return theValue;
        }

    }

}
