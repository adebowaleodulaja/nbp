package globalsoft.com.dialoginterface;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by adebowale.odulaja on 7/7/16.
 */
public class AlertDialogBuilder {

    public AlertDialog.Builder builder;


    public void showAlertDialog(Context context, String message, String title, int icon) {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        builder = new AlertDialog.Builder(context);
        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(message)
                .setTitle(title)
                .setIcon(icon);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        // 3. Get the AlertDialog from create()
        //android.support.v7.app.AlertDialog dialog = builder.create();
        builder.show();
    }

    public void showAlertDialogForSavedQuestion(Context context, String message, String title, int icon) {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        builder = new AlertDialog.Builder(context);
        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(message)
                .setTitle(title)
                .setIcon(icon);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                Process.killProcess(Process.myPid());
            }
        });
        builder.setCancelable(false);
        // 3. Get the AlertDialog from create()
        //android.support.v7.app.AlertDialog dialog = builder.create();
        builder.show();
    }

    public void showAlertDialogWithOption(Context context, String message, String title, int icon) {
        builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title)
                .setIcon(icon);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
              //  finish();
                Process.killProcess(Process.myPid());
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    // 1. Instantiate an AlertDialog.Builder with its constructor
                /*AlertDialog.Builder builder = new AlertDialog.Builder(context);
                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage("Wrong username and/or password")
                        .setTitle("Invalid Login")
                .setIcon(R.mipmap.alerticon);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                // 3. Get the AlertDialog from create()
                AlertDialog dialog = builder.create();
                dialog.show();*/



}
