package globalsoft.com.testgps;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import globalsoft.com.dialoginterface.AlertDialogBuilder;
//import android.widget.Toolbar;


/**
 * Created by adebowale.odulaja on 6/28/16.
 */
public class SubQuestion extends Activity implements View.OnClickListener {
    private static final String ID = "id";
    private static final String SUB_QUESTION = "subquestion";
    private static final String QUESTION_ID = "questionid";
    private String url, questionid, subQuestionID, lastSubQuestID;

    ArrayList<HashMap<String, String>> subquestionarray = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> subQuestOptionHolder = new ArrayList<>();
    ArrayList<String> subquest = new ArrayList<>();
    ArrayList<String> subquestid = new ArrayList<>();
    //Toolbar toolbar;
    AlertDialogBuilder alert = new AlertDialogBuilder();
    Bundle bundle;
    Iterator itr, itrID;
    int selectedRadio;
    RadioButton radioButton;
    RadioGroup radioGroup;
    private Button buttonNext;
    private TextView textViewSubQuest, alertText;

    //MvoQuestionnaireActivity mvo;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questonnaire_subquestion_loader);

        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //getSupportActionBar().setTitle("Marketing Audit Solution");
        //toolbar.setSubtitle("Please login...");
        //toolbar.setNavigationIcon(R.mipmap.nb_launcher);

        bundle = getIntent().getExtras();
        questionid = bundle.getString("QUEST_ID");
        url = "http://www.nbappserver.com/nbpage/nbapi.php?optype=getsubquestions&questionid=" + questionid + "";
        //url = "http://populationassociationng.org/nbpage/nbapi.php?optype=getsubquestions&questionid=5";

        new GetSubQuestion(SubQuestion.this).execute();

        textViewSubQuest = (TextView) findViewById(R.id.textSubQuestion);
        textViewSubQuest.setText("");

        buttonNext = (Button) findViewById(R.id.btnNext);
        buttonNext.setOnClickListener(this);

        alertText = (TextView) findViewById(R.id.textAlert1);


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
        }

        return super.onOptionsItemSelected(item);
    }

    private void addSubQuestion() {
        String aa;
        for (int i = 0; i < subquestionarray.size(); i++) {
            aa = subquestionarray.get(i).get("subquestion");
            subquest.add(aa);
        }
        if (itr == null) {
            itr = subquest.iterator();
            if (itr.hasNext()) {
                textViewSubQuest.setText(itr.next().toString());
            }
            // System.out.println("Iterator has more element? " + itr.hasNext());
        }
        //Log.i("SUBQUESTION==>", "" + subquest);
    }

    private void getSubQuestionID() {
        String id;
        for (int i = 0; i < subquestionarray.size(); i++) {
            id = subquestionarray.get(i).get("id");
            subquestid.add(id);
        }

        if (itrID == null) {
            itrID = subquestid.iterator();
        }
    }

    private void getLastSubQuestionID() {
        while (itrID.hasNext()) {
            lastSubQuestID = itrID.next().toString();
        }
    }

    public void onClick(View view) {
        int btnClicked = view.getId();

        if (btnClicked == R.id.btnNext) {
            radioGroup = (RadioGroup) findViewById(R.id.radioSubQuestions);
            selectedRadio = radioGroup.getCheckedRadioButtonId();
            radioButton = (RadioButton) findViewById(selectedRadio);
            //Iterator itr = subquest.iterator();
            if (alertText.getText() != "") {
                alertText.setText("");
            }
            if (selectedRadio < 0) {
                alertText.setText("Please select an option.");
            } else if (itr.hasNext() && itrID.hasNext()) {
                //System.out.println("" + itr.next());
                textViewSubQuest.setText(itr.next().toString());
                System.out.println(radioButton.getText().toString());
                RadioGroup dd = (RadioGroup) findViewById(R.id.radioSubQuestions);
                dd.clearCheck();

                String QUEST_ID = "questionid", QUEST_SUBQUESID = "subquestionid", QUEST_ANS = "answer";
                HashMap<String, String> answersubquestion = new HashMap<>();
                answersubquestion.put(QUEST_ID, questionid);
                /*if (itrID.hasNext()) {
                    subQuestionID = itrID.next().toString();
                    answersubquestion.put(QUEST_SUBQUESID, subQuestionID);
                }*/
                subQuestionID = itrID.next().toString();
                answersubquestion.put(QUEST_SUBQUESID, subQuestionID);
                //answersubquestion.put(QUEST_SUBQUESID, "" + 1);
                answersubquestion.put(QUEST_ANS, radioButton.getText().toString());

                subQuestOptionHolder.add(answersubquestion);
                System.out.println("Value in SubQuestionHolder: " + subQuestOptionHolder);
            }

            if (!itr.hasNext()) {
                System.out.println("This is the last question...");
                String QUEST_ID = "questionid", QUEST_SUBQUESID = "subquestionid", QUEST_ANS = "answer";
                HashMap<String, String> answersubquestion = new HashMap<>();
                answersubquestion.put(QUEST_ID, questionid);
                getLastSubQuestionID();
                answersubquestion.put(QUEST_SUBQUESID, lastSubQuestID);
                answersubquestion.put(QUEST_ANS, radioButton.getText().toString());

                subQuestOptionHolder.add(answersubquestion);
                super.finish();

                System.out.println("Value in SubQuestionHolder: " + subQuestOptionHolder);
            }
        }

    }


    private class GetSubQuestion extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;

        Context context;

        public GetSubQuestion(Activity listActivity) {
            context = listActivity;
            progressDialog = new ProgressDialog(context);
        }


        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...\nPlease wait");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String string) {
            //SearchOutlet();
            //ListActivity listActivity = new ListActivity();
            if (progressDialog.isShowing()) progressDialog.dismiss();
            if (progressDialog != null) progressDialog = null;
            addSubQuestion();
            getSubQuestionID();
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser = new JSONParser();

            //get JSON string from URL
            JSONArray json = jsonParser.getFromURL(url);

            for (int i = 0; i < json.length(); i++) {

                try {
                    JSONObject c = json.getJSONObject(i);
                    String id = c.getString(ID);
                    String subquestion = c.getString(SUB_QUESTION);
                    String questionID = c.getString(QUESTION_ID);

                    HashMap<String, String> map = new HashMap<>();

                    map.put(ID, id.trim());
                    map.put(SUB_QUESTION, subquestion.trim());
                    map.put(QUESTION_ID, questionID.trim());
                    subquestionarray.add(map);
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                }
            }
            // Log.i("SubQuestionSize", "" + subquestionarray.size());
            return null;
        }
    }

}
