package com.zebra.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.zebra.R;

public class AlertDialogManager {
    AlertDialog alertDialog = null;
    AlertDialog.Builder builder1 = null;

    public void showAlertDialog(final Context context, String title, String message,
                                Boolean status) {
        if (alertDialog != null && alertDialog.isShowing()) {
            return;
        }
        alertDialog = new AlertDialog.Builder(context).create();
        // Setting Dialog Title
        alertDialog.setTitle(title);
        // Setting Dialog Message
        alertDialog.setMessage(message);
        if (status != null)
            // Setting alert dialog collector
            alertDialog.setIcon((status) ? R.mipmap.success : R.mipmap.fail);
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Common.AlertDialogVisibleFlag = true;
                if (Common.AuthorizationFlag == true) {
                    Common.AuthorizationFlag = false;
                }
            }
        });
        alertDialog.show();
    }

    public void KillProcessAlertMessage(Context context, String ErrorMessage) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(ErrorMessage);
        builder1.setCancelable(true);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        android.os.Process.killProcess(android.os.Process.myPid());

                    }
                });
    }


}
