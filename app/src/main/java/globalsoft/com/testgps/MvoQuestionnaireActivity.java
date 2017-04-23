package globalsoft.com.testgps;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import globalsoft.com.dialoginterface.AlertDialogBuilder;
import globalsoft.com.reversegeocode.LocationLocator;

public class MvoQuestionnaireActivity extends AppCompatActivity {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    ArrayList<HashMap<String, String>> questans = new ArrayList<>();
    ArrayList<HashMap<String, String>> optionHolder = new ArrayList<>();
    private HashMap<String, String> answersubquestion;
    public static ArrayList<HashMap<String, String>> subQuestOptionHolder = new ArrayList<>();
    //public static ArrayList<HashMap<String, String>> subQuestOptionHolder = new ArrayList<>();

    HashMap listDataChildStates;

    Button btnSave;
    RadioButton radioButton;
    Spinner spinner;

    Button btnTakePic;
    String ba1;
    Bitmap bm;
    String mCurrentPhotoPath;
    ImageView mImageView;
    int groupposition, childposition;

    AlertDialogBuilder alert = new AlertDialogBuilder();
    LocationLocator locationLocator;
    SubQuestion subQuest;
    String[] answerdropdown, answerdropdownprom;

    Toolbar toolbar;


    int selectedRadio;
    String question, qid, subquestid, picture, anstype;
    public int childID = 0;
    private String uname, outletid;
    private String HeaderString;
    private static final String mvoURL = "http://www.nbappserver.com/nbpage/nbapi.php?optype=questions&outlettype=MVO";
    private static final String QUESTION = "question";
    private static final String QUESTION_ID = "questionid";
    private static final String SUB_QUEST_ID = "subquestionid";
    private static final String QUESTTION_PICTURE = "picture";
    private static final String ANSWER_TYPE = "answertype";

    private String availabilityOption[];//= {"Once a week or more", "Twice a month", "Once a quarter", "Less than once a quarter"};
    private String promotionOption[];//= {"Weekly", "Monthly", "Quarterly", "Yearly", "Never"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e("Error" + Thread.currentThread().getStackTrace()[2], paramThrowable.getLocalizedMessage());
            }
        });
        setContentView(R.layout.mvo_questionnaire);
        Bundle bundle = getIntent().getExtras();
        uname = bundle.getString("USERNAME");
        outletid = bundle.getString("OUTLETID");
        locationLocator = new LocationLocator(MvoQuestionnaireActivity.this);
        locationLocator.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationLocator.updateGPS();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Trade Marketing Audit Soln");
        toolbar.setSubtitle("Questionnaire");
        toolbar.setNavigationIcon(R.mipmap.nb_launcher);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        //prepareListData();
        new GetMVOQuestionnaire(MvoQuestionnaireActivity.this).execute();

        //listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        //expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                //Toast.makeText(getApplicationContext(), listDataHeader.get(groupPosition) + " Expanded", Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                //Toast.makeText(getApplicationContext(), listDataHeader.get(groupPosition) + " Collapsed", Toast.LENGTH_SHORT).show();
            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                groupposition = groupPosition;
                childposition = childPosition;
                HeaderString = listDataHeader.get(groupPosition);
                childID = (int) listAdapter.getChildId(groupPosition, childPosition);
                Log.e("NEWVALUE_OF_CHILDID", "" + childID);
                String confirm1 = "take a picture", confirm2 = "Take a Picture", confirm3 = "please take picture", confirm4 = "Please take picture",
                        confirm5 = "take picture", dropdownquest1 = "How often", dropdownquest2 = "What is the frequency of NB";
                //Toast.makeText(getApplicationContext(), listDataHeader.get(groupPosition) + " : " + listDataChild.get(
                //      listDataHeader.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT).show();
                String text = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                if (loadPicture(childID).equals("1YES") || loadPicture(childID).equals("1NO")) {
                    loadDialogWithPicture(text);
                    //Log.e("Inside LoadPicture","LoadPic");
                }
                //if (text.contains(confirm1) || text.contains(confirm2) || text.contains(confirm3) || text.contains(confirm4) || text.contains(confirm5)) {
                //Toast.makeText(MvoQuestionnaireActivity.this, "This question needs picture", Toast.LENGTH_LONG).show();
                //  loadDialogWithPicture(text);
                //}

                else if (!loadAnsType(childID).equalsIgnoreCase("d")) {
                    loadDialogForAvailability(text);
                    //Log.e("Inside AnswerType",loadAnsType(childID));
                }
                /*else if (HeaderString.equalsIgnoreCase("Availability") && text.contains(dropdownquest1)) {
                    loadDialogForAvailability(text);
                } else if (HeaderString.equalsIgnoreCase("Promotion") && text.contains(dropdownquest2)) {
                    loadDialogForAvailability(text);
                }*/
                else {
                    loadDialog(text);
                    //Log.e("Inside FinalElse","Final Else");
                }
                //int index = parent.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                //parent.setItemChecked(index, true);
                //listAdapter.getChildView(groupPosition,childID,false,v,parent).findViewById(R.id.lblListItem).setBackgroundColor(0xFF697B5D);
                return true;
            }
        });

        btnSave = (Button) findViewById(R.id.btnSaveQuestionnaire);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (optionHolder.size() <= 0) {
                    alert.showAlertDialog(MvoQuestionnaireActivity.this, "You haven't answer any of the questions!", "Nothing to save", 0);
                } else if (optionHolder.size() < questans.size()) {
                    alert.showAlertDialog(MvoQuestionnaireActivity.this, "You must answer all questions!\nYou've answered " + optionHolder.size()
                            + " out of " + questans.size(), "Questionnaire", 0);
                } else {
                    if (subQuestOptionHolder.size() > 0) {
                        //System.out.println("SizeOfSubQuestionAnswer: "+subQuest.subQuestOptionHolder.size());
                        new SubmitSubQuestion().execute();
                        new SubmitQuestion().execute();
                    } else {
                        new SubmitQuestion().execute();
                    }

                }

                //Toast.makeText(getApplicationContext(), "Questionnaire has been saved", Toast.LENGTH_LONG).show();
            }
        });
    }

    public String getQuestionId(int j) {
        String questID = "";
        for (int i = 0; i < questans.size(); i++) {
            questID = questans.get(j).get("questionid");
        }
        Log.e("QuestionID", questID);
        return questID;
    }

    public String getSubQuestionId(int j) {
        String subQuestID = "";
        for (int i = 0; i < questans.size(); i++) {
            subQuestID = questans.get(j).get("subquestionid");
        }
        //Log.e("SubQuestionID", subQuestID);
        return subQuestID;
    }

    public String loadPicture(int j) {
        String picture = "";
        for (int i = 0; i < questans.size(); i++) {
            picture = questans.get(j).get("picture");
        }
        if (picture != "") {
            Log.e("PictureValue", " " + picture);
        }
        return picture;
    }

    public String loadAnsType(int j) {
        String answertype = "";
        for (int i = 0; i < questans.size(); i++) {
            answertype = questans.get(j).get("answertype");
        }
        return answertype;
    }

    public String[] loadAnsAvailDropDown(int x) {
        //String[] answerdropdown;
        for (int i = 0; i < questans.size(); i++) {
            answerdropdown = questans.get(x).get("answertype").split("-");
        }
        return answerdropdown;
    }

    public String[] loadAnsPromDropDown(int x) {
        for (int i = 0; i < questans.size(); i++) {
            if (questans.get(x).get("answertype").contains(":")) {
                answerdropdownprom = questans.get(x).get("answertype").split(":");
            } else if (questans.get(x).get("answertype").contains("-")) {
                answerdropdownprom = questans.get(x).get("answertype").split("-");
            }
        }
        return answerdropdownprom;
    }


    private String loadDialog(String questionText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.questonnaireloader, null);
        TextView textv = (TextView) view.findViewById(R.id.textQuestion);
        textv.setText(questionText);
        // 2. Chain together various setter methods to set the dialog characteristics
        // builder.setView(inflater.inflate(R.layout.questonnaireloader, null).findViewById(R.id.textQuestion));

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioOption);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                int a = checkedId;
                System.out.println("Checked ID: " + a);
                if (a == 2131492997 && getSubQuestionId(childID).equals("1")) {
                    //Intent loadSubQuestion = new Intent(MvoQuestionnaireActivity.this, SubQuestion.class);
                    Intent loadSubQuestion = new Intent(MvoQuestionnaireActivity.this, SubQuestion.class);
                    loadSubQuestion.putExtra("QUEST_ID", getQuestionId(childID));
                    loadSubQuestion.putExtra("OUTLETID", outletid);
                    startActivityForResult(loadSubQuestion, 2);// Activity is started with requestCode 2
                    //startActivity(loadSubQuestion);

                    /*if (subQuestOptionHolder.size() != 0 || subQuest.subQuestOptHolder.size() != 0) {
                        subQuestOptionHolder.clear();
                    }*/
                }
            }
        });

        builder.setView(view);
        //.setMessage("Select an option")
        builder.setTitle("Questionnaire")
                .setIcon(R.mipmap.nb_launcher);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //dialog.dismiss();
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        Button theButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        theButton.setOnClickListener(new CustomListener(dialog));

        return questionText;
    }

    private String loadDialogForAvailability(String questionText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.questonnaire_drop_down_loader, null);
        TextView textv = (TextView) view.findViewById(R.id.textQuestion);
        textv.setText(questionText);

        availabilityOption = loadAnsPromDropDown(childID);
        promotionOption = loadAnsPromDropDown(childID);

        if (HeaderString.equalsIgnoreCase("Availability")) {
            spinner = (Spinner) view.findViewById(R.id.availabilityOption);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MvoQuestionnaireActivity.this, android.R.layout.simple_list_item_1, availabilityOption);
            spinner.setAdapter(adapter);
        } else {
            spinner = (Spinner) view.findViewById(R.id.availabilityOption);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MvoQuestionnaireActivity.this, android.R.layout.simple_list_item_1, promotionOption);
            spinner.setAdapter(adapter);
        }

        // 2. Chain together various setter methods to set the dialog characteristics
        // builder.setView(inflater.inflate(R.layout.questonnaireloader, null).findViewById(R.id.textQuestion));
        builder.setView(view);
        //.setMessage("Select an option")
        builder.setTitle("Questionnaire")
                .setIcon(R.mipmap.nb_launcher);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //dialog.dismiss();
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        Button theButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        theButton.setOnClickListener(new CustomListener(dialog));

        return questionText;
    }

    private String loadDialogWithPicture(String questionText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.questonnaire_picture_loader, null);
        TextView textv = (TextView) view.findViewById(R.id.textQuestion);
        textv.setText(questionText);
        btnTakePic = (Button) view.findViewById(R.id.btnTakePicture);
        mImageView = (ImageView) view.findViewById(R.id.imageView);

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioOption);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                int a = checkedId;
                if (a == 2131492997 && getSubQuestionId(childID).equals("1")) {
                    Intent loadSubQuestion = new Intent(MvoQuestionnaireActivity.this, SubQuestion.class);
                    loadSubQuestion.putExtra("QUEST_ID", getQuestionId(childID));
                    loadSubQuestion.putExtra("OUTLETID", outletid);
                    startActivityForResult(loadSubQuestion, 2);// Activity is started with requestCode 2

                    /*if (subQuestOptionHolder.size() != 0 || subQuest.subQuestOptHolder.size() != 0) {
                        subQuestOptionHolder.clear();
                    }*/
                }
            }
        });


        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });
        // 2. Chain together various setter methods to set the dialog characteristics
        // builder.setView(inflater.inflate(R.layout.questonnaireloader, null).findViewById(R.id.textQuestion));
        builder.setView(view);
        //.setMessage("Select an option")
        builder.setTitle("Questionnaire")
                .setIcon(R.mipmap.nb_launcher);


        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //dialog.dismiss();
            }
        })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        Button theButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        theButton.setOnClickListener(new CustomListener(dialog));

        return questionText;
    }

    class CustomListener implements View.OnClickListener {
        private final Dialog dialog;

        public CustomListener(Dialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void onClick(View v) {
            String confirm1 = "take a picture", confirm2 = "Take a Picture", confirm3 = "Take a Picture if Yes", confirm4 = "if you answer no";
            String text = listDataChild.get(listDataHeader.get(groupposition)).get(childposition);
            // put your code here
            //if (HeaderString.equalsIgnoreCase("Availability") || HeaderString.equalsIgnoreCase("Promotion")) {

            //    }
            if (loadAnsType(childID).equalsIgnoreCase("d")) {
                RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioOption);
                selectedRadio = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) dialog.findViewById(selectedRadio);
            }
            TextView alert = (TextView) dialog.findViewById(R.id.textAlert);
            /*for (int i = 0; i < optionHolder.size(); i++) {
                if (optionHolder.get(i).get("id").equals(getQuestionId(childID))) {
                    alert.setText("You've already answered this question!\nPress cancel to answer a new question");
                    return;
                }
            }*/

            if (selectedRadio < 0) {
                alert.setText("Please select an option.");
            } /*else if (radioButton.getText().toString().equalsIgnoreCase("Yes") && getSubQuestionId(childID).equals("1")) {
                Log.e("SUB_QUESTION_ID_VALUE", getSubQuestionId(childID));
                Intent loadSubQuestion = new Intent(MvoQuestionnaireActivity.this, SubQuestion.class);
                loadSubQuestion.putExtra("QUEST_ID", getQuestionId(childID));
                startActivity(loadSubQuestion);
            }*///else if ((text.contains(confirm1) || text.contains(confirm2)) && radioButton.getText().toString().equals("Yes") && mImageView.getDrawable() == null) {
            else if (loadPicture(childID).equals("1YES") && radioButton.getText().toString().equals("Yes") && mImageView.getDrawable() == null) {
                alert.setText("Image can not be empty if your option is \"Yes\"");
                return;
            }//else if (text.contains(confirm4) && radioButton.getText().toString().equals("No") && mImageView.getDrawable() == null) {
            else if (loadPicture(childID).equals("1NO") && radioButton.getText().toString().equals("No") && mImageView.getDrawable() == null) {
                alert.setText("Image can not be empty if your option is \"No\"");
                return;
            } else {
                String QUEST_ANS = "answer", QUEST_USERNAME = "username", QUEST_OUTLETID = "outletid", QUEST_PIC = "picture", QUEST_ID = "id", QUEST_GPS = "gps";//, FILE_NAME = "name";
                //String fileName = ""+System.currentTimeMillis();
                Double gps_val = locationLocator.longitude;

                HashMap<String, String> optionMap = new HashMap<>();
                optionMap.put(QUEST_ID, getQuestionId(childID));
                //if (HeaderString.equalsIgnoreCase("Availability") || HeaderString.equalsIgnoreCase("Promotion")) {
                if (!loadAnsType(childID).equalsIgnoreCase("d")) {
                    optionMap.put(QUEST_ANS, "" + spinner.getSelectedItem());
                } else {
                    optionMap.put(QUEST_ANS, radioButton.getText().toString());
                }
                optionMap.put(QUEST_USERNAME, uname);
                optionMap.put(QUEST_PIC, ba1);
                optionMap.put(QUEST_OUTLETID, outletid);
                optionMap.put(QUEST_GPS, "Long: " + locationLocator.longitude + " Lat: " + locationLocator.latitude);
                //optionMap.put(FILE_NAME, fileName);
                optionHolder.add(optionMap);
                dialog.dismiss();
                ba1 = null;


                Log.e("OPTION_HOLDER", "" + optionHolder);
                Log.e("ID From activity result", "" + subQuestOptionHolder);
                // Log.e("ba1", "" + ba1);
            }
        }
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (subQuestOptionHolder.size() != 0 || subQuest.subQuestOptHolder.size() != 0) {
            subQuestOptionHolder.clear();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mvoquestionnaire, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            alert.showAlertDialogWithOption(MvoQuestionnaireActivity.this, "Are you sure you want to exit?", "Confirm", R.mipmap.nb_launcher);
        } else if (id == R.id.action_about) {
            String version = "Version " + BuildConfig.VERSION_NAME;
            alert.showAlertDialog(MvoQuestionnaireActivity.this, version, "About", R.mipmap.nb_launcher);
        }


        return super.onOptionsItemSelected(item);
    }


    private void uploadImage() {
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        //Log.e("The value of Image W n H","TargW"+targetW+" TargetH "+targetH+" PhotoW"+photoW+" PhotoH"+photoH);

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        if (bm != null && !bm.isRecycled()) {
            bm.recycle();
            bm = null;
        }

        bm = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ba1 = Base64.encodeToString(ba, Base64.NO_WRAP);

        //Log.e("BASE64VALUE==>", ba1);
    }

    private void captureImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, 100);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 100 && resultCode == RESULT_OK) {
            setPic();
            uploadImage();
        }

        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 2) {
            String QUEST_ID = "questionid", OUTLET_ID = "outletid", QUEST_SUBQUESID = "subquestionid", QUEST_ANS = "answer";
            answersubquestion = new HashMap<>();
            String id = data.getStringExtra("questID");
            String outletid = data.getStringExtra("outletid");
            String subqid = data.getStringExtra("subQuestID");
            String ans = data.getStringExtra("answer");

            answersubquestion.put(QUEST_ID, id);
            answersubquestion.put(OUTLET_ID, outletid);
            answersubquestion.put(QUEST_SUBQUESID, subqid);
            answersubquestion.put(QUEST_ANS, ans);
        }
        subQuestOptionHolder.add(answersubquestion);
        //System.out.println("ID From activity result " + subQuestOptionHolder);

    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        //Log.e("The value of Image W n H","TargW"+targetW+" TargetH "+targetH+" PhotoW"+photoW+" PhotoH"+photoH);

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        //Log.e("Getpath", "Cool" + mCurrentPhotoPath);
        return image;
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // Adding header data
        listDataHeader.add("Visibility");
        listDataHeader.add("Quality");
        listDataHeader.add("Persuation");
        listDataHeader.add("Availability");
        listDataHeader.add("Affordability");
        listDataHeader.add("Promotion");

        // Adding child data
        List<String> visibility = new ArrayList<>();
        visibility.add("Branded NB items are clean and well maintained?");
        visibility.add("Canopies?");
        visibility.add("Lightboxes?");
        visibility.add("Framed banners?");
        visibility.add("Are there are any competitive trade infrastructures available?");
        visibility.add("Is there on display a current NB Campaign Poster ");
        visibility.add("Are there any outdated NB campaign Posters on display");

        List<String> quality = new ArrayList<>();
        quality.add("Is there a Branded NB Chiller at outlet?");
        quality.add("Is there a NB Barcode on the Chiller?");
        quality.add("Drinks where available cold?");
        quality.add("Drinks where presented using same brand glasses or coasters? ");
        //quality.add("Red 2");
        //quality.add("The Wolverine");

        List<String> persuation = new ArrayList<>();
        persuation.add("Bar staff actively offered or mentioned NB Brands?");
        persuation.add("Bar staff are knowledgeable about NB brands?");
        persuation.add("Bar staff are neatly dressed in Branded uniform?");
        //persuation.add("The Canyons");
        //persuation.add("Europa Report");

        List<String> availability = new ArrayList<>();
        availability.add("All NB Brands/SKU are available");
        availability.add("Are all brands seen in NB Chillers?");
        availability.add("Drinks have been arranged in line with their corresponding chiller planogram?");
        availability.add("Does an NB sales Executive or Van Salesman or RTMM Visit the Outlet");
        availability.add("How often?");
        availability.add("Are the outlet service complaints addressed and resolved on time?");

        List<String> affordability = new ArrayList<>();
        affordability.add("Visibility material communicating Brand prices can be seen?");
        affordability.add("Communicated prices are the same as the purchase price?");
        affordability.add("Are NB prices comparative to competitor brands?");
        //affordability.add("The Canyons");
        //affordability.add("Europa Report");

        List<String> promotion = new ArrayList<>();
        promotion.add("Are there any competition promotion running in the bar (i.e. Sampling, in-bar activations)?");
        promotion.add("Is there any NB promotion currently running in the outlets?");
        promotion.add("Is there a Promotional communication Poster visible in Outlet?");
        promotion.add("Is the date on the poster still valid?");
        promotion.add("What is the frequency of NB promotions run in Outlet?");

        listDataChild.put(listDataHeader.get(0), visibility); // Header, Child data
        listDataChild.put(listDataHeader.get(1), quality);
        listDataChild.put(listDataHeader.get(2), persuation);
        listDataChild.put(listDataHeader.get(3), availability);
        listDataChild.put(listDataHeader.get(4), affordability);
        listDataChild.put(listDataHeader.get(5), promotion);
    }


    private class GetMVOQuestionnaire extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;

        Context context;

        public GetMVOQuestionnaire(Activity listActivity) {
            context = listActivity;
            progressDialog = new ProgressDialog(context);
        }

        //public ExisitngOutletTask(OutletPage outletPage) {

        //}

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading Question(s)\nPlease wait...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String string) {
            //ListActivity listActivity = new ListActivity();
            if (progressDialog.isShowing()) progressDialog.dismiss();
            listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);
            // setting list adapter
            expListView.setAdapter(listAdapter);
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser = new JSONParser();
            listDataHeader = new ArrayList<>();
            listDataChild = new HashMap<>();
            int qID = 0;

            //get JSON string from URL
            JSONArray json = jsonParser.getMTOQuestions(mvoURL);

            try {
                //JSONObject c = json.getJSONObject(HEADER1);
                //JSONArray jsonArray = json.getJSONArray(0);

                for (int i = 0; i < json.length(); i++) {
                    //Log.e("INSIDE_MTO",""+c.getString(HEADER1));
                    //String question = c.getString(QUESTION);
                    JSONObject job = json.getJSONObject(i);
                    Iterator<?> keys = job.keys();
                    //int child = 0;
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        try {
                            //Log.e("INSIDE_MTO", "" + key);
                            listDataHeader.add(key);

                            JSONArray headers = job.getJSONArray(key);
                            List<String> questionList = new ArrayList<>();
                            for (int quest_in_headr = 0; quest_in_headr < headers.length(); quest_in_headr++) {
                                qID += 1;
                                JSONObject jsonHeader1 = headers.getJSONObject(quest_in_headr);
                                question = jsonHeader1.getString(QUESTION);
                                qid = jsonHeader1.getString(QUESTION_ID);
                                subquestid = jsonHeader1.getString(SUB_QUEST_ID);
                                picture = jsonHeader1.getString(QUESTTION_PICTURE);
                                anstype = jsonHeader1.getString(ANSWER_TYPE);

                                HashMap<String, String> map = new HashMap<>();
                                map.put(QUESTION_ID, qid.trim());
                                map.put(QUESTION, question);
                                map.put(SUB_QUEST_ID, subquestid);
                                map.put(QUESTTION_PICTURE, picture);
                                map.put(ANSWER_TYPE, anstype);
                                questans.add(map);
                                questionList.add(qID + "." + question.trim());
                                //Log.e("INSIDE_INNER_LOOP", question + " " + qid);
                                //Log.e("ADD_DATA", "" + question);
                            }
                            listDataChild.put(listDataHeader.get(i), questionList);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    //Log.e("VALUES_IN_QUESTANS", "" + json.length());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } finally {

            }
            return null;
        }
    }

    private class SubmitQuestion extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog = new ProgressDialog(MvoQuestionnaireActivity.this);

        @Override
        protected String doInBackground(String... strings) {
            String result = "", st = "";
            String question_answer_url;
            long total = 0;
            //String gps = "3.3821783333333335 and 6.524561666666667";

            for (int i = 0; i < optionHolder.size(); i++) {
                //Log.e("THE PICTURE==>", optionHolder.get(i).get("picture"));
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("questionid", optionHolder.get(i).get("id")));
                nameValuePairs.add(new BasicNameValuePair("answer", optionHolder.get(i).get("answer")));
                nameValuePairs.add(new BasicNameValuePair("username", optionHolder.get(i).get("username")));
                nameValuePairs.add(new BasicNameValuePair("gps", optionHolder.get(i).get("gps")));
                //nameValuePairs.add(new BasicNameValuePair("gps", "" + locationLocator.latitude + " & " + locationLocator.longitude));
                if (optionHolder.get(i).get("picture") != null) {
                    nameValuePairs.add(new BasicNameValuePair("picture", optionHolder.get(i).get("picture")));
                }
                nameValuePairs.add(new BasicNameValuePair("outletid", optionHolder.get(i).get("outletid")));
                //nameValuePairs.add(new BasicNameValuePair("image", optionHolder.get(i).get("picture")));
                //nameValuePairs.add(new BasicNameValuePair("name", ""+optionHolder.get(i).get("name")));
                question_answer_url = "http://www.nbappserver.com/nbpage/getanswers.php";
                //question_answer_url = "http://populationassociationng.org/nbpage/getanswers.php";
                try {
                    total += i;
                    publishProgress("" + (int) ((total * 100) / optionHolder.size()));

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(question_answer_url);
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    StatusLine statusLine = response.getStatusLine();
                    st = EntityUtils.toString(response.getEntity());
                } catch (Exception e) {
                    Log.v("ERROR_OCCURRED", "Error in http connection " + e.toString());
                }
            }
            Log.e("VALUE_OF_ST", st);
            st = st.replace("\"", "");
            //if (st.equalsIgnoreCase("Success")){
            result = st.equalsIgnoreCase("Success") ? "Success" : "Something went wrong";
            //}

            return result;
        }

        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Submitting Answer(s)\nPlease wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            if (progressDialog.isShowing()) progressDialog.dismiss();
            if (progressDialog != null) progressDialog = null;
            Log.e("Response", s);
            if (s.equalsIgnoreCase("Success")) {
                alert.showAlertDialogForSavedQuestion(MvoQuestionnaireActivity.this, "Your question has been saved successfully!", "MVO Questionnaire", R.mipmap.nb_launcher);
            }
            //Process.killProcess(Process.myPid());
        }
    }

    private class SubmitSubQuestion extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog = new ProgressDialog(MvoQuestionnaireActivity.this);

        @Override
        protected String doInBackground(String... strings) {
            String result = "", st = "";
            String sub_question_answer_url;

            if (subQuest.subQuestOptHolder.size() != 0) {

                for (int i = 0; i < subQuest.subQuestOptHolder.size(); i++) {
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
                    nameValuePairs.add(new BasicNameValuePair("questionid", subQuest.subQuestOptHolder.get(i).get("questionid")));
                    nameValuePairs.add(new BasicNameValuePair("subquestionid", subQuest.subQuestOptHolder.get(i).get("subquestionid")));
                    nameValuePairs.add(new BasicNameValuePair("answer", subQuest.subQuestOptHolder.get(i).get("answer")));
                    nameValuePairs.add(new BasicNameValuePair("outletid", subQuest.subQuestOptHolder.get(i).get("outletid")));
                    sub_question_answer_url = "http://www.nbappserver.com/nbpage/subquestionanswers.php";
                    try {
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost httppost = new HttpPost(sub_question_answer_url);
                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                        HttpResponse response = httpclient.execute(httppost);
                        StatusLine statusLine = response.getStatusLine();
                        st = EntityUtils.toString(response.getEntity());
                    } catch (Exception e) {
                        Log.v("ERROR_OCCURRED", "Error in http connection " + e.toString());
                    }
                }
            }

            for (int i = 0; i < subQuestOptionHolder.size(); i++) {
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("questionid", subQuestOptionHolder.get(i).get("questionid")));
                nameValuePairs.add(new BasicNameValuePair("subquestionid", subQuestOptionHolder.get(i).get("subquestionid")));
                nameValuePairs.add(new BasicNameValuePair("answer", subQuestOptionHolder.get(i).get("answer")));
                nameValuePairs.add(new BasicNameValuePair("outletid", subQuestOptionHolder.get(i).get("outletid")));
                sub_question_answer_url = "http://www.nbappserver.com/nbpage/subquestionanswers.php";
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(sub_question_answer_url);
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    StatusLine statusLine = response.getStatusLine();
                    st = EntityUtils.toString(response.getEntity());
                } catch (Exception e) {
                    Log.v("ERROR_OCCURRED", "Error in http connection " + e.toString());
                }
            }
            //Log.e("SubQuestion_Response", st);
            st = st.replace("\"", "");
            result = st.equalsIgnoreCase("Success") ? "Success" : st;

            return result;
        }

        @Override
        protected void onPreExecute() {
            //System.out.println("SizeOfSubQuestionAnswer: "+subQuest.subQuestOptionHolder.size());
            progressDialog.setMessage("Submitting Sub Question Answers\nPlease wait...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            Log.i("Response From SubQuest", s);
            /*if (progressDialog.isShowing()) progressDialog.dismiss();
            Log.i("Response", s);
            if (s.equalsIgnoreCase("Success")) {
                alert.showAlertDialog(MvoQuestionnaireActivity.this, "Your question has been saved successfully!", "MVO Questionnaire", R.mipmap.nb_launcher);
                Process.killProcess(Process.myPid());
            }*/
        }
    }
}
