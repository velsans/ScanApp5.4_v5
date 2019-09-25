package com.zebra.main.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zebra.R;
import com.zebra.database.InternalDataBaseHelperClass;
import com.zebra.utilities.AlertDialogManager;
import com.zebra.utilities.Common;
import com.zebra.utilities.ConnectionFinder;
import com.zebra.utilities.DeviceIMEIClass;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "Login";
    TextView SignIN_img, VersionCode;
    EditText Userid_etxt, Password_etxt;
    ProgressBar LoginProgress;
    InternalDataBaseHelperClass mDBInternalHelper;
    AlertDialogManager alert = new AlertDialogManager();
    AlertDialog.Builder UpdateApk_alertDialogBuilder;
    AlertDialog UpdateApk_alertDialog;
    boolean UpdateDialogFlag = false;
    ProgressDialog progress_bar;
    boolean isInternetPresent;
    DeviceIMEIClass device_imei;
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try {
            PackageInfo pInfo = LoginActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
            Common.VersionName = pInfo.versionName;
            Common.VersionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (Common.VersionCode == 1) {
            Common.DATABASE_VERSION = 1;
        } else if (Common.VersionCode == 2) {
            Common.DATABASE_VERSION = 2;
        } else if (Common.VersionCode == 3) {
            Common.DATABASE_VERSION = 3;
        } else if (Common.VersionCode == 4) {
            if (Common.VersionName.equals("4.6")) {
                Common.DATABASE_VERSION = 5;
            }
            if (Common.VersionName.equals("4.7")) {
                Common.DATABASE_VERSION = 6;
            }
            if (Common.VersionName.equals("4.8")) {
                Common.DATABASE_VERSION = 5;
            }
            if (Common.VersionName.equals("4.9")) {
                Common.DATABASE_VERSION = 6;
            }
        } else if (Common.VersionCode == 5) {
            if (Common.VersionName.equals("5.0")) {
                Common.DATABASE_VERSION = 7;
            }
            if (Common.VersionName.equals("5.1")) {
                Common.DATABASE_VERSION = 8;
            }
            if (Common.VersionName.equals("5.2")) {
                Common.DATABASE_VERSION = 9;
            }
            if (Common.VersionName.equals("5.3")) {
                Common.DATABASE_VERSION = 10;
            }
            if (Common.VersionName.equals("5.4")) {
                Common.DATABASE_VERSION = 11;
            }
        }

        Intialization();
        VersionCode.setText("Version Code : " + String.valueOf(Common.VersionCode) + " , Version Name : " + Common.VersionName);
        SignIN_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    Snackbar.make(findViewById(R.id.snakebarview), "Permission already granted.", Snackbar.LENGTH_LONG).show();
                    LoginMain();
                } else {
                    Snackbar.make(findViewById(R.id.snakebarview), "Checking all permissions...", Snackbar.LENGTH_LONG).show();
                    requestPermission();
                    return;
                }
            }
        });
        Password_etxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String input;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (checkPermission()) {
                        Snackbar.make(findViewById(R.id.snakebarview), "Permission already granted.", Snackbar.LENGTH_LONG).show();
                        LoginMain();
                    } else {
                        Snackbar.make(findViewById(R.id.snakebarview), "Please request permission.", Snackbar.LENGTH_LONG).show();
                        requestPermission();
                    }
                }
                return false; // pass on to other listeners.
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LoginProgress.setVisibility(View.GONE);
            }
        }, Common.SPLASH_TIME_OUT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    public void LoginMain() {
        try {
            mDBInternalHelper = new InternalDataBaseHelperClass(LoginActivity.this);
            //Get Device IMEI Number for Mac Address Feild
            device_imei = new DeviceIMEIClass(LoginActivity.this);
            Common.IMEI = device_imei.GetDeviceIMEI();
            if (isNullOrEmpty(Common.IMEI)) {
                AlertDialogBox("Device IMEI", "Device IMEI not found, please contact adminstrator ", false);
                return;
            }
        } catch (Exception e) {
            Log.e("Exception", ">>>" + e.toString());
        }

        if (UserCredentialValidation(Userid_etxt.getText().toString(), Password_etxt.getText().toString())) {
            Common.Username = Userid_etxt.getText().toString();
            Common.Password = Password_etxt.getText().toString();
            String PasswordUser;
            try {
                if (Common.Username.equals("1")) {
                    Common.UserID = 1;
                } else {
                    Common.UserID = 2;
                }
                Common.LoginDetailsList.clear();
                Common.LoginDetailsList = mDBInternalHelper.getLoginAuthentication(Common.Username);
                if (Common.LoginDetailsList.size() > 0) {
                    if (Common.Username.equals("1")) {
                        PasswordUser = "5432";
                    } else {
                        PasswordUser = Common.LoginDetailsList.get(0).getPassword();
                    }
                    if (PasswordUser.equals(Common.Password)) {
                        Toast.makeText(LoginActivity.this, CommonMessage(R.string.oldloginMsg), Toast.LENGTH_SHORT).show();
                        //AlertDialogBox(CommonMessage(R.string.LoginHD), CommonMessage(R.string.oldloginMsg), true);
                        Intent oneIntent = new Intent(LoginActivity.this, InventoryActivity.class);
                        startActivity(oneIntent);
                    } else {
                        Password_etxt.setError("Password incorrect");
                        AlertDialogBox(CommonMessage(R.string.LoginHD), "Password incorrect", false);
                    }
                } else {
                    RegisterAlertDialog("Are you sure want to register this UserID");
                }

            } catch (Exception ex) {
                Log.v("Logindetails", "Retrive Exception: " + ex.toString());
            }
        }
    }

    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }

    public void Intialization() {
        SignIN_img = findViewById(R.id.signin_txt);
        Userid_etxt = findViewById(R.id.usernamne_txt);
        Password_etxt = findViewById(R.id.password_txt);
        LoginProgress = findViewById(R.id.progressbar_login);
        LoginProgress.setVisibility(View.VISIBLE);
        VersionCode = findViewById(R.id.versionCode_txt);
    }

    public boolean UserCredentialValidation(String UserName, String UserPassword) {
        if (UserName.equals("")) {
            AlertDialogBox(CommonMessage(R.string.LoginHD), CommonMessage(R.string.UserNameMsg), false);
            Userid_etxt.requestFocus();
            return false;
        } else if (UserPassword.equals("")) {
            AlertDialogBox(CommonMessage(R.string.LoginHD), CommonMessage(R.string.PassMsg), false);
            Password_etxt.requestFocus();
            return false;
        }
        return true;
    }

    public void AlertDialogBox(String Title, String Message, boolean YesNo) {
        if (Common.AlertDialogVisibleFlag == true) {
            alert.showAlertDialog(LoginActivity.this, Title, Message, YesNo);
        } else {
            //do something here... if already showing
        }
    }

    public String CommonMessage(int Common_Msg) {
        return LoginActivity.this.getResources().getString(Common_Msg);
    }

    public void RegisterAlertDialog(String ErrorMessage) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
        builder1.setMessage(ErrorMessage);
        builder1.setCancelable(true);
        builder1.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder1.setNegativeButton("Register",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mDBInternalHelper.insertLoginDetails(Common.Username, Common.Password, Common.IMEI);
                        Intent oneIntent = new Intent(LoginActivity.this, InventoryActivity.class);
                        startActivity(oneIntent);
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    // Update New Version Yoko Yepi-------------------------------
    public void ApplicationDetails() {
        try {
            if (!CheckisInternetPresent()) {
                AlertDialogBox(CommonMessage(R.string.Internet_Conn), CommonMessage(R.string.Internet_ConnMsg), false);
            } else {
                //new getPosAPKVersions().execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* public class getPosAPKVersions extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String ControllerName = "Pos/getPosAPKVersions";
            String getPosAPKVersionsURL = ServiceURL.getServiceURL(ControllerName);
            getApkPojo.setVersionCode(String.valueOf(Common.VersionCode));
            getApkPojo.setVersionName(String.valueOf(Common.VersionName));

            try {
                String getPosAPKVersionsValues = new Communicator().POST_ObectClass(getPosAPKVersionsURL, getApkPojo);
                if (getPosAPKVersionsValues != null) {
                    try {
                        jsonObj = new JSONObject(getPosAPKVersionsValues);
                        String UpdatedApk_details = jsonObj.getString("m_Item1");
                        if (UpdatedApk_details != null && !UpdatedApk_details.isEmpty()) {
                            if (UpdatedApk_details.equals("null")) {
                                Common.IsConnected = false;
                            } else {
                                getApkPojo = new Gson().fromJson(UpdatedApk_details, GetPosAPKversionPojo.class);
                                Common.GetAppVersionDetails.add(getApkPojo);
                                Common.IsConnected = true;
                            }
                        }
                    } catch (Exception e) {
                        ErrorMsg = e.toString();
                        Common.IsConnected = false;
                    }
                } else {
                    ErrorMsg = "";
                    Common.IsConnected = true;
                }
            } catch (NullPointerException e) {
                Common.IsConnected = false;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Common.GetAppVersionDetails.clear();
            progressbar_layout.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressbar_layout.setVisibility(View.GONE);
                    try {
                        if (Common.IsConnected) {
                            if (Common.GetAppVersionDetails.size() > 0) {
                                getApkPojo = Common.GetAppVersionDetails.get(0);
                                boolean IsNeedUpdated = getApkPojo.getUpdateNeeded();
                                if (IsNeedUpdated == true) {
                                    Common.AppUpdateURL = getApkPojo.getUrl().toString();
                                    Common.VersionCodefromWebApi = Integer.parseInt(getApkPojo.getVersionCode());
                                    Common.VersionNamefromWebApi = getApkPojo.getVersionName().toString();
                                    if (Common.VersionCode < Common.VersionCodefromWebApi) {
                                        if (UpdateDialogFlag == false) {
                                            AlertDialogUpdate();
                                            stoptimertask();
                                        }
                                    }
                                } else {
                                    //showAToast("No Updates");
                                }
                            }
                        }
                    } catch (Exception e) {
                        showAToast(e.toString());
                    }
                }
            });
        }
    }*/

    // Update the Apk Dialog------
    public void AlertDialogUpdate() {
        UpdateApk_alertDialogBuilder = new AlertDialog.Builder(this);
        UpdateApk_alertDialog = UpdateApk_alertDialogBuilder.create();
        UpdateApk_alertDialog.setCancelable(false);
        UpdateApk_alertDialog.setTitle(getResources().getString(R.string.AppUpdateTitle));
        UpdateApk_alertDialog.setMessage("YokoYepi Version " + Common.VersionNamefromWebApi + " available.");
        UpdateApk_alertDialog.setButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UpdateDialogFlag = false;
                dialog.dismiss();
                new DownloadNewVersion().execute();
            }
        });
        if (!UpdateApk_alertDialog.isShowing()) {
            UpdateDialogFlag = true;
            UpdateApk_alertDialog.show();
        }
    }

    class DownloadNewVersion extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress_bar = new ProgressDialog(LoginActivity.this);
            progress_bar.setCancelable(false);
            progress_bar.setMessage("Downloading...");
            progress_bar.setIndeterminate(true);
            progress_bar.setCanceledOnTouchOutside(false);
            progress_bar.show();
        }

        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            progress_bar.setIndeterminate(false);
            progress_bar.setMax(100);
            progress_bar.setProgress(progress[0]);
            String msg = "";
            if (progress[0] > 99) {
                msg = "Finishing... ";
            } else {
                msg = "Downloading... " + progress[0] + "%";
            }
            progress_bar.setMessage(msg);
        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            Boolean flag = false;
            try {
                String PATH;
                Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
                if (isSDPresent) {
                    PATH = Environment.getExternalStorageDirectory() + "/Download/";
                } else {
                    PATH = Environment.getDataDirectory() + "/Download/";
                }
                File file = new File(PATH);
                file.mkdirs();
                File outputFile = new File(file, "yokoyepi.apk");
                if (outputFile.exists()) {
                    outputFile.delete();
                }
                // Download File from url
                URL u = new URL(Common.AppUpdateURL);
                URLConnection conn = u.openConnection();
                int contentLength = conn.getContentLength();

                DataInputStream stream = new DataInputStream(u.openStream());

                byte[] buffer = new byte[contentLength];
                stream.readFully(buffer);
                stream.close();

                DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
                fos.write(buffer);
                fos.flush();
                fos.close();
                // Install dowloaded Apk file from Devive----------------
                OpenNewVersion(PATH);
                //Uninstall Previous Application------------------------
                //UnInstallApplication(Common.AppPackageName);
                flag = true;
            } catch (MalformedURLException e) {
                flag = false;
            } catch (IOException e) {
                flag = false;
            } catch (Exception e) {
                flag = false;
            }
            return flag;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            //startTimer();
            progress_bar.dismiss();
            if (result) {
                Toast.makeText(getApplicationContext(), "Download Done",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error: Try Again",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    void OpenNewVersion(String location) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(location + "yokoyepi.apk")), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //ApplicationDetails();
    }

    public boolean CheckisInternetPresent() {
        try {
            isInternetPresent = ConnectionFinder.isInternetOn(getBaseContext());
        } catch (Exception ex) {
            //AlertDialogBox("Internet Connection", ex.toString(), false);
        }
        if (!isInternetPresent) {
            Snackbar.make(findViewById(R.id.snakebarview), CommonMessage(R.string.Internet_ConnMsg), Snackbar.LENGTH_LONG).show();
            AlertDialogBox(CommonMessage(R.string.Internet_Conn), CommonMessage(R.string.Internet_ConnMsg), false);
            return false;
        } else {
            return true;
        }
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str == "null";
    }

    /*Check Permission*/
    private boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        return result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean readAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean writeAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean PhoneAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && readAccepted && writeAccepted && PhoneAccepted) {
                        Snackbar.make(findViewById(R.id.snakebarview), "Permission Granted, Now you can access Storage data and camera.", Snackbar.LENGTH_LONG).show();
                        LoginMain();
                    } else {
                        Snackbar.make(findViewById(R.id.snakebarview), "Permission Denied, You cannot access Storage data and camera.", Snackbar.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, READ_PHONE_STATE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }
                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(LoginActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
