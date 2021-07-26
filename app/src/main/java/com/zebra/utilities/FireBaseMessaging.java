package com.zebra.utilities;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zebra.R;

public class FireBaseMessaging extends FirebaseMessagingService {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMessageReceived(RemoteMessage rMsg) {
        sendNotification(rMsg.getNotification());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void sendNotification(RemoteMessage.Notification rNotfy) {
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Intent rintent = new Intent(this, LoginActivity.class);
        //final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, rintent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(rNotfy.getTitle())
                .setContentText(rNotfy.getBody())
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(0x169AB9)
                .setSound(soundUri);
        //.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
}
