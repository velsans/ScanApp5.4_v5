package com.zebra.utilities;

import android.app.DownloadManager;
import android.app.IntentService;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.zebra.R;
import com.zebra.main.activity.Common.GwwMainActivity;
import com.zebra.main.firebase.CrashAnalytics;

import java.io.File;

/**
 * Created by Velmurugan on 1/27/2021.
 */
public class DownloadExternalDBService extends IntentService {
    public static final String EXTRA_URL = "ExternalDB_URL";
    private DownloadManager downloadManager;
    private long mydownloadReference;
    private BroadcastReceiver receiverDownloadComplete;
    private BroadcastReceiver receiverNotificationClicked;
    long DownloadID;

    public DownloadExternalDBService() {
        super("DownloadSongService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String fileName = "GWW.db";
            File saving_locationFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/GWW");
            if (!saving_locationFile.exists()) {
                saving_locationFile.mkdir();
            } else {
                File delete_locationFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/GWW/" + fileName);
                boolean deleted = delete_locationFile.delete();
                Log.v("log_tag", "deleted: " + deleted);
            }
            try {
                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                final String download_url = intent.getStringExtra(EXTRA_URL);
                Uri uri = Uri.parse(download_url + fileName);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                request.setTitle("External database download");
                request.setDescription("Downloading external database");
                //Setting the location to which the file is to be downloaded
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir("/GWW", fileName);

                DownloadID = downloadManager.enqueue(request);
                IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                registerReceiver(receiver, filter);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Long DownloadedID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (DownloadedID == DownloadID) {
                    if (getDownloadedStatus() == DownloadManager.STATUS_SUCCESSFUL) {
                        Toast.makeText(context, "Download Completed", Toast.LENGTH_LONG).show();
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                                .setSmallIcon(R.mipmap.applogo)
                                .setContentTitle("Downloading: Arise")
                                .setContentText("Podington Bear - Arise");

                        Intent resultIntent = new Intent(context, GwwMainActivity.class);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(GwwMainActivity.class);
                    } else {
                        Toast.makeText(context, "Download Not Completed", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception ex) {
                CrashAnalytics.CrashReport(ex);
            }
        }
    };

    public int getDownloadedStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(DownloadID);
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        Cursor cursor = downloadManager.query(query);

        if (cursor.moveToFirst()) {
            int columnOndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int status = cursor.getInt(columnOndex);
            return status;
        }
        return DownloadManager.ERROR_UNKNOWN;
    }

    public void CancelDownload() {
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadManager.remove(DownloadID);
    }


}