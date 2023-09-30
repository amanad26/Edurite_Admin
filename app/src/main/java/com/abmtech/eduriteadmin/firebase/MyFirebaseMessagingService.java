package com.abmtech.eduriteadmin.firebase;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.abmtech.eduriteadmin.R;
import com.abmtech.eduriteadmin.Session.Session;
import com.abmtech.eduriteadmin.activities.DashboardActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    Session sessionManager;
    Activity activity;
    private String nid, title, message, imageUrl, linkUrl, discussid, cat_type, cat_id, post, type;
    private int notifyid = new Random().nextInt();

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        sessionManager = new Session(this);


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());

            type = remoteMessage.getData().get("type");
            title = remoteMessage.getData().get("title");
            message = remoteMessage.getData().get("message");
            linkUrl = remoteMessage.getData().get("url");
            imageUrl = remoteMessage.getData().get("image");
            discussid = remoteMessage.getData().get("discussid");
            cat_type = remoteMessage.getData().get("cat_type");
            cat_id = remoteMessage.getData().get("cat_id");
            post = remoteMessage.getData().get("post_id");
            nid = remoteMessage.getData().get("nid");
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            message = remoteMessage.getNotification().getBody();
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification());
            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
        }

        if (title.equalsIgnoreCase("Call Request Notification")) {
            try {
                String channel_name = remoteMessage.getData().get("channel_name");
                String token = remoteMessage.getData().get("token");
                String app_id = remoteMessage.getData().get("app_id");
                String remainingTime = remoteMessage.getData().get("remainingTime");
                String dailyTime = remoteMessage.getData().get("dailyTime");
                String advertTime = remoteMessage.getData().get("advertTime");
                String my_remainingTime = remoteMessage.getData().get("my_remainingTime");
                String my_dailyTime = remoteMessage.getData().get("my_dailyTime");
                String my_advertTime = remoteMessage.getData().get("my_advertTime");
                String my_token = remoteMessage.getData().get("my_token");

                Log.e(TAG, "onMessageReceived() called with: channel_name = [" + channel_name + "]");
                Log.e(TAG, "onMessageReceived() called with: token = [" + token + "]");
                Log.e(TAG, "onMessageReceived() called with: app_id = [" + app_id + "]");

//
//                Intent intent = new Intent(getApplicationContext(), CallDialogPopupActivity.class)
//                        .putExtra("channel_name", channel_name)
//                        .putExtra("token", token)
//                        .putExtra("remainingTime", remainingTime)
//                        .putExtra("dailyTime", dailyTime)
//                        .putExtra("advertTime", advertTime)
//                        .putExtra("my_remainingTime", my_remainingTime)
//                        .putExtra("my_dailyTime", my_dailyTime)
//                        .putExtra("my_advertTime", my_advertTime)
//                        .putExtra("my_token", my_token)
//                        .putExtra("app_id", app_id);
//
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

 //               startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (title.equalsIgnoreCase("Call Reply Accepted")) {
            String channel_name = remoteMessage.getData().get("channel_name");
            String token = remoteMessage.getData().get("token");
            String app_id = remoteMessage.getData().get("app_id");
            String remainingTime = remoteMessage.getData().get("remainingTime");
            String dailyTime = remoteMessage.getData().get("dailyTime");
            String advertTime = remoteMessage.getData().get("advertTime");
//
//            Intent intent = new Intent(getApplicationContext(), CallConnectingActivity.class)
//                    .putExtra("channel_name", channel_name)
//                    .putExtra("token", token)
//                    .putExtra("remainingTime", (remainingTime))
//                    .putExtra("dailyTime", (dailyTime))
//                    .putExtra("advertTime", (advertTime))
//                    .putExtra("app_id", app_id);
//
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            startActivity(intent);
        }
        else if (imageUrl != null) {
            Bitmap bitmap = getBitmapFromUrl(imageUrl);
            sendNotification(nid, title, message, bitmap, linkUrl, discussid, post, cat_type, cat_id, type);
        }
        else {
            sendNotification(nid, title, message, null, linkUrl, discussid, post, cat_type, cat_id, type);
        }
    }

    private void handleNotification(String title, String body, String imageUrl) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent resultIntent = new Intent(getApplicationContext(), DashboardActivity.class);
            SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
            String format = s.format(new Date());

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent("english_speaking_app");
                pushNotification.putExtra("message", body);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.showNotificationMessage(title, body, "", resultIntent, imageUrl);
                notificationUtils.playNotificationSound();
            }
        }

    }

    @Override
    public void onNewToken(String token) {
        Log.e(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    private void sendNotification(String nid, String title, String message, Bitmap image, String link,
                                  String discussId, String post_id, String cat_type, String cat_id, String type) {
        notifyid++;
        Intent intent;
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        if (link != null && !link.equals("")) {
            stackBuilder.addNextIntentWithParentStack(new Intent(this, DashboardActivity.class));
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(link));
            stackBuilder.addNextIntent(intent);
        } else if (discussId != null && !discussId.equals("") && !discussId.equals("0")) {
            stackBuilder.addNextIntentWithParentStack(new Intent(this, DashboardActivity.class));
            Intent activityB = new Intent(this, DashboardActivity.class);
            activityB.putExtra("discussid", discussId);
            activityB.putExtra("from_n", "notification");
            activityB.putExtra("nid", nid);
            stackBuilder.addNextIntent(activityB);
        } else {


            stackBuilder.addNextIntentWithParentStack(new Intent(this, DashboardActivity.class));
            Intent activityB = new Intent(this, DashboardActivity.class);
            activityB.putExtra("from_n", "notification");
            stackBuilder.addNextIntent(activityB);

//            if (sessionManager.isLoggedIn()) {
//                if (post_id != null && !post_id.equals("") && !post_id.equals("0")) {
//                    Log.e(TAG, "Message data payload: " + post_id);
//                    //post
//                    stackBuilder.addNextIntentWithParentStack(new Intent(this, HomeActivity.class));
//                    Intent activityB = new Intent(this, PostDetailActivity.class);
//                    activityB.putExtra("post_id", post_id);
//                    activityB.putExtra("from_n", "notification");
//
//                    stackBuilder.addNextIntent(activityB);
//                } else if (cat_type != null && !cat_type.equals("") && !cat_type.equals("0") &&
//                        cat_id != null && !cat_id.equals("") && !cat_id.equals("0")) {
//                    Log.e(TAG, "Message data payload: " + post_id);
//                    //post
//                    stackBuilder.addNextIntentWithParentStack(new Intent(this, HomeActivity.class));
//                    Intent activityB = new Intent(this, CategorySActivity.class);
//                    activityB.putExtra("cat_type", cat_type);
//                    activityB.putExtra("cat_id", cat_id);
//                    activityB.putExtra("type", type);
//                    activityB.putExtra("from_n", "notification");
//
//                    stackBuilder.addNextIntent(activityB);
//                }
//            } else {
//                stackBuilder.addNextIntentWithParentStack(new Intent(this, HomeActivity.class));
//                Intent activityB = new Intent(this, HomeActivity.class);
//                activityB.putExtra("from_n", "notification");
//                stackBuilder.addNextIntent(activityB);
////                intent = new Intent(this, HomeActivity.class);
//            }
        }
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        String channelId = "nuptist_chanel";
        String channelName = "nuptist_chanel_name";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        if (image != null) {
            notificationBuilder
                    .setLargeIcon(image)
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image).bigLargeIcon(null));
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(notifyid /* Id of notification */, notificationBuilder.build());
    }

    public Bitmap getBitmapFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int color = 0x008000;
            notificationBuilder.setColor(color);
            return R.drawable.ic_logo;
        }
        return R.drawable.ic_logo;
    }
}